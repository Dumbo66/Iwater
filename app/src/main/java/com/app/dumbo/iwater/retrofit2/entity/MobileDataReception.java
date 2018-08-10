package com.app.dumbo.iwater.retrofit2.entity;

import java.util.Date;
import java.util.List;

/**
 * Created by dumbo on 2018/7/19
 **/

public class MobileDataReception {
    private int code;

    private String message;

    private List<MobileData> data;

    public int getCode() {
        return code;
    }

    public String getMessge() {
        return message;
    }

    public List<MobileData> getData() {
        return data;
    }
}
