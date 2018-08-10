package com.app.dumbo.iwater.retrofit2.entity;

import java.util.List;

/**
 * Created by dumbo on 2018/4/26
 **/

public class SitesReception {
    private int code;
    private String message;
    private List<Sites> data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<Sites> getData() {
        return data;
    }
}
