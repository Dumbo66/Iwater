package com.app.dumbo.iwater.retrofit2.entity.reception;

import com.app.dumbo.iwater.retrofit2.entity.Moments;

import java.util.List;

/**
 * Created by dumbo on 2018/8/5
 **/

public class MomentsReception {
    private int code;

    private String message;

    private List<Moments> data;

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

    public List<Moments> getData() {
        return data;
    }

    public void setData(List<Moments> data) {
        this.data = data;
    }
}
