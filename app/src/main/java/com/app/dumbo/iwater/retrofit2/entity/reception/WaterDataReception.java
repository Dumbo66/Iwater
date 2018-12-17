package com.app.dumbo.iwater.retrofit2.entity.reception;

import com.app.dumbo.iwater.retrofit2.entity.WaterData;

import java.util.List;

/**
 * Created by dumbo on 2018/9/5
 **/

public class WaterDataReception {
    private int code;

    private String message;

    private List<WaterData> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<WaterData> getData() {
        return data;
    }

    public void setData(List<WaterData> data) {
        this.data = data;
    }
}
