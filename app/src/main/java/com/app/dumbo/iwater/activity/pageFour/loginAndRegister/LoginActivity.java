package com.app.dumbo.iwater.activity.pageFour.loginAndRegister;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.MainActivity;
import com.app.dumbo.iwater.activity.superClass.AnimFadeActivity;
import com.app.dumbo.iwater.constant.Common;
import com.app.dumbo.iwater.constant.ResponseCode;
import com.app.dumbo.iwater.retrofit2.Retrofit2;
import com.app.dumbo.iwater.retrofit2.entity.Users;
import com.app.dumbo.iwater.util.CommonUtil;
import com.app.dumbo.iwater.util.EncryptUtil;
import com.jaeger.library.StatusBarUtil;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dumbo 2017/11/15.
 */

public class LoginActivity extends AnimFadeActivity {
    private EditText tel, pasw;//手机号框、密码框
    private String phone,password;//获取的手机号和密码
    private RelativeLayout closedEye, openedEye, clearTel, clearPasw;//查看密码和清空文本区
    private Button login; //登录按钮
    private TextView verCodeLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);

        super.setListener();
        //接收来自注册页面跳转值
        Intent it=getIntent();
        String tel=it.getStringExtra("tel");
        int flag=it.getIntExtra("flag",0);
        if(flag==1){
            this.tel.setText(tel);
            pasw.requestFocus();
        }
    }

    @Override
    public void initView() {
        super.initView();
        //设置状态栏颜色
        StatusBarUtil.setColorForSwipeBack(this,getResources().getColor(R.color.colorClickedBack), 0);

        tel =  findViewById(R.id.et_tel);
        pasw =  findViewById(R.id.et_pasw);
        closedEye =  findViewById(R.id.rl_closed_eye);
        openedEye = findViewById(R.id.rl_opened_eye);
        clearTel =  findViewById(R.id.rl_clear_phone);
        clearPasw =  findViewById(R.id.rl_clear_pasw);
        login =  findViewById(R.id.btn_next);
        verCodeLogin = findViewById(R.id.tv_ver_code_login);
        TextView forgetPasw = findViewById(R.id.tv_forget_pasw);
    }

    @Override
    public void setListener() {
        clearTel.setOnClickListener(this);
        clearPasw.setOnClickListener(this);
        openedEye.setOnClickListener(this);
        closedEye.setOnClickListener(this);
        verCodeLogin.setOnClickListener(this);
        login.setOnClickListener(this);

        //"手机号EditText"监听
        tel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                //若输入非空，显示清除按钮,否则不显示
                if(s.length()!=0){
                    clearTel.setVisibility(View.VISIBLE);
                }else{
                    clearTel.setVisibility(View.INVISIBLE);
                }
            }
        });

        //"密码EditText"监听
        pasw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                //若输入非空，显示清除按钮。否则不显示
                if(!(s.length()==0)){
                    clearPasw.setVisibility(View.VISIBLE);
                }else{
                    clearPasw.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch(v.getId()){
            //清空手机号栏
            case R.id.rl_clear_phone:
                tel.setText("");
                break;

            //清空密码栏
            case R.id.rl_clear_pasw:
                pasw.setText("");
                break;

            //睁眼睛（显示密码）监听
            case R.id.rl_opened_eye:
                openedEye.setVisibility(View.INVISIBLE);
                closedEye.setVisibility(View.VISIBLE);
                //设置密码不可见
                pasw.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;

            //闭眼睛（隐藏密码）监听
            case R.id.rl_closed_eye:
                closedEye.setVisibility(View.INVISIBLE);
                openedEye.setVisibility(View.VISIBLE);
                //设置密码可见
                pasw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                break;

            //“获取验证码”登录界面跳转
            case R.id.tv_ver_code_login:
                CommonUtil.skipActivityByFade(this,VerCodeLoginActivity.class);
                break;

            //“登录”按钮事件监听
            case R.id.btn_next:
                //获取输入框内容
                phone = tel.getText().toString();
                password = pasw.getText().toString();

                if (phone.length() == 0) {
                    Toast.makeText(LoginActivity.this,
                            "请输入手机号！", Toast.LENGTH_SHORT).show();
                    //判断手机号为11位，且首位数字为1，每位均为数字
                }
                else if (!CommonUtil.isPhone(phone)) {
                    Toast.makeText(LoginActivity.this,
                            "请输入有效手机号！", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() == 0) {
                    Toast.makeText(LoginActivity.this,
                            "请输入密码！", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() < 6) {
                    Toast.makeText(LoginActivity.this,
                            "请输入6-16位有效密码！", Toast.LENGTH_SHORT).show();
                }
                else if(password.matches("[0-9]+")){
                    Toast.makeText(LoginActivity.this,
                            "密码不能为纯数字！", Toast.LENGTH_SHORT).show();
                }
                else {
                    login(phone,password);
                }
                break;
        }
    }

    /*登录并跳转*/
    private void login(String phone,String password) {
        //加密
        String encryptedPasw= EncryptUtil.encrypt(password);

        Users users=new Users();
        users.setPhone(phone);
        users.setPassword(encryptedPasw);

        Call<ResponseBody> loginCall= Retrofit2.getService().loginByPasw(users);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
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
                            String avatarUrl= JsonPath.read(responseStr,"$.data.avatar_url");
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
                            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                            intent.putExtra("page",3);
                            //在此activity启动之前，其前面的activity都会被清除，
                            //A-->B-->C-->A   跳转回A时，B，C 被finish()；
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                            break;

                        case ResponseCode.UNREGISTERED://手机号未注册
                            showUnregisteredDialog(message);
                            break;

                        case ResponseCode.PHONE_OR_PASW_ERROR://手机号或密码错误
                            showErroredDialog(message);
                            break;

                        case ResponseCode.PASW_IS_NULL://账号未设置密码
                            showPasswordIsNullDialog(message);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                System.out.println("登录失败");
                System.out.println(t.getMessage());
            }
        });
    }

    /*显示手机号或密码错误对话框 */
    private void showErroredDialog(String message) {
        new SweetAlertDialog(LoginActivity.this)
                .setContentText(message)
                .setConfirmText("重新输入")
                .setCancelText("找回密码")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
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

    /*显示未注册对话框*/
    private void showUnregisteredDialog(String message) {
        new SweetAlertDialog(LoginActivity.this)
                .setContentText(message)
                .setConfirmText("短信登录")
                .setCancelText("立即注册")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent=new Intent(LoginActivity.this,VerCodeLoginActivity.class);
                        intent.putExtra("tel", phone);
                        intent.putExtra("flag",1);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        sweetAlertDialog.dismiss();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                        intent.putExtra("tel", phone);
                        intent.putExtra("flag",1);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        finish();
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }

    /*显示已注册，未设置密码对话框*/
    private void showPasswordIsNullDialog(String message){
        new SweetAlertDialog(LoginActivity.this)
                .setContentText(message)
                .setConfirmText("短信登录")
                .setCancelText("设置密码")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent=new Intent(LoginActivity.this,VerCodeLoginActivity.class);
                        intent.putExtra("tel", phone);
                        intent.putExtra("flag",1);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        sweetAlertDialog.dismiss();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent=new Intent(LoginActivity.this,SetPaswActivity.class);
                        intent.putExtra("tel", phone);
                        intent.putExtra("flag",1);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        sweetAlertDialog.dismiss();
                        finish();
                    }
                })
                .show();
    }

    @Override
    /*按返回键事件*/
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
        return super.onKeyDown(keyCode, event);
    }

}
