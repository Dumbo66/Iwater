package com.app.dumbo.iwater.activity.pageOne;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.BaseMapActivity;
import com.app.dumbo.iwater.constant.ResponseCode;
import com.app.dumbo.iwater.retrofit2.Retrofit2;
import com.app.dumbo.iwater.retrofit2.entity.MobileData;
import com.app.dumbo.iwater.retrofit2.entity.MobileDataReception;
import com.app.dumbo.iwater.retrofit2.entity.Reception;
import com.app.dumbo.iwater.util.CommonUtil;
import com.app.dumbo.iwater.util.DataDecodeUtil;
import com.app.dumbo.iwater.util.MapUtil;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dumbo on 2018/6/21
 **/

@SuppressLint("Registered")
public class MobileMonitorActivity extends BaseMapActivity {
    private MapView myMapView =null;//Baidu地图View
    private BaiduMap myBaiduMap=null;

    private BtHandler btHandler=new BtHandler();

    private RelativeLayout rlEmpty;
    private ScrollView sv;
    private RelativeLayout rlDate;//日期选择
    private TextView tvDate;
    private String date;
    private RelativeLayout postData;
    private TextView tvPostData;
    private MobileData mobileData;

    private TextView tvTem,tvPh,tvTur,tvDio,tvCon,tvGad;

    private TextView tvLat,tvLng;//滑窗显示经纬度
    private TextView tvAddress;
    private double myCurrentLat;//当前经度
    private double myCurrentLng;//当前纬度

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;//蓝牙设备
    private RelativeLayout rlBluetooth;

    //数据通信相关
    private BluetoothSocket btSocket;
    //这条是蓝牙串口通信用的UUID，不要更改
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Thread  rxThread;//数据接收线程
    private InputStream inputStream;
    private OutputStream outputStream;

    /*地图标注相关*/
    private int dataCount;//监测站点数量
    private LatLng[] latLng;//监测站点经纬度
    private Marker[] markers;//监测站点标记
    private float[] dio;//溶氧
    private float[] tur;//浊度
    private float[] ph;//PH
    private float[] con;//电导率
    private float[] tem;//温度
    private char[] gad;//水质等级

    InfoWindow.OnInfoWindowClickListener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_mobile_monitor);
        super.onCreate(savedInstanceState);

        //控件初始化
        initView();

        //控件监听
        listenWidget();

        //设置定位监听器
        MyLocationListener myLocationListener =new MyLocationListener();
        setLocationListener(myLocationListener);

        //获取蓝牙适配器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断设备是否支持蓝牙功能
        if(bluetoothAdapter==null){
            Toast.makeText(this,"当前设备不支持蓝牙功能！",Toast.LENGTH_LONG).show();
        }
        //判断蓝牙是否打开
        if(!bluetoothAdapter.isEnabled()){
            //若没打开则打开蓝牙
            boolean bluetoothState=bluetoothAdapter.enable();
            if(!bluetoothState){
                finish();
            }
        }
        bluetoothAdapter.startDiscovery();

        //注册设备被发现时的广播
        final IntentFilter intentFilter1=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(myReceiver,intentFilter1);

        setRxThread();

        callData(date);//请求数据

    }

    /**控件初始化*/
    public void initView(){
        rlEmpty =findViewById(R.id.rl_empty);
        sv=findViewById(R.id.sv);
        rlBluetooth = findViewById(R.id.rl_bluetooth);
        rlDate=findViewById(R.id.rl_date);
        tvDate=findViewById(R.id.tv_date);

        tvLat=findViewById(R.id.tv_lat);
        tvLng=findViewById(R.id.tv_lng);
        tvAddress =findViewById(R.id.tv_address);

        tvTem=findViewById(R.id.tv_tem);
        tvPh=findViewById(R.id.tv_ph);
        tvTur=findViewById(R.id.tv_tur);
        tvDio=findViewById(R.id.tv_dio);
        tvCon=findViewById(R.id.tv_con);
        tvGad=findViewById(R.id.tv_gad);

        postData=findViewById(R.id.rl_post_data);
        tvPostData=findViewById(R.id.tv_post_data);

        //获取年月日
        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        int thisMonth = (calendar.get(Calendar.MONTH) + 1);
        int thisDay = calendar.get(Calendar.DAY_OF_MONTH);
        date=thisYear +"-"+ thisMonth +"-"+ thisDay;
        tvDate.setText("日期："+date);

        //地图初始化
        myMapView= findViewById(R.id.myMapView);
        myBaiduMap=myMapView.getMap();
    }

    /**控件监听*/
    private void listenWidget() {
        rlEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sv.setVisibility(View.GONE);
            }
        });

        rlDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        myBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                sv.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        rlBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });

        postData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPostData.setText("上传中……");

                Call<Reception> postDataCall= Retrofit2.getService().postMobileData(mobileData);
                postDataCall.enqueue(new retrofit2.Callback<Reception>() {
                    @Override
                    public void onResponse(Call<Reception> call, Response<Reception> response) {
                        int code=response.body().getCode();
                        if(code== ResponseCode.OK){
                            tvPostData.setText("上传数据");
                            Toast.makeText(MobileMonitorActivity.this,
                                    "上传移动监测数据成功！",Toast.LENGTH_LONG).show();
                            System.out.println("上传移动监测数据成功！");
                            myBaiduMap.clear();
                            callData(date);
                        }
                    }

                    @Override
                    public void onFailure(Call<Reception> call, Throwable t) {
                        tvPostData.setText("上传数据");
                        Toast.makeText(MobileMonitorActivity.this,
                                "上传移动监测数据失败！",Toast.LENGTH_LONG).show();
                        System.out.println("上传移动监测数据失败！");
                    }
                });
            }
        });
    }

    private void setRxThread() {
        rxThread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    btSocket=device.createRfcommSocketToServiceRecord(MY_UUID);
                    btSocket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("error:"+e.getMessage());
                    try {
                        btSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                if(btSocket.isConnected()){
                    //定义接收存储空间
                    try {
                        outputStream=btSocket.getOutputStream();
                        String tx= "FFFF";
                        outputStream.write(CommonUtil.getHexBytes(tx));
                        outputStream.flush();

                        while(true){
                            inputStream=btSocket.getInputStream();
                            byte[] buffer = new byte[1024];
                            while(inputStream.available()>0){
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                int count = inputStream.read(buffer);
                                String rxData=CommonUtil.bytesToHexStr(buffer,0,count);
                                MobileData mobileData= DataDecodeUtil.decodeMobileData(rxData);
                                Date date=new Date(System.currentTimeMillis());
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                Message msg=new Message();
                                Bundle b=new Bundle();
                                b.putString("time",sdf.format(date));
                                b.putFloat("tem",mobileData.getTemperature());
                                b.putFloat("ph",(mobileData.getPh()));
                                b.putFloat("dio",(float) 1.00);
                                b.putFloat("tur",(mobileData.getTurbidity()));
                                b.putFloat("con",(float) 1.00);
                                msg.setData(b);
                                btHandler.sendMessage(msg);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**请求数据*/
    private void callData(String date) {
        Call<MobileDataReception> mobileDataCall=Retrofit2.getService().getMobileData(date);
        mobileDataCall.enqueue(new Callback<MobileDataReception>() {
            @Override
            public void onResponse(Call<MobileDataReception> call, Response<MobileDataReception> response) {
                System.out.println("请求数据成功！");
                Toast.makeText(MobileMonitorActivity.this,"请求数据成功！",Toast.LENGTH_SHORT).show();
                myBaiduMap.clear();

                dataCount=response.body().getData().size();

                final int[] s=new int[dataCount];
                double[] lat=new double[dataCount];
                double[] lng=new double[dataCount];
                latLng = new LatLng[dataCount];
                markers = new Marker[dataCount];
                dio=new float[dataCount];
                tur=new float[dataCount];
                ph=new float[dataCount];
                con=new float[dataCount];
                tem=new float[dataCount];
                gad=new char[dataCount];

                for(short i = 0; i< dataCount; i++){
                    //获取经纬度
                    lat[i]=response.body().getData().get(i).getLatBd09ll();
                    lng[i]=response.body().getData().get(i).getLngBd09ll();
                    latLng[i]=new LatLng(lat[i],lng[i]);
                    gad[i]=response.body().getData().get(i).getGrade();
                    //地图标点
                    markers[i]= MapUtil.addMarkers(MobileMonitorActivity.this,
                            myBaiduMap,latLng[i],gad[i]);

                    //获取具体数据
                    dio[i]=response.body().getData().get(i).getDissolvedOxygen();
                    tur[i]=response.body().getData().get(i).getTurbidity();
                    ph[i]=response.body().getData().get(i).getPh();
                    con[i]=response.body().getData().get(i).getConductivity();
                    tem[i]=response.body().getData().get(i).getTemperature();
                }
                myBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        for(int i = 0; i< dataCount; i++){
                            if (marker==markers[i]){
                                showInfoWindow(latLng[i],dio[i],tur[i],ph[i],con[i],tem[i],gad[i]);
                            }
                        }
                        return true;
                    }
                });
            }

            @Override
            public void onFailure(Call<MobileDataReception> call, Throwable t) {
                Toast.makeText(MobileMonitorActivity.this,"请求数据失败！",Toast.LENGTH_SHORT).show();
                System.out.println("请求数据失败！");
                System.out.println(t.getMessage());
            }
        });
    }

    /**从xml创建要显示的InfoWindow*/
    @SuppressLint("SetTextI18n")
    private void showInfoWindow(LatLng latLng,float dio,
                                float tur, float ph, float con, float tem , char gad) {
        LayoutInflater inflater=LayoutInflater.from(getApplicationContext());
        View view=inflater.inflate(R.layout.window_data_info,null);

        TextView title= view.findViewById(R.id.title);
        TextView tv_dio= view.findViewById(R.id.tv_dio);
        TextView tv_tur= view.findViewById(R.id.tv_tur);
        TextView tv_ph= view.findViewById(R.id.tv_ph);
        TextView tv_con= view.findViewById(R.id.tv_con);
        TextView tv_tem= view.findViewById(R.id.tv_tem);
        TextView tv_gad= view.findViewById(R.id.tv_gad);
        TextView tv_site= view.findViewById(R.id.tv_site);
        RelativeLayout close= view.findViewById(R.id.close);
        TextView tv_chart=view.findViewById(R.id.tv_chart);
        RelativeLayout display_chart=view.findViewById(R.id.display_chart);

        title.setText("数据");
        title.setTextColor(Color.GRAY);

        tv_site.setText("");

        tv_dio.setText("溶氧："+dio+"mg/L");
        tv_dio.setTextColor(Color.GRAY);

        tv_tur.setText("浊度："+tur+"NTU");
        tv_tur.setTextColor(Color.GRAY);

        tv_ph.setText("PH："+ph);
        tv_ph.setTextColor(Color.GRAY);

        tv_con.setText("电导率："+con+"μS/cm");
        tv_con.setTextColor(Color.GRAY);

        tv_tem.setText("温度："+tem+"℃");
        tv_tem.setTextColor(Color.GRAY);

        tv_gad.setText("水质等级："+ gad);
        tv_gad.setTextColor(Color.GRAY);

        tv_chart.setText("查看图表");
        tv_chart.setTextColor(Color.GRAY);

        display_chart.setVisibility(View.INVISIBLE);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myBaiduMap.hideInfoWindow();
            }
        });

        listener = new InfoWindow.OnInfoWindowClickListener() {
            public void onInfoWindowClick() {
                myBaiduMap.hideInfoWindow();
            }
        };

        //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
        InfoWindow mInfoWindow = new InfoWindow(view, latLng, -47);
        //显示InfoWindow
        myBaiduMap.showInfoWindow(mInfoWindow);
    }

    private BroadcastReceiver myReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //搜索
            if (Objects.equals(action, BluetoothDevice.ACTION_FOUND)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState()==BluetoothDevice.BOND_BONDED){//已配对设备
                    System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa  "+device.getName());
                    System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb  "+device.getAddress());

                    rxThread.start();
//                    AcceptThread txThread=new AcceptThread();
//                    txThread.start();
                }
            }
        }
    };

    /**创建Handler将子线程中数据发给主线程*/
    private class BtHandler extends android.os.Handler{
        @SuppressLint("SetTextI18n")
        @Override
        // 子类必须重写此方法，接收数据
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle bundle=msg.getData();
            mobileData=new MobileData();

            String time=bundle.getString("time");
            float tem=bundle.getFloat("tem");
            float ph=bundle.getFloat("ph");
            float dio=bundle.getFloat("dio");
            float tur=bundle.getFloat("tur");
            float con=bundle.getFloat("con");

            tvTem.setText(tem+"℃");
            tvPh.setText(""+ph);
            tvTur.setText(tur+"NTU");
//            tvDio.setText(dio+"mg/L");
//            tvCon.setText(con+"μS/cm");
//            tvGad.setText("B");

            mobileData.setRecordTime(time);
            mobileData.setTemperature(tem);
            mobileData.setPh(ph);
            mobileData.setDissolvedOxygen(dio);
            mobileData.setTurbidity(tur);
            mobileData.setConductivity(con);
            mobileData.setLatBd09ll(myCurrentLat);
            mobileData.setLngBd09ll(myCurrentLng);
        }
    }


    private class MyLocationListener extends BaseLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            super.onReceiveLocation(bdLocation);
            myCurrentLat=getLatLng().latitude;
            myCurrentLng=getLatLng().longitude;
            LatLng latLng_bd09=new LatLng(myCurrentLat,myCurrentLng);
            //坐标转换
            LatLng latLng_gcj02= MapUtil.convertBd09ToGcj02(latLng_bd09);
            //获取高德地址
            String address=MapUtil.getGaodeAddress(latLng_gcj02);
            tvAddress.setText("地址："+address);
        }
    }

    /** 服务端接收信息线程*/
    private class AcceptThread extends Thread {
        private BluetoothServerSocket serverSocket;// 服务端接口
        private BluetoothSocket socket;// 获取到客户端的接口
        private InputStream is;// 获取到输入流
        private OutputStream os;// 获取到输出流

        public AcceptThread() {
            try {
                // 通过UUID监听请求，然后获取到对应的服务端接口
                serverSocket = bluetoothAdapter
                        .listenUsingRfcommWithServiceRecord("haha", MY_UUID);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        public void run() {
            try {
                // 接收其客户端的接口
                socket = serverSocket.accept();
                // 获取到输入流
                is = socket.getInputStream();
                // 获取到输出流
                os = socket.getOutputStream();

                String tx= "AAAA";
                os.write(CommonUtil.getHexBytes(tx));
                os.flush();

                // 无线循环来接收数据
                while (true) {
                    // 创建一个128字节的缓冲
                    byte[] buffer = new byte[128];
                    // 每次读取128字节，并保存其读取的角标
                    int count = is.read(buffer);
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

        }
    }

    /**显示日期选择对话框*/
    @SuppressLint("SetTextI18n")
    private void showDatePickerDialog() {
        LayoutInflater inflater=LayoutInflater.from(MobileMonitorActivity.this);
        View datePickerWindow=inflater.inflate(R.layout.window_date_picker,null);
        final DatePicker datePicker=datePickerWindow.findViewById(R.id.date_picker);
        //设置最大最小日期
        Calendar cal=Calendar.getInstance();
        cal.set(2018,5-1,1);
        datePicker.setMinDate(cal.getTimeInMillis());
        datePicker.setMaxDate(new Date().getTime());
        //日期选择对话框
        AlertDialog datePickerDialog=new AlertDialog.Builder
                (MobileMonitorActivity.this,AlertDialog.THEME_HOLO_LIGHT)
                .setView(datePickerWindow)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        date=datePicker.getYear()+"-"
                                +(datePicker.getMonth()+1)+"-"+datePicker.getDayOfMonth();
                        tvDate.setText("日期："+date);
                        callData(date);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        datePickerDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除注册
        unregisterReceiver(myReceiver);
        bluetoothAdapter.cancelDiscovery();
        try {
            if(inputStream!=null){
                inputStream.close();
            }
            if(outputStream!=null){
                outputStream.close();
            }
            if(btSocket!=null){
                btSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        rxThread.interrupt();
    }
}

