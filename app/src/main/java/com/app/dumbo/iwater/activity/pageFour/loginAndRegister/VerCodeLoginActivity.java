package com.app.dumbo.iwater.activity.pageFour.loginAndRegister;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.app.dumbo.iwater.activity.superClass.BaseActivity;
import com.app.dumbo.iwater.constant.Common;
import com.app.dumbo.iwater.constant.ResponseCode;
import com.app.dumbo.iwater.retrofit2.Retrofit2;
import com.app.dumbo.iwater.retrofit2.entity.Users;
import com.app.dumbo.iwater.util.CommonUtil;
import com.jaeger.library.StatusBarUtil;
import com.jayway.jsonpath.JsonPath;
import com.mob.MobSDK;

import java.io.IOException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dumbo on 2017/11/17.
 */

public class VerCodeLoginActivity extends BaseActivity {
    private RelativeLayout clearPhone;//清除手机号栏内容按钮
    private EditText etTel, etPasw;//手机号栏和密码栏
    private Button login;//登录按钮
    private RelativeLayout getVerCode;//获取验证码控件
    private TextView getVerCodeText;//"获取验证码"文本
    private int second=60;//重新获取验证码事件（60s）
    private String phone;//获取的电话号码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_vercode_login);
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

        //接收来自密码登录页面跳转值
        Intent it=getIntent();
        String tel=it.getStringExtra("tel");
        int flag=it.getIntExtra("flag",0);
        if(flag==1){
            etTel.setText(tel);
            etPasw.requestFocus();
        }
    }

    @Override
    public void initView() {
        super.initView();
        //设置状态栏颜色
        StatusBarUtil.setColorForSwipeBack(this,getResources().getColor(R.color.colorClickedBack), 0);

        clearPhone = findViewById(R.id.rl_clear_phone);
        etTel = findViewById(R.id.et_tel);
        etPasw = findViewById(R.id.et_pasw);
        getVerCode = findViewById(R.id.rl_get_verCode);
        getVerCodeText = findViewById(R.id.tv_get_verCode_text);
        login =  findViewById(R.id.btn_next);
    }

    @Override
    public void setListener() {
        super.setListener();
        //手机号EditText监听
        etTel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //若输入非空，显示清除按钮,否则不显示
                if (!(s.length() == 0)) {
                    clearPhone.setVisibility(View.VISIBLE);
                } else {
                    clearPhone.setVisibility(View.INVISIBLE);
                }
            }
        });

        clearPhone.setOnClickListener(this);
        getVerCode.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            //返回按钮监听
            case R.id.back:
                finish();
                Intent intent=new Intent(VerCodeLoginActivity.this,LoginRegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;

            //清空手机号栏
            case R.id.rl_clear_phone:
                etTel.setText("");
                break;

            //获取验证码
            case R.id.rl_get_verCode:
                phone = etTel.getText().toString();//获取的电话号码
                if (phone.length() == 0) {
                    Toast.makeText(VerCodeLoginActivity.this, "请输入手机号！", Toast.LENGTH_SHORT).show();
                }//判断手机号为11位，且首位数字为1，每位均为数字
                else if (!CommonUtil.isPhone(phone)) {
                    Toast.makeText(VerCodeLoginActivity.this, "请输入有效手机号！", Toast.LENGTH_SHORT).show();
                } else {
                    //请求获取短信验证码，在监听中返回
                    SMSSDK.getVerificationCode("86", phone);
                    //设置“获取验证码”不可点击&半透明显示
                    getVerCode.setClickable(false);
                    getVerCodeText.setTextColor(getResources().getColor(R.color.colorTranslucentGreen));
                    getVerCodeText.setText("重新发送("+second+"s)");
                    getVerCodeText.setTextSize(14);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i=second;i>0;i--){
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
                break;

            //登录按钮
            case R.id.btn_next:
                phone = etTel.getText().toString();//获取的电话号码
                String verCode = etPasw.getText().toString();//获取的验证码
                if (phone.length() == 0) {
                    Toast.makeText(VerCodeLoginActivity.this, "请输入手机号！", Toast.LENGTH_SHORT).show();
                }//判断手机号为11位，且首位数字为1，每位均为数字
                else if (!CommonUtil.isPhone(phone)) {
                    Toast.makeText(VerCodeLoginActivity.this, "请输入有效手机号！", Toast.LENGTH_SHORT).show();
                }
                else if (verCode.length() == 0) {
                    Toast.makeText(VerCodeLoginActivity.this, "请输入验证码！", Toast.LENGTH_SHORT).show();
                }
                else if (verCode.length() < 6) {
                    Toast.makeText(VerCodeLoginActivity.this, "请输入6位有效验证码！", Toast.LENGTH_SHORT).show();
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
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==-2){
                getVerCodeText.setText("重新发送("+second+"s)");
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
                        Log.i("提示：", "获取验证码成功 ");
                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//验证成功
                        Log.i("提示：", "验证成功 ");
                        Users users=new Users();
                        users.setPhone(phone);
                        loginByVerCode(users);
                    }
                } else{
                    showVerCodeErroredDialog();
                }
            }
        }
    };

    /**验证码登录*/
    private void loginByVerCode(Users users) {
        Call<ResponseBody> loginByVewCodeCall= Retrofit2.getService().loginByVerCode(users);
        loginByVewCodeCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseStr=null;
                try {
                    responseStr=response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(responseStr!=null){
                    int code= JsonPath.read(responseStr,"$.code");
                    String message= JsonPath.read(responseStr,"$.message");
                    switch (code){
                        case ResponseCode.OK://登录成功
                            String accessJwt= JsonPath.read(responseStr,"$.data.access_jwt");
                            String refreshJwt= JsonPath.read(responseStr,"$.data.refresh_jwt");
                            String avatarUrl= "http://"+ Common.HOST_IP +JsonPath.read(responseStr,"$.data.avatar_url");
                            String nickName=JsonPath.read(responseStr,"$.data.nick_name");
                            int userId=JsonPath.read(responseStr,"$.data.user_id");
                            String sex=JsonPath.read(responseStr,"$.data.sex");

                            //新建SharedPreferences对象
                            SharedPreferences sp=getSharedPreferences("user_info", Context.MODE_PRIVATE);
                            //添加数据(存储jwt和用户信息)
                            SharedPreferences.Editor editor=sp.edit();
                            editor.putString("access_jwt", accessJwt);
                            editor.putString("refresh_jwt", refreshJwt);
                            editor.putString("avatar_url",avatarUrl);
                            editor.putString("nick_name",nickName);
                            editor.putInt("user_id",userId);
                            editor.putString("sex",sex);
                            editor.apply();

                            login.setText("登录中……");

                            //跳转到主页第4页
                            Intent intent=new Intent(VerCodeLoginActivity.this,MainActivity.class);
                            intent.putExtra("page",3);
                            //在此activity启动之前，其前面的activity都会被清除，
                            //A-->B-->C-->A   跳转回A时，B，C 被finish()；
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                            break;

                        case ResponseCode.UNREGISTERED://手机号未注册
                            Toast.makeText(VerCodeLoginActivity.this,"该手机号尚未注册，请完善相关信息！",Toast.LENGTH_SHORT).show();
                            Intent it=new Intent(VerCodeLoginActivity.this,SetPaswActivity.class);
                            it.putExtra("phone",phone);
                            startActivity(it);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(VerCodeLoginActivity.this,"验证码登录失败！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*验证码错误提示框*/
    public void showVerCodeErroredDialog(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(VerCodeLoginActivity.this);
        dialog.setMessage("验证码输入错误，请重试！");
        dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //退出淡入淡出效果
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            Intent intent=new Intent(VerCodeLoginActivity.this,LoginRegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();//注销(必须)
    }


}
