package com.app.dumbo.iwater.activity.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.pageOne.AddMomentActivity;
import com.app.dumbo.iwater.activity.pageOne.MenuActivity;
import com.app.dumbo.iwater.activity.pageOne.MobileMonitorActivity;
import com.app.dumbo.iwater.activity.pageOne.PatrolActivity;
import com.app.dumbo.iwater.activity.pageOne.PolicyActivity;
import com.app.dumbo.iwater.activity.pageOne.RiverInfoActivity;
import com.app.dumbo.iwater.activity.pageOne.UndoTaskActivity;
import com.app.dumbo.iwater.activity.pageOne.mapMonitor.MapMonitorActivity;
import com.app.dumbo.iwater.util.CommonUtil;

/**
 * Created by dumbo on 2018/8/10
 **/

public class PageOneFragment extends Fragment implements View.OnClickListener{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View pageOne = inflater.inflate(R.layout.fragement_page_one,null);
        RelativeLayout mapMonitor = pageOne.findViewById(R.id.rl_map_monitor);
        RelativeLayout mobileMonitor = pageOne.findViewById(R.id.rl_mobile_monitor);
        RelativeLayout riverPatrol = pageOne.findViewById(R.id.rl_river_patrol);
        RelativeLayout problemRecord = pageOne.findViewById(R.id.rl_problem_record);
        RelativeLayout undoTask = pageOne.findViewById(R.id.rl_undo_task);
        RelativeLayout policy = pageOne.findViewById(R.id.rl_policy);
        RelativeLayout supervision = pageOne.findViewById(R.id.rl_river_info);
        RelativeLayout more =  pageOne.findViewById(R.id.rl_more);

        mapMonitor.setOnClickListener(this);
        mobileMonitor.setOnClickListener(this);
        riverPatrol.setOnClickListener(this);
        problemRecord.setOnClickListener(this);
        undoTask.setOnClickListener(this);
        policy.setOnClickListener(this);
        supervision.setOnClickListener(this);
        more.setOnClickListener(this);

        return pageOne;
    }

    /**控件监听*/
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //"地图监测"按钮监听
            case R.id.rl_map_monitor:
                CommonUtil.skipActivityByFade(getActivity(),MapMonitorActivity.class);
                break;

            //“移动监测”按钮监听
            case R.id.rl_mobile_monitor:
//                if(Build.VERSION.SDK_INT>=23){
//                    int checkLocationPermission= ActivityCompat.checkSelfPermission
//                            (MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
//                    if(checkLocationPermission!= PackageManager.PERMISSION_GRANTED){
//                        //无权限，请求权限
//                        ActivityCompat.requestPermissions(MainActivity.this,
//                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                RequestCode.MOBILE_MONITOR_REQUEST_PERMISSION_LOCATION_CODE);
//                    }else{
//                        //有权限，判断GPS是否打开
//                        if(!CommonUtil.GpsIsOpened(MainActivity.this)){
//                            DialogUtil.showOpenGpsDialog(MainActivity.this,
//                                    RequestCode.MOBILE_MONITOR_GPS_REQUEST_CODE);
//                        }else{
//                            skipActivityByFade(MobileMonitorActivity.class);
//                        }
//                    }
//                }else{
//                    skipActivityByFade(MobileMonitorActivity.class);
//                }
                CommonUtil.skipActivityByFade(getActivity(),MobileMonitorActivity.class);
                break;

            //"河湖巡查"按钮监听
            case R.id.rl_river_patrol:
                CommonUtil.skipActivityByFade(getActivity(),PatrolActivity.class);
//                if(Build.VERSION.SDK_INT>=23){
//                    int checkLocationPermission= ActivityCompat.checkSelfPermission
//                            (MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
//                    if(checkLocationPermission!= PackageManager.PERMISSION_GRANTED){
//                        //无权限，请求权限
//                        ActivityCompat.requestPermissions(MainActivity.this,
//                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                RequestCode.RIVER_PATROL_REQUEST_PERMISSION_LOCATION_CODE);
//                    }else{
//                        //有权限，判断GPS是否打开
//                        if(!CommonUtil.GpsIsOpened(MainActivity.this)){
//                            DialogUtil.showOpenGpsDialog(MainActivity.this,
//                                    RequestCode.RIVER_PATROL_GPS_REQUEST_CODE);
//                        }else{
//                            skipActivityByFade(PatrolActivity.class);
//                        }
//                    }
//                }else{
//                    skipActivityByFade(PatrolActivity.class);
//                }
                break;


            //“问题记录”按钮监听
            case R.id.rl_problem_record:
                CommonUtil.skipActivityByFade(getActivity(),AddMomentActivity.class);
                break;

            //“事件处理”按钮监听
            case R.id.rl_undo_task:
                CommonUtil.skipActivityByFade(getActivity(),UndoTaskActivity.class);
                break;

            //“河湖信息”按钮监听
            case R.id.rl_river_info:
                CommonUtil.skipActivityByFade(getActivity(),RiverInfoActivity.class);
                break;

            //“政策法规”按钮监听
            case R.id.rl_policy:
                CommonUtil.skipActivityByFade(getActivity(),PolicyActivity.class);
                break;

            //“更多”按钮监听
            case R.id.rl_more:
                CommonUtil.skipActivityByFade(getActivity(),MenuActivity.class);
                break;
        }
    }
}
