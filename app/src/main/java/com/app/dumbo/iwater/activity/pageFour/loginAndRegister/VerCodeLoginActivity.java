package com.app.dumbo.iwater.activity.pageFour.loginAndRegister;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.app.dumbo.iwater.activity.superClass.WithBackActivity;
import com.app.dumbo.iwater.util.CommonUtil;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by dumbo on 2017/11/17.
 */

public class VerCodeLoginActivity extends WithBackActivity {
    private RelativeLayout clear_tel;//清除手机号栏内容按钮
    private EditText et_tel,et_pasw;//手机号栏和密码栏
    private Button login;//登录按钮
    private RelativeLayout get_verCode;//获取验证码控件
    private RelativeLayout back;//返回控件
    private TextView get_verCode_text;//"获取验证码"文本
    private int second=60;//重新获取验证码事件（60s）
    private JSONObject loginInfo;//注册信息
    private String telNumber;//获取的电话号码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_vercode_login);
        super.onCreate(savedInstanceState);

        clear_tel = (RelativeLayout) findViewById(R.id.rl_clear_tel);
        et_tel = (EditText) findViewById(R.id.et_tel);
        et_pasw = (EditText) findViewById(R.id.et_pasw);
        get_verCode = (RelativeLayout) findViewById(R.id.rl_get_verCode);
        get_verCode_text= (TextView) findViewById(R.id.tv_get_verCode_text);
        login = (Button) findViewById(R.id.btn_login);
        back= (RelativeLayout) findViewById(R.id.back);

        //返回按钮监听事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent=new Intent(VerCodeLoginActivity.this,LoginRegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

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
                //若输入非空，显示清除按钮,否则不显示
                if (!(s.length() == 0)) {
                    clear_tel.setVisibility(View.VISIBLE);
                } else {
                    clear_tel.setVisibility(View.INVISIBLE);
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

        get_verCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telNumber=et_tel.getText().toString();//获取的电话号码
                if (telNumber.length() == 0) {
                    Toast.makeText(VerCodeLoginActivity.this, "请输入手机号！", Toast.LENGTH_SHORT).show();
                }//判断手机号为11位，且首位数字为1，每位均为数字
                else if (CommonUtil.isPhone(telNumber)) {
                    Toast.makeText(VerCodeLoginActivity.this, "请输入有效手机号！", Toast.LENGTH_SHORT).show();
                } else {
                    //请求获取短信验证码，在监听中返回
                    SMSSDK.getVerificationCode("86",telNumber);
                    //设置“获取验证码”不可点击&半透明显示
                    get_verCode.setClickable(false);
                    get_verCode_text.setTextColor(getResources().getColor(R.color.colorTranslucentGreen));
                    get_verCode_text.setText("重新发送("+second+"s)");
                    get_verCode_text.setTextSize(14);
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
            }
        });

        //登录按钮事件监听
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telNumber=et_tel.getText().toString();//获取的电话号码
                String verCode = et_pasw.getText().toString();//获取的验证码
                if (telNumber.length() == 0) {
                    Toast.makeText(VerCodeLoginActivity.this, "请输入手机号！", Toast.LENGTH_SHORT).show();
                }//判断手机号为11位，且首位数字为1，每位均为数字
                else if (CommonUtil.isPhone(telNumber)) {
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
                    SMSSDK.submitVerificationCode("86", telNumber, verCode);
                }
            }
        });

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
            et_tel.setText(tel);
            et_pasw.requestFocus();
        }
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
                    URL url = new URL("http://localhost:8080/com.iwater.server/LoginRegisterServlet");
                    conn= (HttpURLConnection) url.openConnection();

                    //2.设置HttpURLConnection相关属性
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(80000);//设置建立连接超时时间
                    conn.setReadTimeout(80000);//设置网络报文收发超时时间

                    //3.设置post参数
                    PrintWriter pw=new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
                    //将数据包装为json格式
                    loginInfo =new JSONObject();
                    try {
                        loginInfo.put("label","login_by_verCode");
                        loginInfo.put("telNumber",telNumber);
                        loginInfo.put("password","");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pw.println(loginInfo.toString());
                    pw.flush();

                    //4.打开输入流获取返回报文(*此处开始网络请求*)
                    String response;
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while((response=br.readLine())!=null){
                        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+response);
                        Message msg=new Message();
                        msg.obj=response;
                        handler.sendMessage(msg);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==-2){
                get_verCode_text.setText("重新发送("+second+"s)");
            }else if(msg.what==-3){
                get_verCode_text.setText("重新发送");
                //计时结束，恢复原状
                get_verCode.setClickable(true);
                get_verCode_text.setTextSize(16);
                get_verCode_text.setTextColor(getResources().getColor(R.color.colorGreen));
            }else{
                int event=msg.arg1;
                int result=msg.arg2;
                Object data=msg.obj;

                if (result == SMSSDK.RESULT_COMPLETE) { //回调完成
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//获取验证码成功
                        Log.i("提示：", "获取验证码成功 ");
                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//验证成功
                        Log.i("提示：", "验证成功 ");
                        Intent intent = new Intent(VerCodeLoginActivity.this, MainActivity.class);
                        intent.putExtra("page", 4);
                        //在此activity启动之前，任何与此activity相关联的task都会被清除
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                        //将电话号码发送到服务器
                        getHttpURLConnection();
                    }
                } else{
                    showVerCodeErroredDialog();
                }
            }
        }
    };

}
