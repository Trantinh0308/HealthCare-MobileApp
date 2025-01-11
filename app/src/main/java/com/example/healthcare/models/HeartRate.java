package com.example.healthcare.models;

import com.google.firebase.Timestamp;

public class HeartRate {
    private float quantity;
    private long timestamp;

    public HeartRate() {
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
