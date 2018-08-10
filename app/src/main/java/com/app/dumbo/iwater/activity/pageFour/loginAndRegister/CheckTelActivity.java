package com.app.dumbo.iwater.activity.pageFour.loginAndRegister;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.MainActivity;
import com.app.dumbo.iwater.activity.superClass.WithBackActivity;
import com.app.dumbo.iwater.constant.ResponseCode;
import com.app.dumbo.iwater.retrofit2.entity.JwtReception;
import com.app.dumbo.iwater.retrofit2.entity.Users;
import com.app.dumbo.iwater.retrofit2.Retrofit2;
import com.app.dumbo.iwater.util.EncryptUtil;
import com.mob.MobSDK;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by dumbo on 2018/1/5.
 */

public class CheckTelActivity extends WithBackActivity {
    private RelativeLayout getVerCode;//"获取验证码"按钮
    private EditText code;//“输入验证码”文本框
    private TextView getVerCodeText;//"获取验证码"文本
    private String phone,password;//获取的电话号码和密码
    private int second=60;//重新获取验证码时间（60s）

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_check_tel);
        super.onCreate(savedInstanceState);

        getVerCode = findViewById(R.id.rl_get_verCode);
        getVerCodeText = findViewById(R.id.tv_get_verCode_text);
        code =  findViewById(R.id.et_code);
        Button login = findViewById(R.id.btn_login);

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
        System.out.println("11111111111111111111111111111111111"+phone+"|"+password);

        getVerCode.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
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
            }
        });

        //注册按钮事件监听
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verCode = code.getText().toString();//获取的验证码
                if (verCode.length() == 0) {
                    Toast.makeText(CheckTelActivity.this, "请输入验证码！", Toast.LENGTH_SHORT).show();
                }
                else if (verCode.length() < 6) {
                    Toast.makeText(CheckTelActivity.this, "请输入6位有效验证码！", Toast.LENGTH_SHORT).show();
                }
                else if(verCode.length()==6){
                    //提交短信验证码，在监听中返回
                    SMSSDK.submitVerificationCode("86", phone, verCode);
                }
            }
        });
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
                Log.i("AAAAAAAAAAAAAAAAAAAAAAA",event+"%"+result+"%"+data);
                if (result == SMSSDK.RESULT_COMPLETE) { //回调完成
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//获取验证码成功
                        Log.i("提示：", "获取验证码成功 ");
                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//验证成功
                        Log.i("提示：", "验证成功 ");
                        register(phone,password);//注册
                    }
                } else{
                    showVerCodeErroredDialog();
                }
            }
        }
    };

    /*注册*/
    private void register(String phone,String password){
        //使用MD5加密密码password
        String md5Pasw= EncryptUtil.getMD5Str(password);
        //使用RSA加密md5Pasw(以Base64编码的RSA加密的密码)
        String encryptedPasw= EncryptUtil.encrypt(md5Pasw);

        Users users=new Users();
        users.setPhone(phone);
        users.setPassword(encryptedPasw);

        Call<JwtReception> registerCall= Retrofit2.getService().registerByPasw(users);
        registerCall.enqueue(new Callback<JwtReception>() {
            @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
            @Override
            public void onResponse(@NonNull Call<JwtReception> call, @NonNull retrofit2.Response<JwtReception> response) {
                if(Objects.requireNonNull(response.body()).getCode()== ResponseCode.OK){
                    System.out.println("注册成功！");
                    System.out.println("code="+response.body().getCode());
                    System.out.println("data="+response.body().getData());
                    String accessJwt= Objects.requireNonNull(response.body()).getData().getAccessJwt();
                    String refreshJwt= Objects.requireNonNull(response.body()).getData().getRefreshJwt();

                    //注册成功后将服务器返回的jwt存入SharedPreferences
                    //新建SharedPreferences对象
                    SharedPreferences sp=getSharedPreferences("jwt",Context.MODE_PRIVATE);
                    //添加数据
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString("postAccessJwt", accessJwt);
                    editor.putString("postRefreshJwt", refreshJwt);
                    editor.commit();

                    //注册成功后跳转到主页面
                    Intent intent = new Intent(CheckTelActivity.this, MainActivity.class);
                    intent.putExtra("page", 4);
                    //在此activity启动之前的任何与此activity相关联的task都会被清除
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JwtReception> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
                System.out.println("注册失败！");
            }
        });
    }

    /*验证码错误提示框*/
    public void showVerCodeErroredDialog(){
        new SweetAlertDialog(CheckTelActivity.this)
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
            Intent intent = new Intent(CheckTelActivity.this, LoginRegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return super.onKeyDown(keyCode, event);
    }
}
