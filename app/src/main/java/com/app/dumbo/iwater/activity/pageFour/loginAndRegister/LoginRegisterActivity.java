package com.app.dumbo.iwater.activity.pageFour.loginAndRegister;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.AnimFadeActivity;
import com.app.dumbo.iwater.util.CommonUtil;
import com.mob.MobSDK;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by dumbo on 2017/11/14.
 */

public class LoginRegisterActivity extends AnimFadeActivity {
    private Button btnLogin, btnRegister;//登录或注册
    private LinearLayout weixinLogin, QQLogin, weiboLogin;//其他方式登录（微信/QQ/微博登录）
    private TextView privacyPolicy, userProtocol;//用户协议和隐私政策

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_login_register);
        super.onCreate(savedInstanceState);

        //初始化MobSDK
        MobSDK.init(this);

    }

    @Override
    public void initView() {
        super.initView();
        btnLogin =  findViewById(R.id.btn_next);
        btnRegister = findViewById(R.id.btn_register);
        weixinLogin =  findViewById(R.id.ll_weixin_login);
        QQLogin =  findViewById(R.id.ll_QQ_login);
        weiboLogin =  findViewById(R.id.ll_weibo_login);
        privacyPolicy = findViewById(R.id.tv_privacy_policy);
        userProtocol =  findViewById(R.id.tv_user_protocol);
    }

    @Override
    public void setListener() {
        super.setListener();
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        weixinLogin.setOnClickListener(this);
        QQLogin.setOnClickListener(this);
        weiboLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            //“登录”按钮监听
            case R.id.btn_next:
                CommonUtil.skipActivityByFade(this,LoginActivity.class);
                break;

            //“注册”按钮监听
            case R.id.btn_register:
                CommonUtil.skipActivityByFade(this,RegisterActivity.class);
                break;

            //“微信登录”按钮监听
            case R.id.ll_weixin_login:
                loginByWeixin();
                break;

            //“QQ登录”按钮监听
            case R.id.ll_QQ_login:
                loginByQQ();
                break;

            //“微博登录”登录按钮监听
            case R.id.ll_weibo_login:
                loginByWeiBo();
                break;

        }
    }

    //QQ授权登录
    public void loginByQQ(){
        Platform qq= ShareSDK.getPlatform(QQ.NAME);
        //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        qq.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.i("授权完成","1111111111111111111111111111111111");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.i("授权错误","222222222222222222222222222222222");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.i("授权取消","333333333333333333333333333333333");
            }
        });
        //qq.showUser(null);//授权并获取用户信息
        qq.authorize(); /*调用authorize方法，会弹出一个基于ShareSDKUIShell的授权页面，填写账号和密码以后，
        会执行授权操作。这个方法的操作回调paListener并不实际带回什么数据，只是通过回调告知外部成功或者失败。
        但是每一个平台都具备一个PlatformDb的成员，这里面存储了此平台的授权信息。
        可以通过方法getToken、getUserId等方法，获取授权用户在此平台上的授权信息。并由此建立“账户系统”。*/

        String str=qq.getDb().getUserName();
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%"+str);
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%"+qq.getDb().exportData());
    }

    //微信授权登录
    public void loginByWeixin(){
        Platform wechat= ShareSDK.getPlatform(Wechat.NAME);
        //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        wechat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.i("授权完成","");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.i("授权错误","");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.i("授权取消","");
            }
        });
        wechat.authorize();//单独授权,OnComplete返回的hashmap是空的
    }

    //微博授权登录
    public void loginByWeiBo(){
        Platform weibo= ShareSDK.getPlatform(SinaWeibo.NAME);
        //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        weibo.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.i("授权完成","");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.i("授权错误","");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.i("授权取消","");
            }
        });
        weibo.authorize();//单独授权,OnComplete返回的hashmap是空的
    }
}
