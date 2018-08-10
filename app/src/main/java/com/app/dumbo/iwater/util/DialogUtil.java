package com.app.dumbo.iwater.util;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;

import com.app.dumbo.iwater.activity.pageOne.PatrolActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by dumbo on 2018/8/6
 **/

public class DialogUtil {
    /**打开GPS对话框*/
    public static void showOpenGpsDialog(final Activity activity,int requestCode) {
        new SweetAlertDialog(activity)
                .setContentText("河湖巡查需要GPS权限，请打开GPS！")
                .setConfirmText("去打开")
                .setCancelText("取消")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        //跳转到GPS设置界面
                        Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivity(intent);
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
}
