package com.ai.callattender;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.Locale;

public class CallHandler {
    private final AIResponseGenerator aiResponseGenerator;
    private final SpamDetector spamDetector;
    private TextToSpeech textToSpeech;
    private final SharedPreferences preferences;

    public CallHandler(Context context) {
        preferences = context.getSharedPreferences("AICallAttenderPrefs", Context.MODE_PRIVATE);
        spamDetector = new SpamDetector(context);
        aiResponseGenerator = new AIResponseGenerator(context);
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.getDefault());
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });
    }
    // Add this method
    private void handleAIResponse(String callerQuery) {
        speakMessage("Please hold while I process your request.");

        aiResponseGenerator.generateResponse(callerQuery, new AIResponseGenerator.AICallback() {
            @Override
            public void onSuccess(String response) {
                speakMessage(response);
                // Here we could add logic to record the conversation
            }

            @Override
            public void onFailure(String error) {
                speakMessage("I'm having trouble understanding. Please try again later.");
                Log.e("AIResponse", error);
            }
        });
    }
    public void handleIncomingCall(String callerNumber) {
        if (!preferences.getBoolean("isEnabled", false)) {
            return;
        }
        if (spamDetector.isSpam(callerNumber)) {
            // For spam calls, we might want to reject immediately
            speakMessage("This call has been identified as spam and will be disconnected.");
            return;
        }
        int delay = preferences.getInt("delay", 15) * 1000; // Convert to milliseconds
        int responseMode = preferences.getInt("responseMode", R.id.predefinedMode);
        String message = preferences.getString("message", "The user is busy, please leave a message after the tone.");

        // Simulate answering after delay
        new android.os.Handler().postDelayed(() -> {
            if (responseMode == R.id.predefinedMode) {
                speakMessage(message);
            } else {
                // AI response mode - we'll implement this later
                speakMessage("Please hold while I connect you to an AI assistant.");
                //Added by myself
                handleAIResponse(callerNumber);
            }
        }, delay);
    }

    private void speakMessage(String message) {
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
