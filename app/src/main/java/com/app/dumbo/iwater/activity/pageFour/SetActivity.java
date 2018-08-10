package com.app.dumbo.iwater.activity.pageFour;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.MainActivity;
import com.app.dumbo.iwater.activity.superClass.WithBackActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by dumbo on 2017/10/27.
 */

public class SetActivity extends WithBackActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_set);
        super.onCreate(savedInstanceState);
        RelativeLayout rlLogOut=findViewById(R.id.rl_log_out);

        rlLogOut.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(View v) {
                showExitLoginDialog();
            }
        });
    }

    /*显示退出登录对话框*/
    private void showExitLoginDialog() {
        new SweetAlertDialog(SetActivity.this)
                .setContentText("确定退出登录吗？")
                .setConfirmText("确定")
                .setCancelText("取消")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        SharedPreferences sp=getSharedPreferences("jwt", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sp.edit();
                        editor.remove("postAccessJwt");
                        editor.remove("postRefreshJwt");
                        editor.commit();

                        //Activity跳转
                        Intent intent=new Intent(SetActivity.this,MainActivity.class);
                        intent.putExtra("page",4);
                        intent.putExtra("login_state",0);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
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