package com.app.dumbo.iwater.activity.pageOne;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
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
import com.app.dumbo.iwater.retrofit2.entity.Reception;
import com.app.dumbo.iwater.util.MapUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
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
        implements EasyPermissions.PermissionCallbacks,BGASortableNinePhotoLayout.Delegate{
    private static final int PRC_PHOTO_PICKER = 1;

    private static final int RC_CHOOSE_PHOTO = 2;
    private static final int RC_PHOTO_PREVIEW = 3;

    private static final String EXTRA_MOMENT = "EXTRA_MOMENT";

    private BGASortableNinePhotoLayout snpl;//拖拽排序九宫格控件
    private RelativeLayout submit;//“提交”按钮
    private TextView tvAddress;//我的地址
    private EditText etDesc;//描述

    public LocationClient myLocClient =null;
    private MyLocationListener myListener =new MyLocationListener() ;
    private MyLocationData myLocData;//位置信息对象
    private double lat;//纬度
    private double lng;//经度
    private String address;//位置

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_problem_record);
        super.onCreate(savedInstanceState);

        //控件初始化
        initView();

        //控件监听
        listenWidget();
    }

    /**控件初始化*/
    public void initView(){
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

    /**控件监听*/
    private void listenWidget() {

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
                        map.put("desc",RequestBody.create(textType,etDesc.getText().toString()));
                        map.put("addr",RequestBody.create(textType,address));
                        map.put("time",RequestBody.create(textType,sdf.format(date)));
                        map.put("user_id",RequestBody.create(textType,1234+""));
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
                }
            });
        }
    }

    @Override
    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout,
                                        View view, int position, ArrayList<String> models) {
        addPhoto();
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
        Toast.makeText(this, "排序发生变化", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**添加照片*/
    private void addPhoto() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 拍照后照片的存放目录,如果不传递该参数的话则不开启图库里的拍照功能
            File takePhotoDir = new File(Common.TAKE_PHOTO_DIR);

            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                    .cameraFileDir(takePhotoDir) // 拍照后照片的存放目录
                    .maxChooseCount(snpl.getMaxItemCount() - snpl.getItemCount()) // 图片选择张数的最大值
                    .selectedPhotos(null) // 当前已选中的图片路径集合
                    .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                    .build();
            startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO);
        } else {
            EasyPermissions.requestPermissions(this,
                    "图片选择需要以下权限:\n\n1.访问设备上的照片\n\n2.拍照", PRC_PHOTO_PICKER, perms);
        }
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

    /**获取已选择的图片集合*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            snpl.addMoreData(BGAPhotoPickerActivity.getSelectedPhotos(data));
        } else if (requestCode == RC_PHOTO_PREVIEW) {
            snpl.setData(BGAPhotoPickerPreviewActivity.getSelectedPhotos(data));
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == PRC_PHOTO_PICKER) {
            Toast.makeText(this, "您拒绝了「添加图片」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }
}
