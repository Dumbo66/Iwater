package com.app.dumbo.iwater.retrofit2.entity;

/**
 * Created by Dumbo on 2018/4/21
 **/

public class Reception {
    private int code;
    private String message;
    private Object data;

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Reception(int code, String message){
        this.code=code;
        this.message=message;
    }

    public Reception(int code, String message, Object object){
        this.code=code;
        this.message=message;
        this.data =object;
    }
}
