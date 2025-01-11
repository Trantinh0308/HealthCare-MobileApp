package com.example.healthcare.models;

import java.util.List;

public class AppointmentResult {
    private String appointmentId;
    private String symptom;
    private String diagnose;
    private String recommend;
    private List<OnlinePrescription> prescriptionList;
    public AppointmentResult() {
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public String getDiagnose() {
        return diagnose;
    }

    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public List<OnlinePrescription> getPrescriptionList() {
        return prescriptionList;
    }

    public void setPrescriptionList(List<OnlinePrescription> prescriptionList) {
        this.prescriptionList = prescriptionList;
    }
}
