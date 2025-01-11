package com.example.healthcare.models;

public class Role {
    private String userId;
    private int isRole;
    private boolean checkFace;

    public Role() {
    }

    public boolean isCheckFace() {
        return checkFace;
    }

    public void setCheckFace(boolean checkFace) {
        this.checkFace = checkFace;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getIsRole() {
        return isRole;
    }

    public void setIsRole(int isRole) {
        this.isRole = isRole;
    }

    @Override
    public String toString() {
        return "Role{" +
                "userId='" + userId + '\'' +
                ", isRole=" + isRole +
                '}';
    }
}
