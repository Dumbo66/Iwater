package com.app.dumbo.iwater.activity.pageOne;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.BaseMapActivity;
import com.app.dumbo.iwater.retrofit2.Retrofit2;
import com.app.dumbo.iwater.retrofit2.entity.reception.AirDataReception;
import com.app.dumbo.iwater.retrofit2.entity.reception.SitesReception;
import com.app.dumbo.iwater.util.MapUtil;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dumbo on 2018/4/6.
 */

public class AirMonitorActivity extends BaseMapActivity{
    /* 基本地图相关*/
    private BaiduMap myBaiduMap;
    boolean isFirstLoc = true; // 是否首次定位

    //底部控件
    private ImageView circleAdd, circleClose;
    private RelativeLayout myLocation,right, refresh;

    /*地图标注相关*/
    private SitesReception siteInfos;//监测点信息
    private int siteCount;//监测站点数量
    private LatLng[] latLng;//监测站点经纬度
    private Marker[] markers;//监测站点标记
    private float[] VOC;
    private float[] CO2;
    private float[] SO2;
    private float[] NO2;
    private float[] CO;
    private float[] O3;
    private float[] PM25;
    private float[] PM10;
    private float[] airTem;
    private float[] airHumid;
    private char[] gad;


    InfoWindow.OnInfoWindowClickListener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_air_monitor);
        super.onCreate(savedInstanceState);

        //设置定位监听器
        MyLocationListener myLocationListener =new MyLocationListener();
        setLocationListener(myLocationListener);

        //请求数据
        callData();
    }

    @Override
    public void initView() {
        super.initView();
        //地图初始化
        MapView myMapView= findViewById(R.id.myMapView);
        myBaiduMap=myMapView.getMap();

        //底部控件
        circleClose =findViewById(R.id.circle_close);
        circleAdd =findViewById(R.id.circle_add);
        myLocation =findViewById(R.id.rl_my_location);
        right=findViewById(R.id.rl_right);
        refresh =findViewById(R.id.rl_refresh);
    }

    @Override
    public void setListener() {
        super.setListener();
        circleClose.setOnClickListener(this);
        circleAdd.setOnClickListener(this);
        myLocation.setOnClickListener(this);
        right.setOnClickListener(this);
        refresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.circle_close:
                circleAdd.setVisibility(View.VISIBLE);
                circleClose.setVisibility(View.INVISIBLE);
                myLocation.setVisibility(View.INVISIBLE);
                right.setVisibility(View.INVISIBLE);
                refresh.setVisibility(View.INVISIBLE);
                break;

            case R.id.circle_add:
                circleAdd.setVisibility(View.INVISIBLE);
                circleClose.setVisibility(View.VISIBLE);
                myLocation.setVisibility(View.VISIBLE);
                right.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.VISIBLE);
                break;

            case R.id.rl_my_location:
                setZoom(getLatLng(),15.0f);
                break;

            case R.id.rl_right:
                break;

            case R.id.rl_refresh:
                myBaiduMap.clear();
                callData();
                Toast.makeText(AirMonitorActivity.this,"更新完成",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private class MyLocationListener extends BaseLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            super.onReceiveLocation(bdLocation);
            if (isFirstLoc) {
                isFirstLoc = false;
                setZoom(getLatLng(),15.0f);//设置图的尺寸
            }
        }
    }

    /**请求数据*/
    private void callData() {
        //请求siteInfo
        Call<SitesReception> sitesCall= Retrofit2.getService().getAllSites();
        sitesCall.enqueue(new Callback<SitesReception>() {
            @Override
            public void onResponse(@NonNull Call<SitesReception> call,
                                   Response<SitesReception> response) {
                System.out.println("请求数据成功！");
                siteInfos=response.body();
                //监测站点数量
                siteCount=siteInfos.getData().size();

                Call<AirDataReception> airDataCall=Retrofit2.getService().getAirData(siteCount);
                airDataCall.enqueue(new Callback<AirDataReception>() {
                    @Override
                    public void onResponse(Call<AirDataReception> call,
                                           Response<AirDataReception> response) {
                        System.out.println("请求数据成功！");
                        System.out.println("ccccccccccccccccccccccccccccccccccccccccccc"+response.body().getCode());

                        final String[] s=new String[siteCount];
                        double[] lat=new double[siteCount];
                        double[] lng=new double[siteCount];
                        latLng = new LatLng[siteCount];
                        markers = new Marker[siteCount];
                        VOC=new float[siteCount];
                        NO2=new float[siteCount];
                        SO2=new float[siteCount];
                        CO2=new float[siteCount];
                        CO=new float[siteCount];
                        O3=new float[siteCount];
                        PM25=new float[siteCount];
                        PM10=new float[siteCount];
                        airTem=new float[siteCount];
                        airHumid=new float[siteCount];
                        gad=new char[siteCount];

                        for(short i=0;i<siteCount;i++){
                            s[i]=siteInfos.getData().get(i).getSiteId();
                            //获取经纬度
                            lat[i]=siteInfos.getData().get(i).getLatWgs84();
                            lng[i]=siteInfos.getData().get(i).getLngWgs84();
                            latLng[i]=MapUtil.convertWgs84ToBd09ll(new LatLng(lat[i],lng[i]));
                            gad[i]=response.body().getData().get(i).getGrade();
                            //地图标点
                            markers[i]= MapUtil.addMarkers(
                                    AirMonitorActivity.this,myBaiduMap,latLng[i],gad[i]);

                            //获取具体数据
                            VOC[i]= Objects.requireNonNull(response.body()).getData().get(i).getVoc();
                            CO2[i]= Objects.requireNonNull(response.body()).getData().get(i).getCo2();
                            SO2[i]= Objects.requireNonNull(response.body()).getData().get(i).getSo2();
                            NO2[i]= Objects.requireNonNull(response.body()).getData().get(i).getNo2();
                            CO[i]= Objects.requireNonNull(response.body()).getData().get(i).getCo();
                            O3[i]= Objects.requireNonNull(response.body()).getData().get(i).getO3();
                            airTem[i]= Objects.requireNonNull(response.body()).getData().get(i).getAirTemperature();
                            airHumid[i]= Objects.requireNonNull(response.body()).getData().get(i).getAirHumid();
                            PM25[i]= Objects.requireNonNull(response.body()).getData().get(i).getPm25();
                            PM10[i]= Objects.requireNonNull(response.body()).getData().get(i).getPm10();
                        }
                        myBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                for(int i=0;i<siteCount;i++){
                                    if (marker==markers[i]){
                                        showInfoWindow(latLng[i],s[i],VOC[i],CO2[i],SO2[i],NO2[i],CO[i],
                                                O3[i],airTem[i],airHumid[i],PM25[i],PM10[i],gad[i]);
                                    }
                                }
                                return true;
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<AirDataReception> call, Throwable t) {
                        System.out.println("请求数据失败！");
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

    /**从xml创建要显示的InfoWindow*/
    @SuppressLint("SetTextI18n")
    private void showInfoWindow(LatLng latLng,final String s, float VOC, float CO2, float SO2, float NO2,
                                float CO,float O3,float airTem,float airHumid,float PM25,float PM10 , char gad) {
        LayoutInflater inflater=LayoutInflater.from(getApplicationContext());
        View view=inflater.inflate(R.layout.window_air_data_info,null);

        TextView title= view.findViewById(R.id.title);
        TextView parm1= view.findViewById(R.id.tv_parm1);
        TextView parm2= view.findViewById(R.id.tv_parm2);
        TextView parm3= view.findViewById(R.id.tv_parm3);
        TextView parm4= view.findViewById(R.id.tv_parm4);
        TextView parm5= view.findViewById(R.id.tv_parm5);
        TextView parm6= view.findViewById(R.id.tv_parm6);
        TextView parm7= view.findViewById(R.id.tv_parm7);
        TextView parm8= view.findViewById(R.id.tv_parm8);
        TextView parm9= view.findViewById(R.id.tv_parm9);
        TextView parm10= view.findViewById(R.id.tv_parm10);
//        TextView tv_gad= view.findViewById(R.id.tv_gad);
        TextView tv_site= view.findViewById(R.id.tv_site);
        RelativeLayout close= view.findViewById(R.id.close);
        TextView tv_chart=view.findViewById(R.id.tv_chart);
        RelativeLayout display_chart=view.findViewById(R.id.display_chart);

        title.setText("实时数据");
        title.setTextColor(Color.GRAY);

        tv_site.setText("S"+s);
        tv_site.setTextColor(Color.GRAY);

        parm1.setText("VOC："+VOC);
        parm1.setTextColor(Color.GRAY);

        parm2.setText("CO2："+CO2);
        parm2.setTextColor(Color.GRAY);

//        tv_ph.setText("SO2："+SO2);
//        tv_ph.setTextColor(Color.GRAY);
//
//        tv_con.setText("NO2："+NO2);
//        tv_con.setTextColor(Color.GRAY);
//
//        tv_tem.setText("CO："+CO);
//        tv_tem.setTextColor(Color.GRAY);

//        tv_gad.setText("等级："+ gad);
//        tv_gad.setTextColor(Color.GRAY);

        tv_chart.setText("查看图表");
        tv_chart.setTextColor(getResources().getColor(R.color.colorGreen));

        display_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AirMonitorActivity.this,ChartActivity.class);
                intent.putExtra("site",s);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myBaiduMap.hideInfoWindow();
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
}
