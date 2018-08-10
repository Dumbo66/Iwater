package com.app.dumbo.iwater.retrofit2;

import com.app.dumbo.iwater.constant.Common;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dumbo on 2018/5/20
 **/

public class Retrofit2 {
    public static ApiService getService(){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://"+ Common.HOST_IP +"/api/v1/")// 设置网络请求baseUrl
                .addConverterFactory(GsonConverterFactory.create())// 设置使用Gson解析
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())// 支持RxJava
                .build();
        return retrofit.create(ApiService.class);
    }
}
