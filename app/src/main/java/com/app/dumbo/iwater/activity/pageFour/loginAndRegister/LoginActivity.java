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
import com.app.dumbo.iwater.activity.superClass.WithBackActivity;
import com.app.dumbo.iwater.constant.ResponseCode;
import com.app.dumbo.iwater.retrofit2.Retrofit2;
import com.app.dumbo.iwater.retrofit2.entity.JwtReception;
import com.app.dumbo.iwater.retrofit2.entity.Users;
import com.app.dumbo.iwater.util.EncryptUtil;
import com.app.dumbo.iwater.util.CommonUtil;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dumbo 2017/11/15.
 */

public class LoginActivity extends WithBackActivity {
    private EditText tel, pasw;//手机号框、密码框
    private String phone,password;//获取的手机号和密码
    private RelativeLayout closedEye, openedEye, clearPhone, clearPasw;//查看密码和清空文本区
    private Button login; //登录按钮

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);

        tel =  findViewById(R.id.et_tel);
        pasw =  findViewById(R.id.et_pasw);
        closedEye =  findViewById(R.id.rl_closed_eye);
        openedEye = findViewById(R.id.rl_opened_eye);
        clearPhone =  findViewById(R.id.rl_clear_tel);
        clearPasw =  findViewById(R.id.rl_clear_pasw);
        login =  findViewById(R.id.btn_login);
        TextView verCodeLogin = findViewById(R.id.tv_ver_code_login);
        TextView forgetPasw = findViewById(R.id.tv_forget_pasw);

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
                    clearPhone.setVisibility(View.VISIBLE);
                }else{
                    clearPhone.setVisibility(View.INVISIBLE);
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

        //清空手机号栏
        clearPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tel.setText("");
            }
        });

        //清空密码栏
        clearPasw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasw.setText("");
            }
        });

        //睁眼睛
        closedEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closedEye.setVisibility(View.INVISIBLE);
                openedEye.setVisibility(View.VISIBLE);
                //设置密码可见
                pasw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
        });

        //闭眼睛
        openedEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openedEye.setVisibility(View.INVISIBLE);
                closedEye.setVisibility(View.VISIBLE);
                //设置密码不可见
                pasw.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        //“获取验证码”登录界面跳转
        verCodeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,VerCodeLoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        //“登录”按钮事件监听
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入框内容
                phone = tel.getText().toString();
                password = pasw.getText().toString();

                if (phone.length() == 0) {
                    Toast.makeText(LoginActivity.this,
                            "请输入手机号！", Toast.LENGTH_SHORT).show();
                    //判断手机号为11位，且首位数字为1，每位均为数字
                }
                else if (CommonUtil.isPhone(phone)) {
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
            }
        });

        //接收来自注册页面跳转值
        Intent it=getIntent();
        String tel=it.getStringExtra("tel");
        int flag=it.getIntExtra("flag",0);
        if(flag==1){
            this.tel.setText(tel);
            pasw.requestFocus();
        }
    }

    /*登录并跳转*/
    private void login(String phone,String password) {
        //使用MD5加密密码password
        String md5Pasw= EncryptUtil.getMD5Str(password);

        //使用RSA加密md5Pasw
        String encryptedPasw= EncryptUtil.encrypt(md5Pasw);

        Users users=new Users();
        users.setPhone(phone);
        users.setPassword(encryptedPasw);

        Call<JwtReception> loginCall= Retrofit2.getService().loginByPasw(users);
        loginCall.enqueue(new Callback<JwtReception>() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onResponse(@NonNull Call<JwtReception> call,
                                   @NonNull Response<JwtReception> response) {
                int code=response.body().getCode();
                String message=response.body().getMessage();

                switch (code){
                    case ResponseCode.OK://登录成功
                        String accessJwt= Objects.requireNonNull(response.body()).getData().getAccessJwt();
                        String refreshJwt= Objects.requireNonNull(response.body()).getData().getRefreshJwt();

                        //注册成功后将服务器返回的jwt存入SharedPreferences
                        //新建SharedPreferences对象
                        SharedPreferences sp=getSharedPreferences("jwt", Context.MODE_PRIVATE);
                        //添加数据
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putString("postAccessJwt", accessJwt);
                        editor.putString("postRefreshJwt", refreshJwt);
                        editor.commit();

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

            @Override
            public void onFailure(@NonNull Call<JwtReception> call, @NonNull Throwable t) {
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
