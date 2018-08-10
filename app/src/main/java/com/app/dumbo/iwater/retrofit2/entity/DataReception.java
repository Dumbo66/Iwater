package com.app.dumbo.iwater.retrofit2.entity;

import java.util.Date;
import java.util.List;

/**
 * Created by dumbo on 2018/4/23
 **/

public class DataReception {
    private int code;

    private String message;

    private List<MonitorData> data;
    public class MonitorData{
        private Date date;
        private int site;
        private float ph;
        private float temperature;
        private float dissolvedOxygen;
        private float conductivity;
        private float turbidity;
        private char grade;

        public Date getDate() {
            return date;
        }

        public int getSite() {
            return site;
        }

        public float getPh() {
            return ph;
        }

        public float getTemperature() {
            return temperature;
        }

        public float getDissolvedOxygen() {
            return dissolvedOxygen;
        }

        public float getConductivity() {
            return conductivity;
        }

        public float getTurbidity() {
            return turbidity;
        }

        public char getGrade() {
            return grade;
        }
    }

    public int getCode() {
        return code;
    }

    public String getMessge() {
        return message;
    }

    public List<MonitorData> getData() {
        return data;
    }

}
