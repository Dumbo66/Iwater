package com.app.dumbo.iwater.retrofit2.entity.reception;

import com.app.dumbo.iwater.retrofit2.entity.AirData;
import com.app.dumbo.iwater.retrofit2.entity.WaterData;

import java.util.List;

/**
 * Created by dumbo on 2018/4/23
 **/

public class AirDataReception {
    private int code;

    private String message;

    private List<AirData> data;

    public int getCode() {
        return code;
    }

    public String getMessge() {
        return message;
    }

    public List<AirData> getData() {
        return data;
    }
}
