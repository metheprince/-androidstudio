package com.ai.callattender;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class SpamDetector {
    private Set<String> spamNumbers;
    private Context context;
    private SharedPreferences preferences;

    public SpamDetector(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("AICallAttenderPrefs", Context.MODE_PRIVATE);
        spamNumbers = preferences.getStringSet("spamNumbers", new HashSet<>());
    }

    public boolean isSpam(String phoneNumber) {
        // Simple check against known spam numbers
        if (spamNumbers.contains(phoneNumber)) {
            return true;
        }

        // More sophisticated checks can be added here:
        // - Number pattern analysis
        // - Area code checks
        // - Recent call frequency

        return false;
    }

    public void addSpamNumber(String phoneNumber) {
        spamNumbers.add(phoneNumber);
        preferences.edit()
                .putStringSet("spamNumbers", spamNumbers)
                .apply();
    }

    public void removeSpamNumber(String phoneNumber) {
        spamNumbers.remove(phoneNumber);
        preferences.edit()
                .putStringSet("spamNumbers", spamNumbers)
                .apply();
    }
}