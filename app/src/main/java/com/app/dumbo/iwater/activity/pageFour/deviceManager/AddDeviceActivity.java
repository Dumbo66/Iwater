package com.app.dumbo.iwater.activity.pageFour.deviceManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.AnimFadeActivity;
import com.app.dumbo.iwater.constant.ResponseCode;
import com.app.dumbo.iwater.retrofit2.Retrofit2;
import com.app.dumbo.iwater.retrofit2.entity.Reception;
import com.app.dumbo.iwater.retrofit2.entity.Sites;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dumbo on 2018/7/16
 **/

public class AddDeviceActivity extends AnimFadeActivity {
    private  Sites sites;

    private RelativeLayout rlAddDevice;
    private TextView tvAddDevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_device);
        super.onCreate(savedInstanceState);

        rlAddDevice =findViewById(R.id.rl_add_device);
        tvAddDevice=findViewById(R.id.tv_add_device);

        //接收来自DeviceManagerActivity的传值
        Intent intent=getIntent();
        double lat=intent.getDoubleExtra("lat",0);
        double lng=intent.getDoubleExtra("lng",0);

        sites=new Sites();
        sites.setLatBd09ll(lat);
        sites.setLngBd09ll(lng);
        sites.setWorkState("正常");
        sites.setDescription("设备工作正常");

        rlAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Reception> addSiteCall= Retrofit2.getService().postSite(sites);
                addSiteCall.enqueue(new Callback<Reception>() {
                    @Override
                    public void onResponse(Call<Reception> call, Response<Reception> response) {
                        int code=response.body().getCode();
                        String msg=response.body().getMessage();
                        if(code==ResponseCode.OK){
                            Toast.makeText(AddDeviceActivity.this, msg,Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(AddDeviceActivity.this,DeviceManagerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<Reception> call, Throwable t) {
                        Toast.makeText(AddDeviceActivity.this,
                                "添加设备失败",Toast.LENGTH_SHORT).show();
                        System.out.println(t.getMessage());
                    }
                });
            }
        });
    }
}
