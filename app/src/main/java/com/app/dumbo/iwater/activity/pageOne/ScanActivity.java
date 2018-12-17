package com.app.dumbo.iwater.activity.pageOne;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.BaseActivity;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * Created by dumbo on 2018/3/30.
 */
public class ScanActivity extends BaseActivity {
    private static final int CHOOSE_PHOTO=2;

    private CaptureManager capture;
    private DecoratedBarcodeView my_dbv;
    private RelativeLayout torch_off,torch_on;//手电筒控件
    private RelativeLayout openAlbum;//打开相册控件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_scan);
        super.onCreate(savedInstanceState);

        capture = new CaptureManager(this, my_dbv);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    @Override
    public void initView() {
        super.initView();
        //设置状态栏颜色
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);

        my_dbv = findViewById(R.id.my_dbv);
        openAlbum=findViewById(R.id.rl_open_album);
        torch_on = findViewById(R.id.torch_on);
        torch_off = findViewById(R.id.torch_off);

        // 如果没有闪光灯功能，就去掉相关按钮
        if (!hasFlash()) {
            torch_off.setVisibility(View.INVISIBLE);
            torch_off.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        openAlbum.setOnClickListener(this);
        torch_on.setOnClickListener(this);
        torch_off.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            //打开相册按钮监听
            case R.id.rl_open_album:
                Intent intent=new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                startActivityForResult(intent,CHOOSE_PHOTO);
                break;

            //手电筒开按钮监听
            case R.id.torch_on:
                torch_off.setVisibility(View.VISIBLE);
                torch_on.setVisibility(View.INVISIBLE);
                my_dbv.setTorchOff();//关闭闪光灯
                break;

            //手电筒关按钮监听
            case R.id.torch_off:
                torch_off.setVisibility(View.INVISIBLE);
                torch_on.setVisibility(View.VISIBLE);
                my_dbv.setTorchOn();//开启闪光灯
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return my_dbv.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    // 判断是否有闪光灯功能
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }
}
