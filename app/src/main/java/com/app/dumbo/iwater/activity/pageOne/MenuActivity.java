package com.app.dumbo.iwater.activity.pageOne;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.pageOne.mapMonitor.MapMonitorActivity;
import com.app.dumbo.iwater.activity.pageThree.PeopleActivity;
import com.app.dumbo.iwater.activity.superClass.AnimFadeActivity;
import com.app.dumbo.iwater.constant.RequestCode;
import com.app.dumbo.iwater.util.CommonUtil;

/**
 * Created by dumbo on 2018/3/23.
 */

public class MenuActivity extends AnimFadeActivity {
    private static final int RIVER_PATROL_GPS_REQUEST_CODE =1;
    private static final int RIVER_PATROL_REQUEST_PERMISSION_LOCATION_CODE=3;

    private RelativeLayout mapMonitor;
    private RelativeLayout mobileMonitor;
    private RelativeLayout riverPatrol;
    private RelativeLayout problemRecord;
    private RelativeLayout undoTask;
    private RelativeLayout peopleComplain;
    private RelativeLayout supervision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_menu);
        super.onCreate(savedInstanceState);

        //控件初始化
        initView();

        //控件监听
        listenWidget();

    }

    /**控件初始化*/
    public void initView(){
        mapMonitor=findViewById(R.id.rl_map_monitor);
        mobileMonitor=findViewById(R.id.rl_mobile_monitor);
        riverPatrol=findViewById(R.id.rl_river_patrol);
        problemRecord=findViewById(R.id.rl_problem_record);
        undoTask=findViewById(R.id.rl_undo_task);
        peopleComplain=findViewById(R.id.rl_policy);
        supervision=findViewById(R.id.rl_river_info);
    }

    /**控件监听*/
    private void listenWidget() {
        //"地图监测"按钮监听
        mapMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipToDestActivity(MapMonitorActivity.class);
            }
        });

        //“移动监测”按钮监听
        mobileMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipToDestActivity(MobileMonitorActivity.class);
            }
        });

        //"河湖巡查"按钮监听
        riverPatrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT>=23){
                    int checkLocationPermission= ActivityCompat.checkSelfPermission
                            (MenuActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                    if(checkLocationPermission!= PackageManager.PERMISSION_GRANTED){
                        //无权限，请求权限
                        ActivityCompat.requestPermissions(MenuActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                RIVER_PATROL_REQUEST_PERMISSION_LOCATION_CODE);
                    }else{
                        //有权限，判断GPS是否打开
                        if(!CommonUtil.GpsIsOpened(MenuActivity.this)){
                            showOpenGpsDialog(RequestCode.RIVER_PATROL_GPS_REQUEST_CODE);
                        }else{
                            skipToDestActivity(PatrolActivity.class);
                        }
                    }
                }else{
                    skipToDestActivity(PatrolActivity.class);
                }
            }
        });

        //“问题记录”按钮监听
        problemRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipToDestActivity(AddMomentActivity.class);
            }
        });

        //“待办事宜”按钮监听
        undoTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipToDestActivity(UndoTaskActivity.class);
            }
        });

        //“群众投诉”按钮监听
        peopleComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipToDestActivity(PeopleActivity.class);
            }
        });

        //“督办事务”按钮监听
        supervision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipToDestActivity(RiverInfoActivity.class);
            }
        });
    }

    /**跳转目的Activity返回后响应*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== RIVER_PATROL_GPS_REQUEST_CODE){
            if(CommonUtil.GpsIsOpened(MenuActivity.this)){
                skipToDestActivity(PatrolActivity.class);
            }
        }else if(requestCode == RequestCode.MOBILE_MONITOR_GPS_REQUEST_CODE){
            if(CommonUtil.GpsIsOpened(MenuActivity.this)){
                skipToDestActivity(MobileMonitorActivity.class);
            }
        }
    }

    /**权限请求响应*/
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case RIVER_PATROL_REQUEST_PERMISSION_LOCATION_CODE:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //打开GPS
                    if(!CommonUtil.GpsIsOpened(MenuActivity.this)){
                        showOpenGpsDialog(RIVER_PATROL_GPS_REQUEST_CODE);
                    }else{
                        skipToDestActivity(PatrolActivity.class);
                    }
                }else{
                    Toast.makeText(this,
                            "获取定位权限失败，请手动获取！",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**跳转到目的Activity*/
    private void skipToDestActivity(Class desClass) {
        Intent intent=new Intent(this,desClass);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    /**打开GPS对话框*/
    private void showOpenGpsDialog(final int code) {
        AlertDialog.Builder dialog= new AlertDialog.Builder(MenuActivity.this);
        dialog.setMessage("河湖巡查需要GPS权限，请打开GPS");
        dialog.setPositiveButton("去打开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //跳转到GPS设置界面
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, code);
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
