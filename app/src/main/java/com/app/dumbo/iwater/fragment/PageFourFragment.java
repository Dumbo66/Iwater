package com.app.dumbo.iwater.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.pageFour.AboutActivity;
import com.app.dumbo.iwater.activity.pageFour.AssessActivity;
import com.app.dumbo.iwater.activity.pageFour.AttendanceActivity;
import com.app.dumbo.iwater.activity.pageFour.FlagActivity;
import com.app.dumbo.iwater.activity.pageFour.GradeActivity;
import com.app.dumbo.iwater.activity.pageFour.HelpActivity;
import com.app.dumbo.iwater.activity.pageFour.SetActivity;
import com.app.dumbo.iwater.activity.pageFour.deviceManager.DeviceManagerActivity;
import com.app.dumbo.iwater.activity.pageFour.loginAndRegister.LoginRegisterActivity;
import com.app.dumbo.iwater.activity.pageFour.loginAndRegister.UserInfoActivity;
import com.app.dumbo.iwater.activity.pageFour.patrolRecord.PatrolQueryActivity;
import com.app.dumbo.iwater.activity.pageOne.PatrolActivity;
import com.app.dumbo.iwater.constant.Common;
import com.app.dumbo.iwater.constant.RequestCode;
import com.app.dumbo.iwater.util.CommonUtil;
import com.app.dumbo.iwater.util.FragmentPermUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Objects;

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.functions.Consumer;

/**
 * Created by dumbo on 2018/8/10
 **/

public class PageFourFragment extends Fragment implements View.OnClickListener{
    private SweetAlertDialog requestLocPermDialog;

    private ImageView ivAvatar;
    private TextView tvNickname;
    private TextView tvUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View pageFour = inflater.inflate(R.layout.fragement_page_four, null);

        //初始化控件
        RelativeLayout logOut = pageFour.findViewById(R.id.rl_log_out);
        RelativeLayout logIn =  pageFour.findViewById(R.id.rl_log_in);
        ivAvatar = pageFour.findViewById(R.id.iv_avatar);
        tvNickname =pageFour.findViewById(R.id.tv_nick_name);
        tvUserId =pageFour.findViewById(R.id.tv_user_id);

        RelativeLayout deviceManager =  pageFour.findViewById(R.id.rl_device_manager);
        RelativeLayout myRiver = pageFour.findViewById(R.id.rl_my_river);
        RelativeLayout patrolHistory = pageFour.findViewById(R.id.rl_patrol_history);
        RelativeLayout attendance = pageFour.findViewById(R.id.rl_attendance);
        RelativeLayout myFlag=  pageFour.findViewById(R.id.rl_my_flag);
        RelativeLayout myAssess= pageFour.findViewById(R.id.rl_my_assess);
        RelativeLayout myGrade= pageFour.findViewById(R.id.rl_my_grade);
        final RelativeLayout help= pageFour.findViewById(R.id.rl_help);
        RelativeLayout about = pageFour.findViewById(R.id.rl_about);
        RelativeLayout set= pageFour.findViewById(R.id.rl_set);

        //设置监听
        logOut.setOnClickListener(this);
        logIn.setOnClickListener(this);
        deviceManager.setOnClickListener(this);
        myRiver.setOnClickListener(this);
        patrolHistory.setOnClickListener(this);
        attendance.setOnClickListener(this);
        myFlag.setOnClickListener(this);
        myAssess.setOnClickListener(this);
        myGrade.setOnClickListener(this);
        help.setOnClickListener(this);
        about.setOnClickListener(this);
        set.setOnClickListener(this);

        SharedPreferences sp= Objects.requireNonNull(getActivity())
                .getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String avatarUrl="http://"+ Common.HOST_IP +sp.getString("avatar_url",null);
        String nickName=sp.getString("nick_name",null);
        int userId=sp.getInt("user_id",0);

        if (userId!=0){
            logIn.setVisibility(View.VISIBLE);
            logOut.setVisibility(View.GONE);

            //显示信息
            BGAImage.display(ivAvatar, R.mipmap.bga_pp_ic_holder_light,avatarUrl, BGABaseAdapterUtil.dp2px(54));
            tvNickname.setText(nickName);
            tvUserId.setText("ID号："+userId);
        }
        return pageFour;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //“登出”监听事件
            case R.id.rl_log_out:
                CommonUtil.skipActivityByFade(getActivity(), LoginRegisterActivity.class);
                break;

            //“登入”监听事件
            case R.id.rl_log_in:
                CommonUtil.skipActivityByFade(getActivity(), UserInfoActivity.class);
                break;

            //“设备管理”监听事件
            case R.id.rl_device_manager:
                requestLocPermToActivity( RequestCode.DEVICE_MANAGER_GPS_REQUEST_CODE, DeviceManagerActivity.class);
                break;

            //“我的河道”监听事件
            case R.id.rl_my_river:
                break;

            //“巡河记录”监听事件
            case R.id.rl_patrol_history:
                requestLocPermToActivity( RequestCode.PATROL_QUERY_GPS_REQUEST_CODE, PatrolQueryActivity.class);
                break;

            //“我的签到”监听事件
            case R.id.rl_attendance:
                CommonUtil.skipActivityByFade(getActivity(), AttendanceActivity.class);
                break;

            //“治理目标”监听事件
            case R.id.rl_my_flag:
                CommonUtil.skipActivityByFade(getActivity(), FlagActivity.class);
                break;

            //“考核评估”监听事件
            case R.id.rl_my_assess:
                CommonUtil.skipActivityByFade(getActivity(), AssessActivity.class);
                break;

            //“获得成就”监听事件
            case R.id.rl_my_grade:
                CommonUtil.skipActivityByFade(getActivity(), GradeActivity.class);
                break;

            //“帮助反馈”监听事件
            case R.id.rl_help:
                CommonUtil.skipActivityByFade(getActivity(), HelpActivity.class);
                break;

            //“关于应用”监听事件
            case R.id.rl_about:
                CommonUtil.skipActivityByFade(getActivity(), AboutActivity.class);
                break;

            //“系统设置”监听事件
            case R.id.rl_set:
                CommonUtil.skipActivityByFade(getActivity(), SetActivity.class);
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

            case RequestCode.DEVICE_MANAGER_GPS_REQUEST_CODE:
                if(CommonUtil.GpsIsOpened(Objects.requireNonNull(getActivity()))) {
                    CommonUtil.skipActivityByFade(getActivity(), DeviceManagerActivity.class);
                }
                break;

            case RequestCode.RIVER_PATROL_GPS_REQUEST_CODE:
                if(CommonUtil.GpsIsOpened(Objects.requireNonNull(getActivity()))) {
                    CommonUtil.skipActivityByFade(getActivity(), PatrolActivity.class);
                }
                break;
        }
    }

    /**位置权限请求*/
    @SuppressLint("CheckResult")
    public void requestLocPermToActivity(final int requestGpsCode, final Class descClass) {
        RxPermissions rxPermissions=new RxPermissions(getActivity());
        rxPermissions.requestEach(Manifest.permission.ACCESS_FINE_LOCATION)//位置权限
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
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
                                        FragmentPermUtil fragmentPermUtil = new FragmentPermUtil(getActivity(),PageFourFragment.this);
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