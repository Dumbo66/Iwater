package com.app.dumbo.iwater.activity.pageFour.loginAndRegister;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.MainActivity;
import com.app.dumbo.iwater.activity.superClass.WithBackActivity;
import com.app.dumbo.iwater.util.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by dumbo on 2017/11/15.
 */

public class SetPaswActivity extends WithBackActivity {
    private RegisterHandler handler =new RegisterHandler();
    private EditText et_tel,et_pasw,et_rpasw;
    private RelativeLayout closed_eye1,opened_eye1,closed_eye2,opened_eye2,
            clear_tel,clear_pasw,clear_rpasw;
    private JSONObject registerInfo;//注册信息
    private String telNumber,password,rpassword;

    protected void onCreate( Bundle savedInstanceState) {
        setContentView(R.layout.activity_set_password);
        super.onCreate(savedInstanceState);

        Button next = findViewById(R.id.next);
        et_tel= findViewById(R.id.et_tel);
        clear_tel= findViewById(R.id.rl_clear_tel);
        et_pasw=  findViewById(R.id.et_pasw);
        clear_pasw= findViewById(R.id.rl_clear_pasw);
        closed_eye1=  findViewById(R.id.closed_eye1);
        opened_eye1=  findViewById(R.id.opened_eye1);
        et_rpasw= findViewById(R.id.et_rpasw);
        clear_rpasw= findViewById(R.id.clear_rpasw);
        closed_eye2= findViewById(R.id.closed_eye2);
        opened_eye2= findViewById(R.id.opened_eye2);

/*------------------------------------------手机号栏-----------------------------------------------*/
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
        et_tel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                password=et_pasw.getText().toString();
                rpassword=et_rpasw.getText().toString();
                if(hasFocus){
                    if(password.length()!=0){
                        if(password.length()<6 ){
                            Toast.makeText(SetPaswActivity.this,
                                    "请输入6-16位有效密码！", Toast.LENGTH_SHORT).show();
                        }
                    }else if(rpassword.length()!=0){
                        if(rpassword.length()<6 ){
                            Toast.makeText(SetPaswActivity.this,
                                    "请输入6-16位有效密码！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        //清空手机号栏
        clear_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_tel.setText("");
            }
        });

/*--------------------------------------------密码栏-----------------------------------------------*/
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

        //密码栏清空
        clear_pasw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_pasw.setText("");
            }
        });

        //密码栏睁眼睛
        closed_eye1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closed_eye1.setVisibility(View.INVISIBLE);
                opened_eye1.setVisibility(View.VISIBLE);
                //设置密码可见
                et_pasw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
        });

        //密码栏闭眼睛
        opened_eye1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opened_eye1.setVisibility(View.INVISIBLE);
                closed_eye1.setVisibility(View.VISIBLE);
                //设置密码不可见
                et_pasw.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

/*--------------------------------------------确认密码栏-------------------------------------------*/
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

        //确认密码栏清空
        clear_rpasw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_rpasw.setText("");
            }
        });

        //确认密码栏睁眼睛
        closed_eye2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closed_eye2.setVisibility(View.INVISIBLE);
                opened_eye2.setVisibility(View.VISIBLE);
                //设置密码可见
                et_rpasw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
        });

        //确认密码栏闭眼睛
        opened_eye2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opened_eye2.setVisibility(View.INVISIBLE);
                closed_eye2.setVisibility(View.VISIBLE);
                //设置密码不可见
                et_rpasw.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

/*-------------------------------------------登录栏------------------------------------------*/
        //"登录"按钮事件监听
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telNumber=et_tel.getText().toString();
                password=et_pasw.getText().toString();
                rpassword=et_rpasw.getText().toString();

                if(telNumber.length()==0 )
                {
                    Toast.makeText(SetPaswActivity.this, "请输入手机号！", Toast.LENGTH_SHORT).show();
                }
                else if(CommonUtil.isPhone(telNumber)){
                    Toast.makeText(SetPaswActivity.this, "请输入有效手机号！", Toast.LENGTH_SHORT).show();
                }
                else if( password.length()==0)
                {
                    Toast.makeText(SetPaswActivity.this, "请输入密码！", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<6 ){
                    Toast.makeText(SetPaswActivity.this, "请输入6-16位有效密码！", Toast.LENGTH_SHORT).show();
                }
                else if(rpassword.length()==0) {
                    Toast.makeText(SetPaswActivity.this, "请输入确认密码！", Toast.LENGTH_SHORT).show();
                }
                else if(rpassword.length()<6 ){
                    Toast.makeText(SetPaswActivity.this, "请输入6-16位有效密码！", Toast.LENGTH_SHORT).show();
                }
                else if(password.matches("[0-9]+")){
                    Toast.makeText(SetPaswActivity.this, "密码不能为纯数字！", Toast.LENGTH_SHORT).show();
                }
                else if (password.equals(rpassword)){
                    getHttpURLConnection();
                } else {
                    showErroredDialog();
                }
            }
        });

        //接收来自登录页面跳转值
        Intent it=getIntent();
        String tel=it.getStringExtra("tel");
        int flag=it.getIntExtra("flag",0);
        if(flag==1){
            et_tel.setText(tel);
            et_pasw.requestFocus();
        }
    }

    /*错误提示对话框*/
    private void showErroredDialog() {
        new SweetAlertDialog(SetPaswActivity.this)
                .setContentText("两次输入的密码不一致，请重新输入!")
                .setConfirmText("重新输入")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }

    /*注册失败对话框*/
    private void showRegisteredDialog(String message) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(SetPaswActivity.this);
        dialog.setMessage(message);//已注册
        dialog.setPositiveButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("去登陆", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(SetPaswActivity.this,LoginActivity.class);
                intent.putExtra("tel",telNumber);
                intent.putExtra("flag",1);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    /*HttpURLConnection与服务器交互*/
    private void getHttpURLConnection(){
        //耗时操作，开启新线程进行网络通信
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn=null;
                try {
                    //1.l使用URL打开一个HttpURLConnection连接
                    URL url = new URL("http://192.168.1.67:8080/com.icampus.httpServer/LoginRegisterServlet");
                    conn= (HttpURLConnection) url.openConnection();

                    //2.设置HttpURLConnection相关属性
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(80000);//设置建立连接超时时间
                    conn.setReadTimeout(80000);//设置网络报文收发超时时间

                    //3.设置post参数
                    PrintWriter pw=new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
                    //将数据包装为json格式
                    registerInfo=new JSONObject();
                    try {
                        registerInfo.put("label","set_password");
                        registerInfo.put("telNumber",telNumber);
                        registerInfo.put("password",password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pw.println(registerInfo.toString());
                    pw.flush();

                    //4.打开输入流获取返回报文(*此处开始网络请求*)
                    String response;
                    BufferedReader  br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while((response=br.readLine())!=null){
                        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+response);
                        Message msg=new Message();
                        msg.obj=response;
                        handler.sendMessage(msg);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*创建Handler将子线程中数据发给主线程*/
    private class RegisterHandler extends android.os.Handler{
        @Override
        // 子类必须重写此方法，接收数据
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if(!msg.equals(null)){
                    JSONObject json=new JSONObject(msg.obj.toString());
                    String code=json.getString("code");
                    String message=json.getString("message");
                    if(code.equals("-3")){
                        Intent intent = new Intent(SetPaswActivity.this, MainActivity.class);
                        intent.putExtra("page", 4);
                        //在此activity启动之前，任何与此activity相关联的task都会被清除
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
        return super.onKeyDown(keyCode, event);
    }
}


