package com.app.dumbo.iwater.activity.pageOne;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.BaseMapActivity;
import com.app.dumbo.iwater.constant.BaiduTraceConstant;
import com.app.dumbo.iwater.constant.RequestCode;
import com.app.dumbo.iwater.util.CommonUtil;
import com.app.dumbo.iwater.util.MapUtil;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.SortType;
import com.baidu.trace.model.StatusCodes;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * --河湖巡查--
 *
 * Created by dumbo on 2018/4/13
 **/

public class PatrolActivity extends BaseMapActivity {
    private BaiduMap myBaiduMap;//Map对象

    private RelativeLayout rlTraceStart;//开始巡查
    private RelativeLayout  rlTraceStop;//停止巡查
    private TextView tvState;//“开始”或“停止”

    /*鹰眼追踪相关*/
    LBSTraceClient myTraceClient;
    OnTraceListener myTraceListener;//轨迹服务监听器
    private Trace myTrace;
    //轨迹点集合
    List<LatLng> trackPoints = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_patrol);
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);

        //****************初始化轨迹服务********************
        long serviceId= BaiduTraceConstant.BAIDU_TRACE_SERVICE_ID;  //轨迹服务id
        String deviceName="myTrace";    //设备标识
        // 是否需要对象存储服务，默认为：false，关闭对象存储服务。
        // 注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，
        // 若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
        boolean isNeedObjectStorage = false;
        // 初始化轨迹服务
        myTrace = new Trace(serviceId, deviceName, isNeedObjectStorage);
        // 初始化轨迹服务客户端
        myTraceClient = new LBSTraceClient(getApplicationContext());

        //****************设置定位和回传周期********************
        int gatherInterval = 2;   // 定位周期(单位:秒)
        int packInterval = 10;   // 打包回传周期(单位:秒)
        myTraceClient.setInterval(gatherInterval, packInterval);    // 设置定位和打包周期

        //****************初始化轨迹服务监听器********************
        myTraceListener= new OnTraceListener() {
            /**
             * 绑定服务回调接口
             * @param i  状态码
             * @param s 消息
             *                <p>
             *                <pre>0：成功 </pre>
             *                <pre>1：失败</pre>
             */
            @Override
            public void onBindServiceCallback(int i, String s) {
                System.out.println(i);
                Toast.makeText(PatrolActivity.this,
                        "onBindServiceCallback|status:"+1+"|"+"message:"+s,Toast.LENGTH_SHORT).show();
            }

            /**
             * 开启服务回调接口
             * @param status 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功 </pre>
             *                <pre>10000：请求发送失败</pre>
             *                <pre>10001：服务开启失败</pre>
             *                <pre>10002：参数错误</pre>
             *                <pre>10003：网络连接失败</pre>
             *                <pre>10004：网络未开启</pre>
             *                <pre>10005：服务正在开启</pre>
             *                <pre>10006：服务已开启</pre>
             */
            @Override
            public void onStartTraceCallback(int status, String message) {
                if(status== StatusCodes.SUCCESS){
                    System.out.println(status);
                    Toast.makeText(PatrolActivity.this,
                            "onStartTraceCallback|status:"+status+"|"+"message:"+message,Toast.LENGTH_SHORT).show();
                }
            }

            /**
             * 停止服务回调接口
             * @param status 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>11000：请求发送失败</pre>
             *                <pre>11001：服务停止失败</pre>
             *                <pre>11002：服务未开启</pre>
             *                <pre>11003：服务正在停止</pre>
             */
            @Override
            public void onStopTraceCallback(int status, String message) {
                if (status==StatusCodes.SUCCESS){
                    Toast.makeText(PatrolActivity.this,
                            "onStopTraceCallback|status:"+status+"|"+"message:"+message,Toast.LENGTH_SHORT).show();
                    System.out.println(status);
                }
            }

            /**
             * 开启采集回调接口
             * @param status 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>12000：请求发送失败</pre>
             *                <pre>12001：采集开启失败</pre>
             *                <pre>12002：服务未开启</pre>
             */
            @Override
            public void onStartGatherCallback(int status, String message) {
                if (status==StatusCodes.SUCCESS){
                    Toast.makeText(PatrolActivity.this,
                            "onStartGatherCallback|status:"+status+"|"+"message:"+message,Toast.LENGTH_SHORT).show();
                    System.out.println(status);
                }
            }

            /**
             * 停止采集回调接口
             * @param status 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>13000：请求发送失败</pre>
             *                <pre>13001：采集停止失败</pre>
             *                <pre>13002：服务未开启</pre>
             */
            @Override
            public void onStopGatherCallback(int status, String message) {
                if (status==StatusCodes.SUCCESS){
                    Toast.makeText(PatrolActivity.this,"onStopGatherCallback|status:"+status+"|"+"message:"+message,Toast.LENGTH_SHORT).show();
                    System.out.println(status);
                }
            }

            @Override
            public void onInitBOSCallback(int i, String s) {
                Toast.makeText(PatrolActivity.this,
                        "onInitBOSCallback|status:"+i+"|"+"message:"+s,Toast.LENGTH_SHORT).show();
                System.out.println(i);
            }

            /**
             * 推送消息回调接口
             *
             * @param messageNo 状态码
             * @param message 消息
             *                  <p>
             *                  <pre>0x01：配置下发</pre>
             *                  <pre>0x02：语音消息</pre>
             *                  <pre>0x03：服务端围栏报警消息</pre>
             *                  <pre>0x04：本地围栏报警消息</pre>
             *                  <pre>0x05~0x40：系统预留</pre>
             *                  <pre>0x41~0xFF：开发者自定义</pre>
             */
            @Override
            public void onPushCallback(byte messageNo, PushMessage message) {}
        };

        // 开启服务
        myTraceClient.startTrace(myTrace, myTraceListener);
    }

    @Override
    public void initView() {
        super.initView();
        //地图初始化
        MapView myMapView = findViewById(R.id.myMapView);
        myBaiduMap= myMapView.getMap();

        rlTraceStart=findViewById(R.id.rl_trace_start);
        rlTraceStop=findViewById(R.id.rl_trace_stop);
        tvState=findViewById(R.id.tv_state);
    }

    @Override
    public void setListener() {
        super.setListener();
        rlTraceStart.setOnClickListener(this);
        rlTraceStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_trace_start:
                if(CommonUtil.GpsIsOpened(PatrolActivity.this)){
                    rlTraceStart.setVisibility(View.GONE);
                    rlTraceStop.setVisibility(View.VISIBLE);
                    tvState.setText("巡查中…");
                    // 开启采集
                    myTraceClient.startGather(myTraceListener);
                }else{
                    //显示打开GPS对话框
                    showOpenGpsDialog();
                }
                break;

            case R.id.rl_trace_stop:
                //显示退出巡查对话框
                showExitTracingDialog();
                //查询历史轨迹
                queryHistoryTrack();
                break;
        }
    }


    /**确认退出巡查对话框*/
    private void showExitTracingDialog() {
        new SweetAlertDialog(PatrolActivity.this)
                .setContentText("确定退出巡查吗？")
                .setConfirmText("确定")
                .setCancelText("取消")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        rlTraceStart.setVisibility(View.VISIBLE);
                        rlTraceStop.setVisibility(View.GONE);
                        tvState.setText("河湖巡查");
                        //停止采集
                        myTraceClient.stopGather(myTraceListener);
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

    /*打开GPS对话框*/
    private void showOpenGpsDialog() {
        new SweetAlertDialog(PatrolActivity.this)
                .setContentText("该功能需要使用GPS，请打开GPS！")
                .setConfirmText("去打开")
                .setCancelText("取消")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        //跳转到GPS设置界面
                        Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, RequestCode.RIVER_PATROL_GPS_REQUEST_CODE);
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

    /**查询历史轨迹*/
    private void queryHistoryTrack() {
        // 请求标识
        int tag = 1;
        // 轨迹服务ID
        long serviceId = 200727;
        // 设备标识
        String entityName = "myTrace";
        // 创建历史轨迹请求实例
        final HistoryTrackRequest historyTrackRequest
                = new HistoryTrackRequest(tag, serviceId, entityName);

        //设置轨迹查询起止时间
        // 开始时间(单位：秒)
        long startTime = System.currentTimeMillis() / 1000 - 60;
        // 结束时间(单位：秒)
        long endTime = System.currentTimeMillis() / 1000;
        // 设置开始时间
        historyTrackRequest.setStartTime(startTime);
        // 设置结束时间
        historyTrackRequest.setEndTime(endTime);
        // 初始化轨迹服务客户端
        myTraceClient = new LBSTraceClient(getApplicationContext());

        // 初始化轨迹监听器
        OnTrackListener myTrackListener = new OnTrackListener() {
            // 历史轨迹回调
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse response) {
                int total = response.getTotal();
                if (StatusCodes.SUCCESS != response.getStatus()) {
                    Toast.makeText(PatrolActivity.this,response.getMessage(),Toast.LENGTH_SHORT).show();
                } else if (total == 0) {
                    Toast.makeText(PatrolActivity.this,"未查询到轨迹",Toast.LENGTH_SHORT).show();
                } else {
                    List<TrackPoint> points = response.getTrackPoints();
                    if (points != null) {
                        for (TrackPoint trackPoint : points) {
                            if (!MapUtil.isZeroPoint(trackPoint.getLocation().getLatitude(),
                                    trackPoint.getLocation().getLongitude())) {
                                trackPoints.add(MapUtil.convertTrace2Map(trackPoint.getLocation()));
                            }
                        }
                    }
                    //绘制轨迹
                    MapUtil.drawHistoryTrack(PatrolActivity.this,myBaiduMap,trackPoints, SortType.asc);
                }
            }
        };
        // 查询历史轨迹
        myTraceClient.queryHistoryTrack(historyTrackRequest, myTrackListener);
    }
}
