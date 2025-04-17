package com.ai.callattender;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    // Add this constant
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PERMISSIONS_REQUEST_CODE = 1001;
    private Switch serviceToggle;
    private RadioGroup responseModeGroup;
    private EditText predefinedMessage;
    private SeekBar delaySeekBar;
    private TextView delayValue;
    private Button saveSettings, viewCallLogs;

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ANSWER_PHONE_CALLS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.RECORD_AUDIO
        };

        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        checkPermissions();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_CODE);
        }

        // Initialize views
        serviceToggle = findViewById(R.id.serviceToggle);
        responseModeGroup = findViewById(R.id.responseModeGroup);
        predefinedMessage = findViewById(R.id.predefinedMessage);
        delaySeekBar = findViewById(R.id.delaySeekBar);
        delayValue = findViewById(R.id.delayValue);
        saveSettings = findViewById(R.id.saveSettings);
        viewCallLogs = findViewById(R.id.viewCallLogs);


        // Set up listeners
        responseModeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.predefinedMode) {
                predefinedMessage.setVisibility(android.view.View.VISIBLE);
            } else {
                predefinedMessage.setVisibility(android.view.View.GONE);
            }

        });
        delaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                delayValue.setText(progress + " seconds");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        saveSettings.setOnClickListener(v -> saveSettings());
        Button viewCallLogs = findViewById(R.id.viewCallLogs);
        viewCallLogs.setOnClickListener(v -> {
            //  implemented later
            Intent intent = new Intent(MainActivity.this, CallLogsActivity.class);
            startActivity(intent);

            //Toast.makeText(this, "Call logs feature coming soon", Toast.LENGTH_SHORT).show();
            //it is also implemented


        });
    }
    // Add this method

    private void saveSettings() {
        boolean isEnabled = serviceToggle.isChecked();
        int responseMode = responseModeGroup.getCheckedRadioButtonId();
        String message = predefinedMessage.getText().toString();
        int delay = delaySeekBar.getProgress();

        // Save to SharedPreferences
        getSharedPreferences("AICallAttenderPrefs", MODE_PRIVATE)
                .edit()
                .putBoolean("isEnabled", isEnabled)
                .putInt("responseMode", responseMode)
                .putString("message", message)
                .putInt("delay", delay)
                .apply();
        // Start or stop service
        Intent serviceIntent = new Intent(this, CallReceiverService.class);
        if (isEnabled) {
            startService(serviceIntent);
        } else {
            stopService(serviceIntent);
        }

        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
    }

}
