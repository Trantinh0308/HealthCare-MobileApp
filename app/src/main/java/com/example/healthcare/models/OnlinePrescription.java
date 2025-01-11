package com.example.healthcare.models;

import java.util.List;

public class OnlinePrescription {
    private String drugName;
    private String image;
    private String unit;
    private int order;
    private String startDate;
    private String endDate;
    private String frequency;
    private String note;
    private Boolean remind;
    private List<PrescriptionDetail> prescriptionDetailList;
    public OnlinePrescription() {
    }

    public OnlinePrescription(String drugName, String image, String unit, int order,
                              String startDate, String endDate, String frequency, String note, Boolean remind) {
        this.drugName = drugName;
        this.image = image;
        this.unit = unit;
        this.order = order;
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
        this.note = note;
        this.remind = remind;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<PrescriptionDetail> getPrescriptionDetailList() {
        return prescriptionDetailList;
    }

    public void setPrescriptionDetailList(List<PrescriptionDetail> prescriptionDetailList) {
        this.prescriptionDetailList = prescriptionDetailList;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Boolean getRemind() {
        return remind;
    }

    public void setRemind(Boolean remind) {
        this.remind = remind;
    }
}
