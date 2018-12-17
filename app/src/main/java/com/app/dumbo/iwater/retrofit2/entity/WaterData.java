package com.app.dumbo.iwater.retrofit2.entity;

import java.util.Date;

/**
 * --定点监测数据实体类--
 *
 * Created by Dumbo on 2018/4/22
 **/

public class WaterData {
    //记录时间
    private Date recordTime;
    //监测点号
    private String siteId;
    //PH
    private float ph;
    //温度
    private float temperature;
    //溶氧
    private float dissolvedOxygen;
    //电导率
    private float conductivity;
    //浊度
    private float turbidity;
    //水质等级
    private char grade;

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public float getPh() {
        return ph;
    }

    public void setPh(float ph) {
        this.ph = ph;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getDissolvedOxygen() {
        return dissolvedOxygen;
    }

    public void setDissolvedOxygen(float dissolvedOxygen) {
        this.dissolvedOxygen = dissolvedOxygen;
    }

    public float getConductivity() {
        return conductivity;
    }

    public void setConductivity(float conductivity) {
        this.conductivity = conductivity;
    }

    public float getTurbidity() {
        return turbidity;
    }

    public void setTurbidity(float turbidity) {
        this.turbidity = turbidity;
    }

    public char getGrade() {
        return grade;
    }

    public void setGrade(char grade) {
        this.grade = grade;
    }
}