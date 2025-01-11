package com.example.healthcare.models;

import com.google.firebase.Timestamp;

public class BloodSugar {
    private float quantity;
    private long timestamp;

    public BloodSugar() {
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
