package com.app.dumbo.iwater.activity.pageOne;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.MainActivity;
import com.app.dumbo.iwater.activity.pageFour.loginAndRegister.LoginRegisterActivity;
import com.app.dumbo.iwater.activity.superClass.AnimFadeActivity;
import com.app.dumbo.iwater.constant.Common;
import com.app.dumbo.iwater.constant.RequestCode;
import com.app.dumbo.iwater.constant.ResponseCode;
import com.app.dumbo.iwater.retrofit2.Retrofit2;
import com.app.dumbo.iwater.retrofit2.entity.reception.Reception;
import com.app.dumbo.iwater.util.CommonUtil;
import com.app.dumbo.iwater.util.MapUtil;
import com.app.dumbo.iwater.util.PermissionUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;

/**
 * Created by dumbo on 2018/3/11.
 */

@SuppressLint("Registered")
public class AddMomentActivity extends AnimFadeActivity
        implements BGASortableNinePhotoLayout.Delegate{
    private static final int PRC_PHOTO_PICKER = 1;

    private static final int RC_CHOOSE_PHOTO = 2;
    private static final int RC_PHOTO_PREVIEW = 3;

    private BGASortableNinePhotoLayout snpl;//拖拽排序九宫格控件
    private RelativeLayout submit;//“提交”按钮
    private TextView tvAddress;//我的地址
    private EditText etDesc;//描述

    private SweetAlertDialog requestStoragePermDialog;

    public LocationClient myLocClient =null;
    private MyLocationListener myListener =new MyLocationListener() ;
    private double lat;//纬度
    private double lng;//经度
    private String address;//位置

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_moment);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView(){
        super.initView();
        snpl=findViewById(R.id.snpl);
        submit=findViewById(R.id.rl_submit);
        tvAddress=findViewById(R.id.tv_address);
        etDesc=findViewById(R.id.et_desc);

        snpl.setMaxItemCount(9);//最多显示
        snpl.setEditable(true);//可编辑
        snpl.setPlusEnable(true);//显示九图控件的加号按钮
        snpl.setSortable(true);//拖拽排序
        snpl.setDelegate(this);// 设置拖拽排序控件的代理

        //定位获取经纬度
        //初始化LocationClient类,定位初始化
        myLocClient =new LocationClient(getApplicationContext());
        //注册监听函数
        myLocClient.registerLocationListener(myListener);
        //配置定位SDK参数
        LocationClientOption option=new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(0);//单次定位
        myLocClient.setLocOption(option);
        myLocClient.start();
    }

    @Override
    public void setListener() {
        super.setListener();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    /**定位SDK监听函数*/
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //获取实时经纬度
            lat = bdLocation.getLatitude();
            lng = bdLocation.getLongitude();
            LatLng latLng_bd09=new LatLng(lat,lng);
            //坐标转换
            LatLng latLng_gcj02= MapUtil.convertBd09ToGcj02(latLng_bd09);
            //获取高德地址
            address=MapUtil.getGaodeAddress(latLng_gcj02);
            tvAddress.setText(address);

            //"提交"控件监听
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final SharedPreferences sp= Objects.requireNonNull(AddMomentActivity.this)
                            .getSharedPreferences("user_info", Context.MODE_PRIVATE);
                    String avatarUrl=sp.getString("avatar_url",null);
                    String nickName=sp.getString("nick_name",null);
                    int userId=sp.getInt("user_id",0);

                    if (userId!=0){
                        if(etDesc.getText().toString().equals("")){
                            Toast.makeText(AddMomentActivity.this,
                                    "问题描述不能为空！", Toast.LENGTH_SHORT).show();
                        }else if(snpl.getData().isEmpty()){
                            Toast.makeText(AddMomentActivity.this,
                                    "图片不能为空！", Toast.LENGTH_SHORT).show();
                        }else{
                            //获取时间
                            Date date=new Date();
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                            //上传的文本map
                            MediaType textType=MediaType.parse("text/plain");
                            final Map<String,RequestBody> map=new HashMap<>();
                            map.put("user_id",RequestBody.create(textType,userId+""));
                            map.put("avatar_url",RequestBody.create(textType,avatarUrl));
                            map.put("nick_name",RequestBody.create(textType,nickName));
                            map.put("record_time",RequestBody.create(textType,sdf.format(date)));
                            map.put("description",RequestBody.create(textType,etDesc.getText().toString()));
                            map.put("address",RequestBody.create(textType,address));
                            map.put("lat",RequestBody.create(textType,""+lat));
                            map.put("lng",RequestBody.create(textType,""+lng));

                            //新建图片压缩缓存文件夹
                            File file=new File(Common.COMPRESSED_PHOTO_DIR);
                            if (!file.exists()){
                                file.mkdirs();
                            }

                            //使用LuBan压缩图片
                            Observable.just(snpl.getData())
                                    .observeOn(Schedulers.io())
                                    .map(new Function<List<String>, List<File>>() {
                                        @Override
                                        public List<File> apply(List<String> list) throws Exception {
                                            return Luban.with(getApplicationContext())
                                                    .load(snpl.getData())
                                                    .ignoreBy(100)
                                                    .setTargetDir(Common.COMPRESSED_PHOTO_DIR)
                                                    .filter(new CompressionPredicate() {
                                                        @Override
                                                        public boolean apply(String path) {
                                                            return !(TextUtils.isEmpty(path) ||
                                                                    path.toLowerCase().endsWith(".gif"));
                                                        }
                                                    }).get();
                                        }
                                    }).observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<List<File>>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onNext(List<File> list) {
                                            uploadMoment(map,list);//上传
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                        }
                    }else{
                        showGoLoginDialog();//显示请先登录对话框
                    }
                }
            });
        }
    }

    @Override
    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout,
                                        View view, int position, ArrayList<String> models) {
        //请求权限后跳转到照片选择
        requestPermissionToActivity();
    }

    @Override
    public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout,
                                           View view, int position, String model, ArrayList<String> models) {
        snpl.removeItem(position);
    }

    /**预览已选择的图片*/
    @Override
    public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout,
                                     View view, int position, String model, ArrayList<String> models) {
        Intent photoPickerPreviewIntent = new BGAPhotoPickerPreviewActivity.IntentBuilder(this)
                .previewPhotos(models) // 当前预览的图片路径集合
                .selectedPhotos(models) // 当前已选中的图片路径集合
                .maxChooseCount(snpl.getMaxItemCount()) // 图片选择张数的最大值
                .currentPosition(position) // 当前预览图片的索引
                .isFromTakePhoto(false) // 是否是拍完照后跳转过来
                .build();
        startActivityForResult(photoPickerPreviewIntent, RC_PHOTO_PREVIEW);
    }

    @Override
    public void onNinePhotoItemExchanged(BGASortableNinePhotoLayout sortableNinePhotoLayout,
                                         int fromPosition, int toPosition, ArrayList<String> models) {
        Toast.makeText(this, "排序改变！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {}

    /**添加照片*/
    private void addPhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        // 拍照后照片的存放目录,如果不传递该参数的话则不开启图库里的拍照功能
        File takePhotoDir = new File(Common.TAKE_PHOTO_DIR);

        Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                .cameraFileDir(takePhotoDir) // 拍照后照片的存放目录
                .maxChooseCount(snpl.getMaxItemCount() - snpl.getItemCount()) // 图片选择张数的最大值
                .selectedPhotos(null) // 当前已选中的图片路径集合
                .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                .build();
        startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO);
    }

    /**上传Moment*/
    public void uploadMoment(Map<String,RequestBody> map, List<File> list) {
        RequestBody[] requestBodies=new RequestBody[list.size()];
        MultipartBody.Part[] multipartBody=new MultipartBody.Part[list.size()];

        for(int i=0;i<list.size();i++){
            //创建RequestBody，其中`multipart/form-data`为编码类型
            requestBodies[i]=RequestBody.create(MediaType.parse("multipart/form-data"),list.get(i));

            //创建`MultipartBody.Part`，其中需要注意第一个参数`files`需要与服务器@RequestParams("files")对应
            multipartBody[i]=MultipartBody.Part.createFormData("files",list.get(i).getName(),requestBodies[i]);
        }

        // 调用uploadPictures上传图文
        Call<Reception> uploadPicturesCall= Retrofit2.getService().uploadPictures(map,multipartBody);
        uploadPicturesCall.enqueue(new Callback<Reception>() {
            @Override
            public void onResponse(Call<Reception> call, Response<Reception> response) {
                if (response.body().getCode()== ResponseCode.OK){
                    Toast.makeText(AddMomentActivity.this,"上传成功！",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("page",1);
                    //A-->B-->C-->A   跳转回A时，B，C 被finish()；
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Reception> call, Throwable t) {
                System.out.println("上传图片失败！！");
            }
        });
    }

    /**存储权限请求*/
    @SuppressLint("CheckResult")
    private void requestPermissionToActivity() {
        //动态权限请求
        RxPermissions rxPermissions=new RxPermissions(this);
        rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if(permission.granted) {
                            // 用户已经同意该权限
                            addPhoto();
                        }else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限,未勾选不在询问
                            Toast.makeText(AddMomentActivity.this,"该功能需要开启存储权限，请您开启！",Toast.LENGTH_SHORT).show();
                        } else{
                            // 用户拒绝了该权限,且勾选不在询问
                            showOpenPermissionDialog();
                        }
                    }

                    private void showOpenPermissionDialog() {
                        requestStoragePermDialog=new SweetAlertDialog(AddMomentActivity.this);
                        requestStoragePermDialog .setTitleText("权限申请")
                                .setContentText("该功能需要在“设置-应用-掌上治水-权限”中开启存储权限，请您开启！")
                                .setConfirmText("去打开")
                                .setCancelText("取消")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        PermissionUtil permPageUtil = new PermissionUtil(AddMomentActivity.this);
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
                });
    }

    /**显示请先登录对话框*/
    private void showGoLoginDialog() {
        new SweetAlertDialog(AddMomentActivity.this)
                .setContentText("请先登录，方可使用该功能！")
                .setConfirmText("去登录")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        CommonUtil.skipActivityByFade(AddMomentActivity.this, LoginRegisterActivity.class);
                    }
                })
                .setCancelText("取消")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                }).show();
    }

    /**获取已选择的图片集合*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            snpl.addMoreData(BGAPhotoPickerActivity.getSelectedPhotos(data));
        } else if (requestCode == RC_PHOTO_PREVIEW) {
            snpl.setData(BGAPhotoPickerPreviewActivity.getSelectedPhotos(data));
        } else if(requestCode== RequestCode.REQUEST_PERMISSION_CODE){
            requestStoragePermDialog.dismiss();
        }
    }
}
