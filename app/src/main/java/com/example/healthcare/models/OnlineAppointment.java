package com.example.healthcare.models;

public class OnlineAppointment {
    private String id;
    private User user;
    private String bookDate;
    private String appointmentDate;
    private OnlineDoctor doctor;
    private String time;
    private String symptom;
    private int stateAppointment;
    private int event ;

    public OnlineAppointment() {
        this.event = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBookDate() {
        return bookDate;
    }

    public void setBookDate(String bookDate) {
        this.bookDate = bookDate;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public OnlineDoctor getDoctor() {
        return doctor;
    }

    public void setDoctor(OnlineDoctor doctor) {
        this.doctor = doctor;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public int getStateAppointment() {
        return stateAppointment;
    }

    public void setStateAppointment(int stateAppointment) {
        this.stateAppointment = stateAppointment;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }
}
