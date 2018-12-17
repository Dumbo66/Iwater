package com.app.dumbo.iwater.activity.pageFour.loginAndRegister;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.AnimFadeActivity;
import com.app.dumbo.iwater.constant.ResponseCode;
import com.app.dumbo.iwater.retrofit2.Retrofit2;
import com.app.dumbo.iwater.retrofit2.entity.Users;
import com.app.dumbo.iwater.retrofit2.entity.reception.Reception;
import com.app.dumbo.iwater.util.CommonUtil;
import com.jaeger.library.StatusBarUtil;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by dumbo on 2017/11/15.
 */

public class RegisterActivity extends AnimFadeActivity {
    private Button next;
    private EditText et_tel,et_pasw,et_rpasw;
    private RelativeLayout closed_eye1,opened_eye1,closed_eye2,opened_eye2,
                           clear_tel,clear_pasw,clear_rpasw;
    private String phone,password,rpassword;

    protected void onCreate( Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);
        super.onCreate(savedInstanceState);

        //接收来自登录页面跳转值
        Intent it=getIntent();
        String tel=it.getStringExtra("tel");
        int flag=it.getIntExtra("flag",0);
        if(flag==1){
            et_tel.setText(tel);
            et_pasw.requestFocus();
        }
    }

    @Override
    public void initView() {
        super.initView();
        //设置状态栏颜色
        StatusBarUtil.setColorForSwipeBack(this,getResources().getColor(R.color.colorClickedBack), 0);

        et_tel= findViewById(R.id.et_tel);
        clear_tel= findViewById(R.id.rl_clear_phone);

        et_pasw= findViewById(R.id.et_pasw);
        clear_pasw= findViewById(R.id.rl_clear_pasw);
        closed_eye1=  findViewById(R.id.closed_eye1);
        opened_eye1=  findViewById(R.id.opened_eye1);

        et_rpasw=  findViewById(R.id.et_rpasw);
        clear_rpasw= findViewById(R.id.rl_clear_rpasw);
        closed_eye2= findViewById(R.id.closed_eye2);
        opened_eye2= findViewById(R.id.opened_eye2);

        next= findViewById(R.id.btn_next);
    }

    @Override
    public void setListener() {
        super.setListener();
        clear_tel.setOnClickListener(this);

        clear_pasw.setOnClickListener(this);
        closed_eye1.setOnClickListener(this);
        opened_eye1.setOnClickListener(this);

        clear_rpasw.setOnClickListener(this);
        closed_eye2.setOnClickListener(this);
        opened_eye2.setOnClickListener(this);

        next.setOnClickListener(this);

        //手机号EditText监听
        et_tel.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //若输入非空，显示清除按钮
                if(!(s.length()==0)){
                    clear_tel.setVisibility(View.VISIBLE);
                }else{
                    clear_tel.setVisibility(View.INVISIBLE);
                }
            }
        });

        //密码EditText监听
        et_pasw.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                //若输入非空，显示清除按钮
                if(!(s.length()==0)){
                    clear_pasw.setVisibility(View.VISIBLE);
                }else{
                    clear_pasw.setVisibility(View.INVISIBLE);
                }
            }
        });

        //确认密码EditText监听
        et_rpasw.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                //若输入非空，显示清除按钮
                if(!(s.length()==0)){
                    clear_rpasw.setVisibility(View.VISIBLE);
                }else{
                    clear_rpasw.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            //清空手机号栏
            case R.id.rl_clear_phone:
                et_tel.setText("");
                break;

            //清空密码栏
            case R.id.rl_clear_pasw:
                et_pasw.setText("");
                break;

            //密码栏睁眼睛（显示密码）
            case R.id.closed_eye1:
                closed_eye1.setVisibility(View.INVISIBLE);
                opened_eye1.setVisibility(View.VISIBLE);
                //设置密码可见
                et_pasw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                break;

            //密码栏闭眼睛（隐藏密码）
            case R.id.opened_eye1:
                opened_eye1.setVisibility(View.INVISIBLE);
                closed_eye1.setVisibility(View.VISIBLE);
                //设置密码不可见
                et_pasw.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;

            //清空确认密码栏
            case R.id.rl_clear_rpasw:
                et_rpasw.setText("");
                break;

            //确认密码栏睁眼睛（显示密码）
            case R.id.closed_eye2:
                closed_eye2.setVisibility(View.INVISIBLE);
                opened_eye2.setVisibility(View.VISIBLE);
                //设置密码可见
                et_rpasw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                break;

            //确认密码栏闭眼睛（隐藏密码）
            case R.id.opened_eye2:
                opened_eye2.setVisibility(View.INVISIBLE);
                closed_eye2.setVisibility(View.VISIBLE);
                //设置密码不可见
                et_rpasw.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;

            //"下一步"按钮事件监听
            case R.id.btn_next:
                phone =et_tel.getText().toString();
                password=et_pasw.getText().toString();
                rpassword=et_rpasw.getText().toString();

                if(phone.length()==0 ) {
                    Toast.makeText(RegisterActivity.this, "请输入手机号！", Toast.LENGTH_SHORT).show();
                }
                else if(!CommonUtil.isPhone(phone)){
                    Toast.makeText(RegisterActivity.this, "请输入有效手机号！", Toast.LENGTH_SHORT).show();

                }else if( password.length()==0){
                    Toast.makeText(RegisterActivity.this, "请输入密码！", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<6 ){
                    Toast.makeText(RegisterActivity.this, "请输入6-16位有效密码！", Toast.LENGTH_SHORT).show();
                }
                else if(password.matches("[0-9]+")){
                    Toast.makeText(RegisterActivity.this, "密码不能为纯数字！", Toast.LENGTH_SHORT).show();
                }
                else if(rpassword.length()==0) {
                    Toast.makeText(RegisterActivity.this, "请输入确认密码！", Toast.LENGTH_SHORT).show();
                }
                else if(rpassword.length()<6 ){
                    Toast.makeText(RegisterActivity.this, "请输入6-16位有效密码！", Toast.LENGTH_SHORT).show();
                }else if (password.equals(rpassword)){
                    register(phone);
                }
                else{
                    Toast.makeText(RegisterActivity.this, "两次输入的密码不一致，请重新输入!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /*注册*/
    private void register(final String phone){
        Users users=new Users();
        users.setPhone(phone);

        Call<Reception> registerCall= Retrofit2.getService().selectUserByPhone(phone);
        registerCall.enqueue(new Callback<Reception>() {
            @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
            @Override
            public void onResponse(@NonNull Call<Reception> call, @NonNull retrofit2.Response<Reception> response) {
                if(Objects.requireNonNull(response.body()).getCode()== ResponseCode.REGISTERED){//手机号已注册
                    showRegisteredDialog(Objects.requireNonNull(response.body()).getMessage());
                }else{
                    //跳转到核查电话号码页面
                    Intent intent=new Intent(RegisterActivity.this,CheckPhoneActivity.class);
                    intent.putExtra("phone",phone);
                    intent.putExtra("password",password);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Reception> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
                System.out.println("注册失败！");
            }
        });
    }

    /*手机号已注册对话框*/
    private void showRegisteredDialog(String message) {
        new SweetAlertDialog(RegisterActivity.this)
                .setContentText(message)
                .setConfirmText("去登陆")
                .setCancelText("返回")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                        intent.putExtra("tel", phone);
                        intent.putExtra("flag",1);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        sweetAlertDialog.dismiss();
                        finish();
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


