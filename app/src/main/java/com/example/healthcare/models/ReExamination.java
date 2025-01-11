package com.example.healthcare.models;

public class ReExamination {
    private User user;
    private String AppointmentDate;
    private String reAppointmentDate;
    private OnlineDoctor doctor;
    private int stateAppointment;
    public ReExamination() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAppointmentDate() {
        return AppointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        AppointmentDate = appointmentDate;
    }

    public String getReAppointmentDate() {
        return reAppointmentDate;
    }

    public void setReAppointmentDate(String reAppointmentDate) {
        this.reAppointmentDate = reAppointmentDate;
    }

    public OnlineDoctor getDoctor() {
        return doctor;
    }

    public void setDoctor(OnlineDoctor doctor) {
        this.doctor = doctor;
    }

    public int getStateAppointment() {
        return stateAppointment;
    }

    public void setStateAppointment(int stateAppointment) {
        this.stateAppointment = stateAppointment;
    }
}
