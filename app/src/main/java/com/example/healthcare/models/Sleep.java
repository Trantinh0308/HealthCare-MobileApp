package com.example.healthcare.models;

import com.google.firebase.Timestamp;

public class Sleep {
    private float totalTime;
    private long timestamp;

    public Sleep() {
    }

    public float getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(float totalTime) {
        this.totalTime = totalTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
