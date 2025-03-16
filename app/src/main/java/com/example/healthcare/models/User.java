package com.example.healthcare.models;

import com.example.healthcare.activities.patient.ScheduleActivity;
import com.google.firebase.Timestamp;

import java.util.List;

public class User {
    private String fullName;
    private String gender;
    private String phoneNumber;
    private String address;
    private String birth;
    private int height;
    private int weight;
    private String image;
    private long createdTime;
    private List<User> relativeList;
    private String relative;
    private String userId;
    public User() {
    }

    public User(String fullName, String gender, String phoneNumber, String address, String birth,
                int height, int weight, String image, long createdTime, List<User> relativeList, String relative, String userId) {
        this.fullName = fullName;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.birth = birth;
        this.height = height;
        this.weight = weight;
        this.image = image;
        this.createdTime = createdTime;
        this.relativeList = relativeList;
        this.relative = relative;
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<User> getRelativeList() {
        return relativeList;
    }

    public void setRelativeList(List<User> relativeList) {
        this.relativeList = relativeList;
    }

    public String getRelative() {
        return relative;
    }

    public void setRelative(String relative) {
        this.relative = relative;
    }
}
