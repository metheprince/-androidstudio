package com.ai.callattender;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIResponseGenerator {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "\n" + " "; // Replace with your actual API key
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private Context context;
    private OkHttpClient client;
    private SharedPreferences preferences;

    public AIResponseGenerator(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences("AICallAttenderPrefs", Context.MODE_PRIVATE);
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void generateResponse(String callerQuery, final AICallback callback) {
        String prompt = "You are an AI call assistant. The caller said: \"" + callerQuery +
                "\". Please respond appropriately in a professional and helpful manner. " +
                "Keep the response concise (1-2 sentences).";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");

            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);

            jsonBody.put("messages", new JSONObject[]{message});
            jsonBody.put("temperature", 0.7);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onFailure("Error creating request");
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("AIResponse", "API call failed", e);
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("Unexpected code " + response);
                    return;
                }

                try {
                    String responseData = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseData);
                    String aiResponse = jsonResponse.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");

                    callback.onSuccess(aiResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFailure("Error parsing response");
                }
            }
        });
    }

    public interface AICallback {
        void onSuccess(String response);
        void onFailure(String error);
    }
}
