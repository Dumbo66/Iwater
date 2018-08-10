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
import com.app.dumbo.iwater.activity.pageFour.AboutActivity;
import com.app.dumbo.iwater.activity.pageFour.AssessActivity;
import com.app.dumbo.iwater.activity.pageFour.AttendanceActivity;
import com.app.dumbo.iwater.activity.pageFour.FlagActivity;
import com.app.dumbo.iwater.activity.pageFour.GradeActivity;
import com.app.dumbo.iwater.activity.pageFour.HelpActivity;
import com.app.dumbo.iwater.activity.pageFour.SetActivity;
import com.app.dumbo.iwater.activity.pageFour.deviceManager.DeviceManagerActivity;
import com.app.dumbo.iwater.activity.pageFour.loginAndRegister.LoginRegisterActivity;
import com.app.dumbo.iwater.activity.pageFour.patrolRecord.PatrolQueryActivity;
import com.app.dumbo.iwater.util.CommonUtil;

/**
 * Created by dumbo on 2018/8/10
 **/

public class PageFourFragment extends Fragment implements View.OnClickListener{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View pageFour = inflater.inflate(R.layout.fragement_page_four, null);

        //初始化控件
        RelativeLayout logOut = pageFour.findViewById(R.id.rl_log_out);
        RelativeLayout logIn =  pageFour.findViewById(R.id.rl_log_in);
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
                break;

            //“设备管理”监听事件
            case R.id.rl_device_manager:
                CommonUtil.skipActivityByFade(getActivity(), DeviceManagerActivity.class);
                break;

            //“我的河道”监听事件
            case R.id.rl_my_river:
                break;

            //“巡河记录”监听事件
            case R.id.rl_patrol_history:
                CommonUtil.skipActivityByFade(getActivity(), PatrolQueryActivity.class);
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
}