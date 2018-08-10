package com.app.dumbo.iwater.retrofit2.entity;

/**
 * --监测点实体类--
 *
 * Created by Dumbo on 2018/4/22
 **/

public class Sites {
    private int site;
    private double latBd09ll;
    private double lngBd09ll;
    private String workState;
    private String description;

    public int getSite() {
        return site;
    }

    public void setSite(int site) {
        this.site = site;
    }

    public double getLatBd09ll() {
        return latBd09ll;
    }

    public void setLatBd09ll(double latBd09ll) {
        this.latBd09ll = latBd09ll;
    }

    public double getLngBd09ll() {
        return lngBd09ll;
    }

    public void setLngBd09ll(double lngBd09ll) {
        this.lngBd09ll = lngBd09ll;
    }

    public String getWorkState() {
        return workState;
    }

    public void setWorkState(String workState) {
        this.workState = workState;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}