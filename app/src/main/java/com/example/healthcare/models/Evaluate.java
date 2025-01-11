package com.example.healthcare.models;

public class Evaluate {
    OnlineDoctor doctor;
    User user;
    String comment;
    int rating;
    String date;
    public Evaluate() {
    }

    public OnlineDoctor getDoctor() {
        return doctor;
    }

    public void setDoctor(OnlineDoctor doctor) {
        this.doctor = doctor;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
