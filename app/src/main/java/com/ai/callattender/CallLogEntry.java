package com.ai.callattender;

import java.util.Date;

public class CallLogEntry {
    private final String phoneNumber;
    private final Date callTime;
    private final boolean isSpam;
    private final String callSummary;

    public CallLogEntry(String phoneNumber, Date callTime, boolean isSpam, String callSummary) {
        this.phoneNumber = phoneNumber;
        this.callTime = callTime;
        this.isSpam = isSpam;
        this.callSummary = callSummary;

    }
    // Getters and setters
    public String getPhoneNumber() { return phoneNumber; }
    public Date getCallTime() { return callTime; }
    public boolean isSpam() { return isSpam; }
    public String getCallSummary() { return callSummary; }
}
