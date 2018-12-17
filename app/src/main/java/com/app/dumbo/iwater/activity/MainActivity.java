package com.app.dumbo.iwater.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.fragment.PageFourFragment;
import com.app.dumbo.iwater.fragment.PageOneFragment;
import com.app.dumbo.iwater.fragment.PageThreeFragment;
import com.app.dumbo.iwater.fragment.PageTwoFragment;
import com.app.dumbo.iwater.activity.pageOne.AddMomentActivity;
import com.app.dumbo.iwater.activity.pageOne.ScanActivity;
import com.app.dumbo.iwater.constant.RequestCode;
import com.app.dumbo.iwater.constant.ResponseCode;
import com.app.dumbo.iwater.retrofit2.Retrofit2;
import com.app.dumbo.iwater.retrofit2.entity.reception.JwtReception;
import com.app.dumbo.iwater.util.CommonUtil;
import com.app.dumbo.iwater.util.PermissionUtil;
import com.baidu.mapapi.SDKInitializer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.jaeger.library.StatusBarUtil;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static Boolean isExit=false;

    //标题栏控件
    private TextView tvTitle;//标题栏标题
    private RelativeLayout scan,search,add,contact, camera;//消息通知按钮
    private SweetAlertDialog requestCameraPermDialog;

    //内容栏控件
    private ViewPager viewPager;
    List<Fragment> fragments;

    private RelativeLayout netState;//网络状态栏
    private NetworkChangeReceiver networkChangeReceiver;//网络状态接收广播

    //导航栏控件
    private LinearLayout rlPageOne,rlPageTwo,rlPageThree,rlPageFour;//导航栏4按钮
    private ImageView ivPageOne, ivPageTwo, ivPageThree, ivPageFour;//导航栏4按钮图标
    private TextView tvPageOne, tvPageTwo, tvPageThree, tvPageFour;//导航栏4按钮文字

    @SuppressLint("CheckResult")
    protected void onCreate(Bundle savedInstanceState) {
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        //控件初始化
        initView();

        //权限请求
        RxPermissions rxPermissions=new RxPermissions(this);
        rxPermissions.requestEach(Manifest.permission.ACCESS_FINE_LOCATION,
                                  Manifest.permission.CAMERA,
                                  Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        if (permission.granted) {
                        } else {
                        }
                    }
                });

        //设置监听
        setListener();

        //设置ViewPager
        setViewPager();

        //检测网络连接状态
        checkNetConnectState();

        //保持登录
        keepLoginState();

        //接收来自登录页面跳转值并跳到第4页
        Intent it=getIntent();//Activity间传值
        int page=it.getIntExtra("page",0);
        viewPager.setCurrentItem(page);
    }

    private void initView() {
        //设置状态栏颜色
        StatusBarUtil.setColorForSwipeBack(this,getResources().getColor(R.color.colorClickedBack), 0);

        //标题栏
        tvTitle =  findViewById(R.id.headText);
        scan = findViewById(R.id.rl_scan);
        search=findViewById(R.id.rl_search);
        camera=findViewById(R.id.rl_camera);
        add=findViewById(R.id.rl_add);
        contact=findViewById(R.id.rl_contact);

        //内容栏
        netState = findViewById(R.id.rl_net_state);
        viewPager = findViewById(R.id.view_pager);

        //导航栏
        rlPageOne=  findViewById(R.id.rl_page_one);
        rlPageTwo = findViewById(R.id.rl_page_two);
        rlPageThree =  findViewById(R.id.rl_page_three);
        rlPageFour =  findViewById(R.id.rl_page_four);

        ivPageOne =findViewById(R.id.iv_page_one);
        ivPageTwo =findViewById(R.id.iv_page_two);
        ivPageThree =findViewById(R.id.iv_page_three);
        ivPageFour =findViewById(R.id.iv_page_four);

        tvPageOne = findViewById(R.id.tv_page_one);
        tvPageTwo =  findViewById(R.id.tv_page_two);
        tvPageThree = findViewById(R.id.tv_page_three);
        tvPageFour = findViewById(R.id.tv_page_four);
    }

   public void setListener(){
       scan.setOnClickListener(this);
       camera.setOnClickListener(this);

       rlPageOne.setOnClickListener(this);
       rlPageTwo.setOnClickListener(this);
       rlPageThree.setOnClickListener(this);
       rlPageFour.setOnClickListener(this);
   }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //“扫描”按钮监听事件
            case R.id.rl_scan:
                requestCameraPermToScan();//请求权限后打开扫描
                break;

            //“相机”按钮监听事件
            case R.id.rl_camera:
                CommonUtil.skipActivityByFade(this,AddMomentActivity.class);
                break;

            //“首页”按钮监听事件
            case R.id.rl_page_one:
                viewPager.setCurrentItem(0);//跳转到第1页
                setPageOneWidget();//改变按钮状态
                break;

            //“动态”按钮监听事件
            case R.id.rl_page_two:
                viewPager.setCurrentItem(1);//跳转到第2页
                setPageTwoWidget();//改变按钮状态
                break;

            //“消息”按钮监听事件
            case R.id.rl_page_three:
                viewPager.setCurrentItem(2);//跳转到第3页
                setPageThreeWidget();//改变按钮状态
                break;

            //“我的"按钮监听事件
            case R.id.rl_page_four:
                viewPager.setCurrentItem(3);//跳转到第4页
                setPageFourWidget();//改变按钮状态
                break;
        }
    }

    /** 请求权限后打开扫描*/
    @SuppressLint("CheckResult")
    private void requestCameraPermToScan() {
        RxPermissions rxPermissions=new RxPermissions(MainActivity.this);
        rxPermissions.requestEach(Manifest.permission.CAMERA)//相机权限
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            scan();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限,未勾选不在询问
                            Toast.makeText(MainActivity.this,"该功能需要开启位置权限，请您开启！",Toast.LENGTH_SHORT).show();
                        } else {
                            // 用户拒绝了该权限,且勾选不在询问
                            showOpenPermissionDialog();//显示请求权限对话框
                        }
                    }

                    private void showOpenPermissionDialog() {
                        requestCameraPermDialog=new SweetAlertDialog(MainActivity.this);
                        requestCameraPermDialog .setTitleText("权限申请")
                                .setContentText("该功能需要在“设置-应用-掌上治水-权限”中开启相机权限，请您开启！")
                                .setConfirmText("去打开")
                                .setCancelText("取消")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        PermissionUtil permPageUtil = new PermissionUtil(MainActivity.this);
                                        permPageUtil.jumpPermissionPage();
                                    }
                                })
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                }).show();
                    }

                    /*打开二维码扫描*/
                    private void scan() {
                        IntentIntegrator integrator=new IntentIntegrator(MainActivity.this);
                        integrator.setCaptureActivity(ScanActivity.class);//自定义CaptureActivity
                        // 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                        integrator.setPrompt("");//设置提示语
                        integrator.setTimeout(60000);//设置超时,超过这个时间之后，扫描的 Activity 将会被 finish 。
                        integrator.setBeepEnabled(true);// 是否开启声音,扫完码之后会"哔"的一声
                        integrator.initiateScan();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RequestCode.REQUEST_PERMISSION_CODE:
                requestCameraPermDialog.dismiss();
        }
    }

    /**设置ViewPager适配Fragment*/
    protected void setViewPager() {
        fragments=new ArrayList<>();
        PageOneFragment pageOne=new PageOneFragment();
        PageTwoFragment pageTwo=new PageTwoFragment();
        PageThreeFragment pageThree=new PageThreeFragment();
        PageFourFragment pageFour=new PageFourFragment();

        fragments.add(pageOne);
        fragments.add(pageTwo);
        fragments.add(pageThree);
        fragments.add(pageFour);

        FragmentManager fm=getSupportFragmentManager();
        MyFragmentPagerAdapter adapter=new MyFragmentPagerAdapter(fm);

        //设置适配器
        viewPager.setAdapter(adapter);
        //监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //跳转到指定页面时更改相应页面控件外观
                switch(position){
                    case 0:
                        setPageOneWidget();
                        new PageOneFragment();
                        break;
                    case 1:
                        new PageTwoFragment();
                        setPageTwoWidget();
                        break;
                    case 2:
                        new PageThreeFragment();
                        setPageThreeWidget();
                        break;
                    case 3:
                        new PageFourFragment();
                        setPageFourWidget();
                        break;
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**核查网络连接状态，若无网络，则显示提示栏*/
    protected void checkNetConnectState() {
        //注册receiver
        IntentFilter intentFilter = new IntentFilter();
        //广播接收器想要监听什么广播，就在这里添加相应的action
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver=new NetworkChangeReceiver();
        //调用resigerReceiver()方法进行注册
        registerReceiver(networkChangeReceiver, intentFilter);

        //点击跳转到网络设置界面
        netState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });
    }

    /**保持登录状态*/
    private void keepLoginState() {
        //从SharedPreferences中取出jwt
        SharedPreferences sp=getSharedPreferences("user_info", Context.MODE_PRIVATE);
        final String accessJwt=sp.getString("access_jwt",null);
        //发送accessToken到服务器验证
        if(accessJwt!=null){
            Call<JwtReception> keepLoginCall= Retrofit2.getService().postAccessJwt(accessJwt);
            keepLoginCall.enqueue(new Callback<JwtReception>() {
                @SuppressLint("ApplySharedPref")
                @Override
                public void onResponse(@NonNull Call<JwtReception> call, @NonNull Response<JwtReception> response) {
                    int code= Objects.requireNonNull(response.body()).getCode();

                    //新建SharedPreferences对象
                    SharedPreferences sp=getSharedPreferences("user_info", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();

                    switch (code) {
                        case ResponseCode.OK:
                            System.out.println("保持登录成功");
                            //添加数据
                            editor.putString("access_jwt", accessJwt);
                            editor.apply();
                            break;

                        case ResponseCode.TOKEN_IS_INVALID: //令牌无效
                            System.out.println("令牌无效");
                            editor.putInt("user_id",0);
                            editor.apply();
                            break;

                        case ResponseCode.ACCESS_TOKEN_IS_EXPIRED: //令牌过期
                            System.out.println("令牌过期");
                            final String refreshJwt=sp.getString("refresh_jwt",null);

                            if(refreshJwt!=null){
                                Call<JwtReception> jwtCall=Retrofit2.getService().postRefreshJwt(refreshJwt);
                                jwtCall.enqueue(new Callback<JwtReception>() {
                                    @Override
                                    public void onResponse(@NonNull Call<JwtReception> call, Response<JwtReception> response) {
                                        int code= Objects.requireNonNull(response.body()).getCode();
                                        String newAccessJwt= Objects.requireNonNull(response.body()).getData().getAccessJwt();
                                        String newRefreshJwt= Objects.requireNonNull(response.body()).getData().getRefreshJwt();

                                        if(code==6666) {//登录成功
                                            //注册成功后将服务器返回的jwt存入SharedPreferences
                                            //新建SharedPreferences对象
                                            SharedPreferences sp=getSharedPreferences("user_info", Context.MODE_PRIVATE);
                                            //添加数据
                                            SharedPreferences.Editor editor=sp.edit();
                                            editor.putString("access_jwt", newAccessJwt);
                                            editor.putString("refresh_jwt", newRefreshJwt);
                                            editor.apply();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<JwtReception> call, Throwable t) {

                                    }
                                });
                            }
                            break;
                        case ResponseCode.REFRESH_TOKEN_IS_EXPIRED: //重新登录
                            break;
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JwtReception> call, @NonNull Throwable t) {
                    System.out.println(t.getMessage());
                }
            });
        }
    }

    /**设置标题栏控件状态——不同页面显示不同控件；
     设置导航栏控件状态——底部导航栏4个按钮点击后颜色改变*/
    public void setPageOneWidget(){
        //标题栏控件可见与否
        tvTitle.setText(R.string.tv_first_page);
        scan.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
        add.setVisibility(View.INVISIBLE);
        contact.setVisibility(View.INVISIBLE);
        camera.setVisibility(View.INVISIBLE);

        //设置图标1颜色改变，其余不变
        ivPageOne.setBackgroundResource(R.drawable.one_color);
        ivPageTwo.setBackgroundResource(R.drawable.two);
        ivPageThree.setBackgroundResource(R.drawable.three);
        ivPageFour.setBackgroundResource(R.drawable.four);

        //设置按钮下标签1颜色改变，其余不变
        tvPageOne.setTextColor(ContextCompat.getColor(this,R.color.colorGreen));
        tvPageTwo.setTextColor(ContextCompat.getColor(this,android.R.color.darker_gray));
        tvPageThree.setTextColor(ContextCompat.getColor(this,android.R.color.darker_gray));
        tvPageFour.setTextColor(ContextCompat.getColor(this,android.R.color.darker_gray));
    }

    public void setPageTwoWidget(){
        //标题栏控件可见与否
        tvTitle.setText(R.string.tv_dynamic);
        scan.setVisibility(View.INVISIBLE);
        search.setVisibility(View.INVISIBLE);
        add.setVisibility(View.INVISIBLE);
        contact.setVisibility(View.INVISIBLE);
        camera.setVisibility(View.VISIBLE);

        //设置图标2颜色改变，其余不变
        ivPageOne.setBackgroundResource(R.drawable.one);
        ivPageTwo.setBackgroundResource(R.drawable.two_color);
        ivPageThree.setBackgroundResource(R.drawable.three);
        ivPageFour.setBackgroundResource(R.drawable.four);

        //设置按钮下标签2颜色改变，其余不变
        tvPageOne.setTextColor(ContextCompat.getColor(this,android.R.color.darker_gray));
        tvPageTwo.setTextColor(ContextCompat.getColor(this,R.color.colorGreen));
        tvPageThree.setTextColor(ContextCompat.getColor(this,android.R.color.darker_gray));
        tvPageFour.setTextColor(ContextCompat.getColor(this,android.R.color.darker_gray));
    }

    public void setPageThreeWidget(){
        //标题栏控件可见与否
        tvTitle.setText(R.string.tv_message);
        scan.setVisibility(View.INVISIBLE);
        search.setVisibility(View.INVISIBLE);
        add.setVisibility(View.VISIBLE);
        contact.setVisibility(View.VISIBLE);
        camera.setVisibility(View.INVISIBLE);

        //设置图标3颜色改变，其余不变
        ivPageOne.setBackgroundResource(R.drawable.one);
        ivPageTwo.setBackgroundResource(R.drawable.two);
        ivPageThree.setBackgroundResource(R.drawable.three_color);
        ivPageFour.setBackgroundResource(R.drawable.four);

        //设置按钮下标签3颜色改变，其余不变
        tvPageOne.setTextColor(ContextCompat.getColor(this,android.R.color.darker_gray));
        tvPageTwo.setTextColor(ContextCompat.getColor(this,android.R.color.darker_gray));
        tvPageThree.setTextColor(ContextCompat.getColor(this,R.color.colorGreen));
        tvPageFour.setTextColor(ContextCompat.getColor(this,android.R.color.darker_gray));

    }

    public void setPageFourWidget(){
        //标题栏控件可见与否
        tvTitle.setText(R.string.tv_my);
        scan.setVisibility(View.INVISIBLE);
        search.setVisibility(View.INVISIBLE);
        add.setVisibility(View.INVISIBLE);
        contact.setVisibility(View.INVISIBLE);
        camera.setVisibility(View.INVISIBLE);

        //设置图标4颜色改变，其余不变
        ivPageOne.setBackgroundResource(R.drawable.one);
        ivPageTwo.setBackgroundResource(R.drawable.two);
        ivPageThree.setBackgroundResource(R.drawable.three);
        ivPageFour.setBackgroundResource(R.drawable.four_color);

        //设置按钮下标签4颜色改变，其余不变
        tvPageOne.setTextColor(ContextCompat.getColor(this,android.R.color.darker_gray));
        tvPageTwo.setTextColor(ContextCompat.getColor(this,android.R.color.darker_gray));
        tvPageThree.setTextColor(ContextCompat.getColor(this,android.R.color.darker_gray));
        tvPageFour.setTextColor(ContextCompat.getColor(this,R.color.colorGreen));
    }

    /**监听网络状态改变类-----通过广播receiver实时监听网络连接状态，若无网
     络连接则显示网络异常提示栏，点击后跳转到网络设置页面，有网则不显示*/
    class NetworkChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            ConnectivityManager connectionManager=(ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);  //得到系统服务类
            NetworkInfo networkInfo= null;
            if (connectionManager != null) {
                networkInfo = connectionManager.getActiveNetworkInfo();
            }
            if(networkInfo!=null && networkInfo.isAvailable()){
                netState.setVisibility(View.GONE);
            }else{
                netState.setVisibility(View.VISIBLE);
            }
        }
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter{

        MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    /**实现只在冷启动时显示启动页，即点击返回键与点击HOME键退出效果一致*/
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消receiver注册
        unregisterReceiver(networkChangeReceiver);
    }
}

