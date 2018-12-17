package com.app.dumbo.iwater.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.pageOne.AddMomentActivity;
import com.app.dumbo.iwater.activity.pageOne.AirMonitorActivity;
import com.app.dumbo.iwater.activity.pageOne.MenuActivity;
import com.app.dumbo.iwater.activity.pageOne.MobileMonitorActivity;
import com.app.dumbo.iwater.activity.pageOne.PatrolActivity;
import com.app.dumbo.iwater.activity.pageOne.PolicyActivity;
import com.app.dumbo.iwater.activity.pageOne.RiverInfoActivity;
import com.app.dumbo.iwater.activity.pageOne.WaterMonitorActivity;
import com.app.dumbo.iwater.constant.RequestCode;
import com.app.dumbo.iwater.util.CommonUtil;
import com.app.dumbo.iwater.util.FragmentPermUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.functions.Consumer;

/**
 * Created by dumbo on 2018/8/10
 **/

public class PageOneFragment extends Fragment implements View.OnClickListener{
    private SweetAlertDialog requestLocPermDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
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
                CommonUtil.skipActivityByFade(getActivity(),WaterMonitorActivity.class);
                break;

            //“移动监测”按钮监听
            case R.id.rl_mobile_monitor:
                requestLocPermToActivity( RequestCode.MOBILE_MONITOR_GPS_REQUEST_CODE, MobileMonitorActivity.class);
                break;

            //"河湖巡查"按钮监听
            case R.id.rl_river_patrol:
                //位置权限请求，若请求成功则跳转，请求失败则显示对话框
                requestLocPermToActivity(RequestCode.RIVER_PATROL_GPS_REQUEST_CODE, PatrolActivity.class );
                break;

            //“问题记录”按钮监听
            case R.id.rl_problem_record:
                requestLocPermToActivity( RequestCode.ADD_MOMENT_GPS_REQUEST_CODE, AddMomentActivity.class);
                break;

            //“事件处理”按钮监听
            case R.id.rl_undo_task:
                CommonUtil.skipActivityByFade(getActivity(),AirMonitorActivity.class);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RequestCode.REQUEST_PERMISSION_CODE:
                requestLocPermDialog.dismiss();
                break;

            case RequestCode.MOBILE_MONITOR_GPS_REQUEST_CODE:
                if(CommonUtil.GpsIsOpened(Objects.requireNonNull(getActivity()))){
                    CommonUtil.skipActivityByFade(getActivity(),MobileMonitorActivity.class);
                }
                break;

            case RequestCode.RIVER_PATROL_GPS_REQUEST_CODE:
                if(CommonUtil.GpsIsOpened(Objects.requireNonNull(getActivity()))) {
                    CommonUtil.skipActivityByFade(getActivity(), PatrolActivity.class);
                }
                break;

            case RequestCode.ADD_MOMENT_GPS_REQUEST_CODE:
                if(CommonUtil.GpsIsOpened(Objects.requireNonNull(getActivity()))) {
                    CommonUtil.skipActivityByFade(getActivity(), AddMomentActivity.class);
                }
                break;
        }
    }

    /**位置权限请求*/
    @SuppressLint("CheckResult")
    public void requestLocPermToActivity(final int requestGpsCode, final Class descClass) {
        RxPermissions rxPermissions=new RxPermissions(Objects.requireNonNull(getActivity()));
        rxPermissions.requestEach(Manifest.permission.ACCESS_FINE_LOCATION)//位置权限
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            if(CommonUtil.GpsIsOpened(getActivity())){//判断GPS是否打开
                                CommonUtil.skipActivityByFade(getActivity(),descClass);
                            }else{
                                showOpenGpgDialog();//显示打开GPS对话框
                            }
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限,未勾选不在询问
                            Toast.makeText(getActivity(),"该功能需要开启位置权限，请您开启！",Toast.LENGTH_SHORT).show();
                        } else {
                            // 用户拒绝了该权限,且勾选不在询问
                            showOpenPermissionDialog();//显示请求权限对话框
                        }
                    }

                    private void showOpenPermissionDialog() {
                        requestLocPermDialog=new SweetAlertDialog(getActivity());
                        requestLocPermDialog.setTitleText("权限申请")
                                .setContentText("该功能需要在“设置-应用-掌上治水-权限”中开启位置权限，请您开启！")
                                .setConfirmText("去打开")
                                .setCancelText("取消")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        FragmentPermUtil fragmentPermUtil = new FragmentPermUtil(getActivity(),PageOneFragment.this);
                                        fragmentPermUtil.jumpPermissionPage();
                                    }
                                })
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                }).show();
                    }

                    private void showOpenGpgDialog() {
                        new SweetAlertDialog(getActivity())
                                .setContentText("该功能需要使用GPS，请打开GPS！")
                                .setConfirmText("去打开")
                                .setCancelText("取消")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        //跳转到GPS设置界面
                                        Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivityForResult(intent,requestGpsCode);
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
                });
    }
}
