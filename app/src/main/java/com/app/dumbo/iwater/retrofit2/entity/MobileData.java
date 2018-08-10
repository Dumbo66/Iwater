package com.app.dumbo.iwater.retrofit2.entity;

public class MobileData {
    private Double latBd09ll;

    private Double lngBd09ll;

    private String recordTime;

    private Float ph;

    private Float temperature;

    private Float dissolvedOxygen;

    private Float conductivity;

    private Float turbidity;

    private char grade;

    public Double getLatBd09ll() {
        return latBd09ll;
    }

    public void setLatBd09ll(Double latBd09ll) {
        this.latBd09ll = latBd09ll;
    }

    public Double getLngBd09ll() {
        return lngBd09ll;
    }

    public void setLngBd09ll(Double lngBd09ll) {
        this.lngBd09ll = lngBd09ll;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public Float getPh() {
        return ph;
    }

    public void setPh(Float ph) {
        this.ph = ph;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Float getDissolvedOxygen() {
        return dissolvedOxygen;
    }

    public void setDissolvedOxygen(Float dissolvedOxygen) {
        this.dissolvedOxygen = dissolvedOxygen;
    }

    public Float getConductivity() {
        return conductivity;
    }

    public void setConductivity(Float conductivity) {
        this.conductivity = conductivity;
    }

    public Float getTurbidity() {
        return turbidity;
    }

    public void setTurbidity(Float turbidity) {
        this.turbidity = turbidity;
    }

    public char getGrade() {
        return grade;
    }

    public void setGrade(char grade) {
        this.grade = grade;
    }
}