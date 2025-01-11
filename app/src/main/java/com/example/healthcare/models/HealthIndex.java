package com.example.healthcare.models;

import java.util.List;

public class HealthIndex {
    private List<BloodPressure> bloodPressures;
    private List<BloodSugar> bloodSugars;
    private List<HeartRate> heartRates;
    private List<Sleep> sleeps;
    private List<Calor> calories;
    public HealthIndex() {
    }

    public List<BloodPressure> getBloodPressures() {
        return bloodPressures;
    }

    public void setBloodPressures(List<BloodPressure> bloodPressures) {
        this.bloodPressures = bloodPressures;
    }

    public List<BloodSugar> getBloodSugars() {
        return bloodSugars;
    }

    public void setBloodSugars(List<BloodSugar> bloodSugars) {
        this.bloodSugars = bloodSugars;
    }

    public List<HeartRate> getHeartRates() {
        return heartRates;
    }

    public void setHeartRates(List<HeartRate> heartRates) {
        this.heartRates = heartRates;
    }

    public List<Sleep> getSleeps() {
        return sleeps;
    }

    public void setSleeps(List<Sleep> sleeps) {
        this.sleeps = sleeps;
    }

    public List<Calor> getCalories() {
        return calories;
    }

    public void setCalories(List<Calor> calories) {
        this.calories = calories;
    }
}
