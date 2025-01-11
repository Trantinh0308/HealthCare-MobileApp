package com.example.healthcare.models;

public class BloodPressure {
    private long systolic;
    private long diastolic;
    private long timestamp;

    public BloodPressure() {
    }

    public long getSystolic() {
        return systolic;
    }

    public void setSystolic(long systolic) {
        this.systolic = systolic;
    }

    public long getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(long diastolic) {
        this.diastolic = diastolic;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
