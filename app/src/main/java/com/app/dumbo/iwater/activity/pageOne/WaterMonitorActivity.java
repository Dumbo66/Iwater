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
import com.app.dumbo.iwater.retrofit2.entity.reception.SitesReception;
import com.app.dumbo.iwater.retrofit2.entity.reception.WaterDataReception;
import com.app.dumbo.iwater.util.MapUtil;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dumbo on 2018/4/6.
 */

public class WaterMonitorActivity extends BaseMapActivity{
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
    private float[] dio;//溶氧
    private float[] tur;//浊度
    private float[] ph;//PH
    private float[] con;//电导率
    private float[] tem;//温度
    private char[] gad;//水质等级

    //地图中心点
    LatLng myLatLng = new LatLng(39.261702,117.101714);

    InfoWindow.OnInfoWindowClickListener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_water_monitor);
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
                setZoom(myLatLng,15.0f);
                break;

            case R.id.rl_right:
                break;

            case R.id.rl_refresh:
                myBaiduMap.clear();
                callData();
                Toast.makeText(WaterMonitorActivity.this,"更新完成",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private class MyLocationListener extends BaseLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            super.onReceiveLocation(bdLocation);
            if (isFirstLoc) {
                isFirstLoc = false;
                setZoom(myLatLng,15.0f);//设置图的尺寸
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
                System.out.println("监测点信息请求数据成功！");
                siteInfos=response.body();
                //监测站点数量
                siteCount=siteInfos.getData().size();

                Call<WaterDataReception> waterDataCall=Retrofit2.getService().getWaterData(siteCount);
                waterDataCall.enqueue(new Callback<WaterDataReception>() {
                @Override
                public void onResponse(Call<WaterDataReception> call,
                        Response<WaterDataReception> response) {
                    System.out.println("请求数据成功！");

                    final String[] s=new String[siteCount];
                    double[] lat=new double[siteCount];
                    double[] lng=new double[siteCount];
                    latLng = new LatLng[siteCount];
                    markers = new Marker[siteCount];
                    dio=new float[siteCount];
                    tur=new float[siteCount];
                    ph=new float[siteCount];
                    con=new float[siteCount];
                    tem=new float[siteCount];
                    gad=new char[siteCount];

                    for(short i=0;i<siteCount;i++){
                        s[i]=siteInfos.getData().get(i).getSiteId();
                        //获取经纬度
                        lat[i]=siteInfos.getData().get(i).getLatWgs84();
                        lng[i]=siteInfos.getData().get(i).getLngWgs84();
                        latLng[i]=new LatLng(lat[i],lng[i]);
//                        latLng[i]=MapUtil.convertWgs84ToBd09ll(new LatLng(lat[i],lng[i]));
                        gad[i]=response.body().getData().get(i).getGrade();
                        //地图标点
                        markers[i]= MapUtil.addMarkers(
                                WaterMonitorActivity.this,myBaiduMap,latLng[i],gad[i]);

                        //获取具体数据
                        dio[i]=response.body().getData().get(i).getDissolvedOxygen();
                        tur[i]=response.body().getData().get(i).getTurbidity();
                        ph[i]=response.body().getData().get(i).getPh();
                        con[i]=response.body().getData().get(i).getConductivity();
                        tem[i]=response.body().getData().get(i).getTemperature();
                    }
                    myBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            for(int i=0;i<siteCount;i++){
                                if (marker==markers[i]){
                                    showInfoWindow(latLng[i],s[i],dio[i],tur[i],ph[i],con[i],tem[i],gad[i]);
                                }
                            }
                            return true;
                        }
                    });
                }

                @Override
                public void onFailure(Call<WaterDataReception> call, Throwable t) {
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

    /**从xml创建要显示的InfoWindow*/
    @SuppressLint("SetTextI18n")
    private void showInfoWindow(LatLng latLng,final String s, float dio,
                                float tur, float ph, float con, float tem , char gad) {
        LayoutInflater inflater=LayoutInflater.from(getApplicationContext());
        View view=inflater.inflate(R.layout.window_water_data_info,null);

        TextView title= view.findViewById(R.id.title);
        TextView tv_dio= view.findViewById(R.id.tv_dio);
        TextView tv_tur= view.findViewById(R.id.tv_tur);
        TextView tv_ph= view.findViewById(R.id.tv_ph);
        TextView tv_con= view.findViewById(R.id.tv_con);
        TextView tv_tem= view.findViewById(R.id.tv_tem);
        TextView tv_gad= view.findViewById(R.id.tv_gad);
        TextView tv_site= view.findViewById(R.id.tv_site);
        RelativeLayout close= view.findViewById(R.id.close);
        TextView tv_chart=view.findViewById(R.id.tv_chart);
        RelativeLayout display_chart=view.findViewById(R.id.display_chart);

        title.setText("实时数据");
        title.setTextColor(Color.GRAY);

        tv_site.setText("S"+s);
        tv_site.setTextColor(Color.GRAY);

        tv_dio.setText("溶氧："+dio+"mg/L");
        tv_dio.setTextColor(Color.GRAY);

        tv_tur.setText("浊度："+tur+"NTU");
        tv_tur.setTextColor(Color.GRAY);

        tv_ph.setText("PH："+ph);
        tv_ph.setTextColor(Color.GRAY);

        tv_con.setText("电导率："+con+"μS/cm");
        tv_con.setTextColor(Color.GRAY);

        tv_tem.setText("温度："+tem+"℃");
        tv_tem.setTextColor(Color.GRAY);

        tv_gad.setText("水质等级："+ gad);
        tv_gad.setTextColor(Color.GRAY);

        tv_chart.setText("查看图表");
        tv_chart.setTextColor(getResources().getColor(R.color.colorGreen));

        display_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WaterMonitorActivity.this,ChartActivity.class);
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
