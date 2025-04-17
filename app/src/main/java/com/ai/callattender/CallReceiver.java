package com.ai.callattender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import java.util.Date;

public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            // Save it to the DB
            if (incomingNumber != null && !incomingNumber.isEmpty()) {
                try (CallLogDBHelper dbHelper = new CallLogDBHelper(context)) {
                    dbHelper.addCallLog(new CallLogEntry(
                            incomingNumber,
                            new Date(),
                            false,
                            "Incoming call"
                    ));
                }}
        }
    }
}