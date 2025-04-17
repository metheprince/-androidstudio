package com.ai.callattender;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CallLogsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CallLogDBHelper dbHelper = null;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_call_logs);
        ListView callLogsList = findViewById(R.id.callLogsList);
        dbHelper = new CallLogDBHelper(this);
        List<CallLogEntry> callLogs = dbHelper.getAllCallLogs();
        Toast.makeText(this, "Logs found: " + callLogs.size(), Toast.LENGTH_SHORT).show();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        String[] callLogStrings = new String[callLogs.size()];
        for (int i = 0; i < callLogs.size(); i++) {
            CallLogEntry call = callLogs.get(i);
            String spamTag = call.isSpam() ? "[SPAM] " : "";
            callLogStrings[i] = spamTag + call.getPhoneNumber() + "\n" +
                    dateFormat.format(call.getCallTime()) + "\n" +
                    call.getCallSummary();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                callLogStrings);

        callLogsList.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
    }

}