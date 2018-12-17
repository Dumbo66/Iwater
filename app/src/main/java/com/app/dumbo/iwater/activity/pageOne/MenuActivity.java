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
    }

    @Override
    public void initView() {
        super.initView();
        mapMonitor=findViewById(R.id.rl_map_monitor);
        mobileMonitor=findViewById(R.id.rl_mobile_monitor);
        riverPatrol=findViewById(R.id.rl_river_patrol);
        problemRecord=findViewById(R.id.rl_problem_record);
        undoTask=findViewById(R.id.rl_undo_task);
        peopleComplain=findViewById(R.id.rl_policy);
        supervision=findViewById(R.id.rl_river_info);
    }

    @Override
    public void setListener() {
        super.setListener();
        mapMonitor.setOnClickListener(this);
        mobileMonitor.setOnClickListener(this);
        riverPatrol.setOnClickListener(this);
        problemRecord.setOnClickListener(this);
        undoTask.setOnClickListener(this);
//        policy.setOnClickListener(this);
//        supervision.setOnClickListener(this);
//        more.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        //"地图监测"按钮监听
        switch (v.getId()){
            //"地图监测"按钮监听
            case R.id.rl_map_monitor:
                CommonUtil.skipActivityByFade(this,WaterMonitorActivity.class);
                break;

            //“移动监测”按钮监听
            case R.id.rl_mobile_monitor:
//                //动态权限请求
//                RxPermissions rxPermissions=new RxPermissions(this);
//                rxPermissions.requestEach(Manifest.permission.ACCESS_FINE_LOCATION)//位置权限
//                        .subscribe(new Consumer<Permission>() {
//                            @Override
//                            public void accept(Permission permission) throws Exception {
//                                if(permission.granted) {// 用户已经同意该权限
//                                    //判断GPS是否打开
//                                    if(CommonUtil.GpsIsOpened(this)){
//                                        CommonUtil.skipActivityByFade(this,MobileMonitorActivity.class);
//                                    }else{
//                                        DialogUtil.showOpenGpsDialog(this, RequestCode.MOBILE_MONITOR_GPS_REQUEST_CODE);
//                                    }
//                                }else if(permission.shouldShowRequestPermissionRationale) {
//                                    // 用户拒绝了该权限，未选中『不再询问』，
//                                    // 下次再次启动时，还会提示请求权限的对话框
//                                    new SweetAlertDialog(this)
//                                            .setContentText("移动监测需要位置权限，请开启位置权限，以正常使用相关功能！")
//                                            .setConfirmText("去开启")
//                                            .setCancelText("取消")
//                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                @Override
//                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                                    //跳转到应用详细界面
//                                                    Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                                    this.startActivity(intent);
//                                                    sweetAlertDialog.dismiss();
//                                                }
//                                            })
//                                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                @Override
//                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                                    sweetAlertDialog.dismiss();
//                                                }
//                                            })
//                                            .show();
//                                }else {
//                                    // 用户拒绝了该权限，并且选中『不再询问』
//                                    // 用户拒绝了该权限
//                                    new SweetAlertDialog(this)
//                                            .setContentText("移动监测需要位置权限，请开启位置权限，以正常使用相关功能！")
//                                            .setConfirmText("去开启")
//                                            .setCancelText("取消")
//                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                @Override
//                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                                    //跳转到应用详细界面
//                                                    Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                                    this.startActivity(intent);
//                                                    sweetAlertDialog.dismiss();
//                                                }
//                                            })
//                                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                @Override
//                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                                    sweetAlertDialog.dismiss();
//                                                }
//                                            })
//                                            .show();
//                                }
//                            }
//                        });
//                if(Build.VERSION.SDK_INT>=23){
//                    int checkLocationPermission= ActivityCompat.checkSelfPermission
//                            (MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
//                    if(checkLocationPermission!= PackageManager.PERMISSION_GRANTED){
//                        //无权限，请求权限
//                        ActivityCompat.requestPermissions(MainActivity.this,
//                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                RequestCode.MOBILE_MONITOR_REQUEST_PERMISSION_LOCATION_CODE);
//                    }else{
//
//                    }
//                }else{
//                    skipActivityByFade(MobileMonitorActivity.class);
//                }
//                CommonUtil.skipActivityByFade(this,MobileMonitorActivity.class);
                break;

            //"河湖巡查"按钮监听
            case R.id.rl_river_patrol:
                CommonUtil.skipActivityByFade(this,PatrolActivity.class);
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
                CommonUtil.skipActivityByFade(this,AddMomentActivity.class);
                break;

            //“事件处理”按钮监听
            case R.id.rl_undo_task:
                CommonUtil.skipActivityByFade(this,UndoTaskActivity.class);
                break;

            //“河湖信息”按钮监听
            case R.id.rl_river_info:
                CommonUtil.skipActivityByFade(this,RiverInfoActivity.class);
                break;

            //“政策法规”按钮监听
            case R.id.rl_policy:
                CommonUtil.skipActivityByFade(this,PolicyActivity.class);
                break;

            //“更多”按钮监听
            case R.id.rl_more:
                CommonUtil.skipActivityByFade(this,MenuActivity.class);
                break;
        }
    }
    

    /**控件监听*/
    private void listenWidget() {
        
        mapMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipToDestActivity(WaterMonitorActivity.class);
            }
        });

        
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
