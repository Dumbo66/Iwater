package com.app.dumbo.iwater.activity.pageFour.deviceManager;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.BaseMapActivity;
import com.app.dumbo.iwater.constant.ResponseCode;
import com.app.dumbo.iwater.retrofit2.Retrofit2;
import com.app.dumbo.iwater.retrofit2.entity.DataReception;
import com.app.dumbo.iwater.retrofit2.entity.Reception;
import com.app.dumbo.iwater.retrofit2.entity.SitesReception;
import com.app.dumbo.iwater.util.MapUtil;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dumbo on 2018/4/6.
 */

public class DeviceManagerActivity extends BaseMapActivity{
    /* 基本地图相关*/
    private BaiduMap myBaiduMap;//Baidu地图

    /*地图标注相关*/
    private SitesReception siteInfos;//监测点信息
    private int siteCount;//监测站点数量
    private LatLng[] latLng;//监测站点经纬度
    private Marker[] markers;//监测站点标记

    InfoWindow.OnInfoWindowClickListener listener;

    private RelativeLayout add;//添加设备
    private RelativeLayout repair;//报修

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_device_manager);
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);

        LayoutInflater inflater=LayoutInflater.from(DeviceManagerActivity.this);
        View view=inflater.inflate(R.layout.window_device_info,null);

        add=findViewById(R.id.rl_add_device);
        repair=view.findViewById(R.id.rl_repair);

        //设置定位监听器
        MyLocationListener myLocationListener =new MyLocationListener();
        setLocationListener(myLocationListener);

        //请求siteInfos
        callSiteInfos();
    }

    /*请求站点信息*/
    private void callSiteInfos() {
        Call<SitesReception> sitesCall= Retrofit2.getService().getAllSites();
        sitesCall.enqueue(new Callback<SitesReception>() {
            @Override
            public void onResponse(@NonNull Call<SitesReception> call,
                                   Response<SitesReception> response) {
                System.out.println("请求数据成功！");
                siteInfos=response.body();
                //监测站点数量
                siteCount=siteInfos.getData().size();

                //请求monitorData
                Call<DataReception> monitorDataCall=Retrofit2.getService().getMonitorData(siteCount);
                monitorDataCall.enqueue(new Callback<DataReception>() {
                    @Override
                    public void onResponse(Call<DataReception> call,
                                           Response<DataReception> response) {
                        System.out.println("请求数据成功！");

                        double[] lat=new double[siteCount];
                        double[] lng=new double[siteCount];
                        final String[] state=new String[siteCount];
                        final String[] desc=new String[siteCount];
                        final int s[]=new int[siteCount];
                        latLng = new LatLng[siteCount];
                        markers = new Marker[siteCount];

                        for(short i=0;i<siteCount;i++){
                            s[i]=siteInfos.getData().get(i).getSite();
                            //获取经纬度
                            lat[i]=siteInfos.getData().get(i).getLatBd09ll();
                            lng[i]=siteInfos.getData().get(i).getLngBd09ll();
                            latLng[i]=new LatLng(lat[i],lng[i]);
                            //状态信息
                            state[i]=siteInfos.getData().get(i).getWorkState();
                            desc[i]=siteInfos.getData().get(i).getDescription();
                            if(state[i].equals("正常")){
                                //地图标点
                                markers[i]= MapUtil.addMarkers(
                                        DeviceManagerActivity.this,myBaiduMap,latLng[i],'A');
                            }else if(state[i].equals("故障")){
                                //地图标点
                                markers[i]=MapUtil.addMarkers(
                                        DeviceManagerActivity.this,myBaiduMap,latLng[i],'F');
                            }
                        }
                        myBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                for(int i=0;i<siteCount;i++){
                                    if (marker==markers[i]){
                                        showInfoWindow(latLng[i],s[i],state[i],desc[i]);
                                    }
                                }
                                return true;
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<DataReception> call, Throwable t) {
                        System.out.println("请求数据失败！");
                        System.out.println(t.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Call<SitesReception> call, Throwable t) {
                System.out.println("请求数据失败！");
                System.out.println(t.getMessage());
            }
        });
    }

    /*从xml创建要显示的InfoWindow*/
    @SuppressLint("SetTextI18n")
    private void showInfoWindow(LatLng latLng, final int siteNum, String stateStr, String descStr) {
        LayoutInflater inflater=LayoutInflater.from(getApplicationContext());
        View view=inflater.inflate(R.layout.window_device_info,null);

        TextView title= view.findViewById(R.id.title);
        RelativeLayout close=view.findViewById(R.id.rl_close);
        final TextView site= view.findViewById(R.id.tv_site);
        TextView state= view.findViewById(R.id.tv_state);
        TextView desc= view.findViewById(R.id.tv_desc);
        TextView address= view.findViewById(R.id.tv_address);
        RelativeLayout deleteDevice=view.findViewById(R.id.rl_delete_device);

        title.setText("设备信息");
        title.setTextColor(Color.GRAY);

        site.setText("设备号："+siteNum);
        site.setTextColor(Color.GRAY);

        state.setText("设备状态："+stateStr);
        state.setTextColor(Color.GRAY);

        desc.setText("描述："+descStr);
        desc.setTextColor(Color.GRAY);

        address.setText("地址：XXXXXXXXXXXXXXXXXXXXXXX");
        address.setTextColor(Color.GRAY);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myBaiduMap.hideInfoWindow();
            }
        });

        deleteDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteSiteDialog(siteNum);
            }
        });

        listener = new InfoWindow.OnInfoWindowClickListener() {
            public void onInfoWindowClick() {
                myBaiduMap.hideInfoWindow();
            }
        };

        //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
        InfoWindow mInfoWindow = new InfoWindow(view, latLng, -47);
        //显示InfoWindow
        myBaiduMap.showInfoWindow(mInfoWindow);
    }

    /*显示删除监测点对话框*/
    private void showDeleteSiteDialog(final int site) {
        new SweetAlertDialog(DeviceManagerActivity.this)
                .setContentText("确定删除该检测点吗？")
                .setConfirmText("确定")
                .setCancelText("取消")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Call<Reception> deleteSiteCall= Retrofit2.getService().deleteSite(site);
                        deleteSiteCall.enqueue(new Callback<Reception>() {
                            @Override
                            public void onResponse(Call<Reception> call, Response<Reception> response) {
                                int code=response.body().getCode();
                                String msg=response.body().getMessage();
                                if(code== ResponseCode.OK){
                                    myBaiduMap.clear();
                                    callSiteInfos();
                                    Toast.makeText(DeviceManagerActivity.this, msg,Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Reception> call, Throwable t) {
                                Toast.makeText(DeviceManagerActivity.this,
                                        "删除监测点失败",Toast.LENGTH_SHORT).show();
                                System.out.println(t.getMessage());
                            }
                        });
                        sweetAlertDialog.dismiss();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }

    private class MyLocationListener extends BaseLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            super.onReceiveLocation(bdLocation);
            setZoom(getLatLng(),15.0f);
        }
    }
}
