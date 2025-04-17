package com.ai.callattender;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.util.Date;


/** @noinspection ALL*/
public class CallReceiverService extends Service {
    private CallHandler callHandler;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;

    @Override
    public void onCreate() {
        super.onCreate();
        callHandler = new CallHandler(this);
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);

                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        // Incoming call
                        Toast.makeText(CallReceiverService.this, "Incoming call from: " + phoneNumber, Toast.LENGTH_SHORT).show();
                        // Here we would implement the call answering logic
                        callHandler.handleIncomingCall(phoneNumber);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        // Call answered
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Call ended
                        callHandler.shutdown();
                        break;
                }

            }

        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

