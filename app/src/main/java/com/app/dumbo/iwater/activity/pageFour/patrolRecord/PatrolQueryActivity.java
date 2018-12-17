package com.app.dumbo.iwater.activity.pageFour.patrolRecord;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.BaseMapActivity;
import com.app.dumbo.iwater.util.MapUtil;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.SortType;
import com.baidu.trace.model.StatusCodes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dumbo on 2018/5/29
 **/

public class PatrolQueryActivity extends BaseMapActivity {
    private MapView myMapView =null;//Baidu地图View
    private BaiduMap myBaiduMap=null;

    private MapUtil mapUtil;

    /*鹰眼追踪相关*/
    LBSTraceClient myTraceClient;

    // 轨迹点集合
    private List<LatLng> trackPoints = new ArrayList<>();

    //轨迹排序规则
    private SortType sortType = SortType.asc;

    private int pageIndex = 1;

    private RelativeLayout rl_empty;
    private ScrollView svTrackQuery;

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_patrol_history);
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);

        //地图初始化
        myMapView= findViewById(R.id.myMapView);
        myBaiduMap=myMapView.getMap();

        rl_empty=findViewById(R.id.rl_empty);
        svTrackQuery=findViewById(R.id.sv_track_query);

        rl_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svTrackQuery.setVisibility(View.GONE);
            }
        });

        myBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                svTrackQuery.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        // 请求标识
        int tag = 1;
        // 轨迹服务ID
        long serviceId = 200727;
        // 设备标识
        String entityName = "myTrace";
        // 创建历史轨迹请求实例
        final HistoryTrackRequest historyTrackRequest = new HistoryTrackRequest(tag, serviceId, entityName);

        //设置轨迹查询起止时间
        // 开始时间(单位：秒)
        long startTime = System.currentTimeMillis() / 1000 - 12 * 60 * 60;
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
                if(response.getStatus()== StatusCodes.SUCCESS){
                    List<TrackPoint> points = response.getTrackPoints();
                    if (null != points) {
                        for (TrackPoint trackPoint : points) {
                            if (!MapUtil.isZeroPoint(trackPoint.getLocation().getLatitude(),
                                    trackPoint.getLocation().getLongitude())) {
                                trackPoints.add(MapUtil.convertTrace2Map(trackPoint.getLocation()));
                            }
                        }
                    }
                    //绘制轨迹
                    MapUtil.drawHistoryTrack(PatrolQueryActivity.this,myBaiduMap,trackPoints,SortType.asc);
                }
            }
        };

        // 查询历史轨迹
        myTraceClient.queryHistoryTrack(historyTrackRequest, myTrackListener);
    }
}
