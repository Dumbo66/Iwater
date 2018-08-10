package com.app.dumbo.iwater.activity.pageOne;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.WithBackActivity;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * Created by dumbo on 2018/3/30.
 */
public class ScanActivity extends WithBackActivity {
    private static final int CHOOSE_PHOTO=2;

    private final int LIGHT_TAG=20;//光线传感器阈值,小于20时出现打开手电筒的标志
    private SensorManager sensorManager;
    private SensorEventListener listener;
    private CaptureManager capture;
    private DecoratedBarcodeView my_dbv;
    private RelativeLayout torch_off,torch_on;
    private RelativeLayout openAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_scan);
        super.onCreate(savedInstanceState);

        //控件初始化
        initView();

        //控件监听
        listenWidget();

        capture = new CaptureManager(this, my_dbv);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    /**控件初始化*/
    public void initView(){
        my_dbv = findViewById(R.id.my_dbv);
        torch_off = findViewById(R.id.torch_off);
        torch_on = findViewById(R.id.torch_on);
        openAlbum=findViewById(R.id.rl_open_album);
    }

    /**控件监听*/
    private void listenWidget() {
        // 如果没有闪光灯功能，就去掉相关按钮
        if (!hasFlash()) {
            torch_off.setVisibility(View.INVISIBLE);
            torch_off.setVisibility(View.INVISIBLE);
        }

//        //注册光线传感器
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);//光线传感器
//        listener = new SensorEventListener() {
//            @Override
//            public void onSensorChanged(SensorEvent sensorEvent) {
//                //当传感器监测到的数值发生变化时
//                float value=sensorEvent.values[0]; // values数组中第一个值就是当前的光照强度
//                Log.i("TTTTTTTTTTTTTTTTTTTT",""+value);
//                if(value<LIGHT_TAG){
//                    torch_off.setVisibility(View.VISIBLE);
//                    torch_on.setVisibility(View.INVISIBLE);
//                }else{
//                    torch_off.setVisibility(View.INVISIBLE);
//                    torch_on.setVisibility(View.INVISIBLE);
//                }
//            }
//
//            @Override
//            public void onAccuracyChanged(Sensor sensor, int i) {
//                //当传感器精度发生变化时
//            }
//        };
//        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);

        openAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                startActivityForResult(intent,CHOOSE_PHOTO);
            }
        });

        torch_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                torch_off.setVisibility(View.INVISIBLE);
                torch_on.setVisibility(View.VISIBLE);
                my_dbv.setTorchOn();//开启闪光灯
            }
        });
        torch_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                torch_off.setVisibility(View.VISIBLE);
                torch_on.setVisibility(View.INVISIBLE);
                my_dbv.setTorchOff();//关闭闪光灯
            }
        });
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
        //注销监听器
        if (sensorManager != null) {
            sensorManager.unregisterListener(listener);
        }
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
