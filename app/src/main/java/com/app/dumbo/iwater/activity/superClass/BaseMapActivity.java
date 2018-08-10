package com.app.dumbo.iwater.activity.superClass;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.app.dumbo.iwater.R;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by dumbo on 2018/5/29
 **/

@SuppressLint("Registered")
public class BaseMapActivity extends AnimFadeActivity implements SensorEventListener{
    /* 基本地图相关*/
    private MapView myMapView =null;//Baidu地图View
    private BaiduMap myBaiduMap=null;
    private RelativeLayout normalMap, satelliteMap;//平面图和卫星图切换控件

    /*定位相关*/
    private SensorManager mSensorManager;
    public LocationClient myLocClient =null;
    private BaseLocationListener baseListener =new BaseLocationListener() ;
    boolean isFirstLoc = true; // 是否首次定位

    private MyLocationData myLocData;//位置信息对象
    private double myCurrentLat;//当前经度
    private double myCurrentLng;//当前纬度
    private double lastX;
    private int myCurrentDirection;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());

        setBaseMap(); //基本地图设置

        setLocation();  //定位设置
    }

    /*基本地图设置*/
    private void setBaseMap() {
        normalMap = findViewById(R.id.normal_map);
        satelliteMap = findViewById(R.id.satellite_map);
        RelativeLayout myLocation = findViewById(R.id.my_location);
        //地图初始化
        myMapView= findViewById(R.id.myMapView);
        myBaiduMap=myMapView.getMap();
        UiSettings myUiSetting = myBaiduMap.getUiSettings();

        //设置“缩放按钮”不显示
        myMapView.showZoomControls(false);

        //禁用地图旋转功能
        myUiSetting.setRotateGesturesEnabled(false);

        //不显示指南针
        myUiSetting.setCompassEnabled(false);

        //设置最大最小缩放
        myBaiduMap.setMaxAndMinZoomLevel(21, 14);

        //默认卫星地图
        myBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        //监听平面图按钮，显示卫星地图
        normalMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalMap.setVisibility(View.INVISIBLE);
                satelliteMap.setVisibility(View.VISIBLE);
                myBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            }
        });

        //监听卫星图按钮，显示平面地图
        satelliteMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalMap.setVisibility(View.VISIBLE);
                satelliteMap.setVisibility(View.INVISIBLE);
                myBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            }
        });

        myBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) { }
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    /*设置定位*/
    private void setLocation() {
        //获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // 开启定位图层
        myBaiduMap.setMyLocationEnabled(true);

        //初始化LocationClient类,定位初始化
        myLocClient =new LocationClient(getApplicationContext());

        //设置监听器
        setLocationListener(baseListener);

        //配置定位SDK参数
        LocationClientOption option=new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);//发起定位请求的间隔(ms)
        myLocClient.setLocOption(option);
        myLocClient.start();
    }

    protected void setLocationListener(BDLocationListener listener) {
        //注册监听函数
        myLocClient.registerLocationListener(listener);
    }

    /*定位SDK监听函数*/
    public class BaseLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // map view 销毁后不在处理新接收的位置
            if (bdLocation == null || myMapView == null) {
                return;
            }
            //实时位置经纬度
            myCurrentLat = bdLocation.getLatitude();
            myCurrentLng = bdLocation.getLongitude();

            myLocData = new MyLocationData.Builder()
                    .latitude(myCurrentLat)
                    .longitude(myCurrentLng)
                    .direction(myCurrentDirection)
                    .build();
            myBaiduMap.setMyLocationData(myLocData);
            if (isFirstLoc) {
                isFirstLoc = false;
                setZoom(getLatLng(),18.0f);
            }
        }

        /**获取经纬度*/
        public LatLng getLatLng() {
            return new LatLng(myCurrentLat,myCurrentLng);
        }

        /**设置图的尺寸*/
        protected void setZoom(LatLng latLng,float f){
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng).zoom(f);
            myBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    /*方向传感器监听*/
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            myCurrentDirection = (int) x;
            myLocData = new MyLocationData.Builder()
                    .latitude(myCurrentLat)
                    .longitude(myCurrentLng)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(myCurrentDirection)
                    .build();
            myBaiduMap.setMyLocationData(myLocData);
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        myMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        myMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        myLocClient.stop();
        // 关闭定位图层
        myBaiduMap.setMyLocationEnabled(false);
        myMapView.onDestroy();
        myMapView = null;
        super.onDestroy();
    }
}
