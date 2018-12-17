package com.app.dumbo.iwater.activity.pageFour.loginAndRegister;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.AnimFadeActivity;
import com.jaeger.library.StatusBarUtil;
import com.mob.MobSDK;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by dumbo on 2018/1/5.
 */

public class CheckPhoneActivity extends AnimFadeActivity {
    private RelativeLayout getVerCode;//"获取验证码"按钮
    private EditText etCode;//“输入验证码”文本框
    private TextView getVerCodeText;//"获取验证码"文本
    private String phone,password;//获取的电话号码和密码
    private int second=60;//重新获取验证码时间（60s）

    private Button next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_check_phone);
        super.onCreate(savedInstanceState);

        //初始化SMSSDK
        MobSDK.init(this,"24b5741957200","f06ffbe8f59eb009bed06ffd261243fc");
        //注册一个事件接收器
        EventHandler eventHandler=new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);//注册短信回调

        //接收来自注册页面跳转值
        Intent it=getIntent();
        phone =it.getStringExtra("phone");
        password=it.getStringExtra("password");
    }

    @Override
    public void initView() {
        super.initView();
        //设置状态栏颜色
        StatusBarUtil.setColorForSwipeBack(this,getResources().getColor(R.color.colorClickedBack), 0);

        getVerCode = findViewById(R.id.rl_get_verCode);
        getVerCodeText = findViewById(R.id.tv_get_verCode_text);
        etCode =  findViewById(R.id.et_code);
        next = findViewById(R.id.btn_next);
    }

    @Override
    public void setListener() {
        super.setListener();
        getVerCode.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            //获取验证码按钮监听
            case R.id.rl_get_verCode:
                SMSSDK.getVerificationCode("86", phone);
                //设置“获取验证码”不可点击&半透明显示
                getVerCode.setClickable(false);
                getVerCodeText.setTextColor(getResources().getColor(R.color.colorTranslucentGreen));
                getVerCodeText.setText("重新发送(" + second + "s)");
                getVerCodeText.setTextSize(14);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = second; i > 0; i--) {
                            try {
                                Thread.sleep(1000);//睡眠1s
                                second--;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            handler.sendEmptyMessage(-2);// setText("重新发送（"+second+"s)");
                        }
                        handler.sendEmptyMessage(-3);// setText("获取验证码");
                    }
                }).start();
                break;

            //登录按钮监听
            case R.id.btn_next:
                String verCode = etCode.getText().toString();//获取的验证码
                if (verCode.length() == 0) {
                    Toast.makeText(CheckPhoneActivity.this, "请输入验证码！", Toast.LENGTH_SHORT).show();
                }
                else if (verCode.length() < 6) {
                    Toast.makeText(CheckPhoneActivity.this, "请输入6位有效验证码！", Toast.LENGTH_SHORT).show();
                }
                else if(verCode.length()==6){
                    //提交短信验证码，在监听中返回
                    SMSSDK.submitVerificationCode("86", phone, verCode);
                }
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==-2){
                getVerCodeText.setText("重新发送(" + second + "s)");
            }else if(msg.what==-3){
                getVerCodeText.setText("重新发送");
                //计时结束，恢复原状
                getVerCode.setClickable(true);
                getVerCodeText.setTextSize(16);
                getVerCodeText.setTextColor(getResources().getColor(R.color.colorGreen));
            }else{
                int event=msg.arg1;
                int result=msg.arg2;
                Object data=msg.obj;
                if (result == SMSSDK.RESULT_COMPLETE) { //回调完成
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//获取验证码成功
                        System.out.println("获取验证码成功 ");
                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//验证成功
                        System.out.println("验证成功 ");
                        //跳转到填写资料页面
                        Intent intent=new Intent(CheckPhoneActivity.this,FillUserInfoActivity.class);
                        intent.putExtra("phone",phone);
                        intent.putExtra("password",password);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    }
                } else{
                    showVerCodeErroredDialog();
                }
            }
        }
    };

    /*验证码错误提示框*/
    public void showVerCodeErroredDialog(){
        new SweetAlertDialog(CheckPhoneActivity.this)
                .setContentText("验证码输入错误，请重试！")
                .setConfirmText("确定")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    /*返回*/
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            finish();
            Intent intent = new Intent(CheckPhoneActivity.this, LoginRegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return super.onKeyDown(keyCode, event);
    }
}
