package com.app.dumbo.iwater.activity.pageFour.loginAndRegister;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.MainActivity;
import com.app.dumbo.iwater.activity.superClass.AnimFadeActivity;
import com.app.dumbo.iwater.constant.Common;
import com.app.dumbo.iwater.constant.RequestCode;
import com.app.dumbo.iwater.constant.ResponseCode;
import com.app.dumbo.iwater.retrofit2.Retrofit2;
import com.app.dumbo.iwater.util.CommonUtil;
import com.app.dumbo.iwater.util.EncryptUtil;
import com.app.dumbo.iwater.util.PermissionUtil;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.jayway.jsonpath.JsonPath;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.util.BGAPhotoHelper;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dumbo on 2018/8/20
 **/

public class FillUserInfoActivity extends AnimFadeActivity {
    private static final int REQUEST_CODE_PERMISSION_CHOOSE_PHOTO = 1;
    private static final int REQUEST_CODE_PERMISSION_TAKE_PHOTO = 2;

    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    private static final int REQUEST_CODE_TAKE_PHOTO = 2;
    private static final int REQUEST_CODE_CROP = 3;

    private BGAPhotoHelper mPhotoHelper;
    private RelativeLayout avatar;//设置头像控件
    private ImageView ivAvatar;
    private EditText nickName;//设置昵称控件
    private RelativeLayout clearNickName;//清空昵称栏
    private RadioGroup sex;//设置性别控件
    private Button register;//注册按钮

    private ActionSheetDialog dialog;
    private SweetAlertDialog requestStoragePermDialog;
    private SweetAlertDialog requestCameraPermDialog;

    private String phone;//获取的电话号码
    private String encryptedPasw;//加密的密码
    private String nickNameStr;//昵称
    private String sexStr="";//性别

    private Map<String,RequestBody> map;//上传的文本
    private File avatarFile;//上传的头像

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_fill_user_info);
        super.onCreate(savedInstanceState);

        // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
        File takePhotoDir = new File(Common.TAKE_PHOTO_DIR);
        mPhotoHelper = new BGAPhotoHelper(takePhotoDir);
    }

    @Override
    public void initView() {
        super.initView();
        avatar=findViewById(R.id.rl_avatar);
        ivAvatar=findViewById(R.id.iv_avatar);
        nickName=findViewById(R.id.et_nick_name);
        clearNickName=findViewById(R.id.rl_clear_nick_name);
        sex=findViewById(R.id.rg_sex);
        register=findViewById(R.id.btn_register);
    }

    @Override
    public void setListener() {
        super.setListener();
        avatar.setOnClickListener(this);
        clearNickName.setOnClickListener(this);
        register.setOnClickListener(this);

        //昵称栏监听
        nickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                //若输入非空，显示清除按钮,否则不显示
                if(s.length()!=0){
                    clearNickName.setVisibility(View.VISIBLE);
                }else{
                    clearNickName.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        nickNameStr=nickName.getText().toString();
        //性别栏监听
        sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==R.id.rb_male){
                    sexStr="男";
                }else if(checkedId==R.id.rb_female){
                    sexStr="女";
                }
            }
        });
        switch (v.getId()){
            //设置头像
            case R.id.rl_avatar:
                String[] str={"拍照","从相册选择"};
                dialog=new ActionSheetDialog(FillUserInfoActivity.this,str,null);
                dialog.title("修改头像")
                        .itemTextColor(getResources().getColor(R.color.colorGreen))
                        .cancelText(getResources().getColor(R.color.colorGreen))
                        .setOnOperItemClickL(new OnOperItemClickL() {
                            @Override
                            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if(position==0){
                                    takePhoto();//拍照
                                }else if(position==1){
                                    choosePhoto();//从相册选择
                                }
                            }
                        });
                dialog.show();
                break;

            //清空昵称
            case R.id.rl_clear_nick_name:
                nickName.setText("");
                break;

            //注册按钮监听
            case R.id.btn_register:
                if(mPhotoHelper.getCropFilePath()==null){
                    Toast.makeText(FillUserInfoActivity.this,"请选择头像!",Toast.LENGTH_SHORT).show();
                }
                else if(nickNameStr.length()==0){
                    Toast.makeText(FillUserInfoActivity.this,"请输入昵称!",Toast.LENGTH_SHORT).show();
                }
                else if(nickNameStr.length()>8){
                    Toast.makeText(FillUserInfoActivity.this,"请输入1-8位中文/字母/数字!",Toast.LENGTH_SHORT).show();
                }
                else if(sexStr.length()==0){
                    Toast.makeText(FillUserInfoActivity.this,"请选择性别!",Toast.LENGTH_SHORT).show();
                }
                else{
                    register();
                }
        }
    }

    /**从相册选择照片*/
    @SuppressLint("CheckResult")
    public void choosePhoto() {
        //动态权限请求
        RxPermissions rxPermissions=new RxPermissions(this);
        rxPermissions.requestEach(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            startActivityForResult(mPhotoHelper.getChooseSystemGalleryIntent(), REQUEST_CODE_CHOOSE_PHOTO);
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限,未勾选不在询问
                            Toast.makeText(FillUserInfoActivity.this, "该功能需要开启存储权限，请您开启！", Toast.LENGTH_SHORT).show();
                        } else {
                            // 用户拒绝了该权限,且勾选不在询问
                            showOpenPermissionDialog();
                        }
                    }

                    private void showOpenPermissionDialog() {
                        requestStoragePermDialog = new SweetAlertDialog(FillUserInfoActivity.this);
                        requestStoragePermDialog.setTitleText("权限申请")
                                .setContentText("该功能需要在“设置-应用-掌上治水-权限”中开启存储权限，请您开启！")
                                .setConfirmText("去打开")
                                .setCancelText("取消")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        PermissionUtil permPageUtil = new PermissionUtil(FillUserInfoActivity.this);
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

    /**拍摄照片*/
    @SuppressLint("CheckResult")
    public void takePhoto() {
        //动态权限请求
        RxPermissions rxPermissions=new RxPermissions(this);
        rxPermissions.requestEachCombined(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe(new Consumer<Permission>() {
                @Override
                public void accept(Permission permission) throws Exception {
                    if (permission.granted) {
                        // 用户已经同意所有权限
                        try {
                            startActivityForResult(mPhotoHelper.getTakePhotoIntent(), REQUEST_CODE_TAKE_PHOTO);
                        } catch (Exception e) {
                            BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_take_photo);
                        }
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // 用户拒绝了该权限,未勾选不在询问
                        Toast.makeText(FillUserInfoActivity.this, "该功能需要开启相机和存储权限，请您开启！", Toast.LENGTH_SHORT).show();
                    } else {
                        // 用户拒绝了该权限,且勾选不在询问
                        showOpenPermissionDialog();
                    }
                }

                private void showOpenPermissionDialog() {
                    requestCameraPermDialog = new SweetAlertDialog(FillUserInfoActivity.this);
                    requestCameraPermDialog.setTitleText("权限申请")
                            .setContentText("该功能需要在“设置-应用-掌上治水-权限”中开启相机和存储权限，请您开启！")
                            .setConfirmText("去打开")
                            .setCancelText("取消")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    PermissionUtil permPageUtil = new PermissionUtil(FillUserInfoActivity.this);
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

    /*注册*/
    private void register() {
        //接收来自注册页面跳转值
        Intent it=getIntent();
        phone =it.getStringExtra("phone");
        String password=it.getStringExtra("password");
        //加密
        encryptedPasw=EncryptUtil.encrypt(password);
        nickNameStr=nickName.getText().toString();
        //性别栏监听
        sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==R.id.rb_male){
                    sexStr="男";
                }else if(checkedId==R.id.rb_female){
                    sexStr="女";
                }
            }
        });

        //上传的文本map
        MediaType textType=MediaType.parse("text/plain");
        map=new HashMap<>();
        map.put("phone",RequestBody.create(textType,phone));
        map.put("password",RequestBody.create(textType,encryptedPasw));
        map.put("nick_name",RequestBody.create(textType,nickNameStr));
        map.put("sex",RequestBody.create(textType,sexStr));

        //上传的头像
        avatarFile=new File(mPhotoHelper.getCropFilePath());

        //创建RequestBody，其中`multipart/form-data`为编码类型
        RequestBody requestBody=RequestBody.create(MediaType.parse("multipart/form-data"),avatarFile);
        MultipartBody.Part multipartBody=MultipartBody.Part.createFormData("file",avatarFile.getName(),requestBody);

        Call<ResponseBody> registerCall = Retrofit2.getService().register(map,multipartBody);
        registerCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                String responseStr = null;
                try {
                    responseStr = Objects.requireNonNull(response.body()).string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (responseStr != null) {
                    int code = JsonPath.read(responseStr, "$.code");
                    String message = JsonPath.read(responseStr, "$.message");
                    switch (code) {
                        case ResponseCode.OK://登录成功
                            String accessJwt = JsonPath.read(responseStr, "$.data.access_jwt");
                            String refreshJwt = JsonPath.read(responseStr, "$.data.refresh_jwt");
                            int userId = JsonPath.read(responseStr, "$.data.user_id");
                            String avatarUrl = JsonPath.read(responseStr, "$.data.avatar_url");

                            //新建SharedPreferences对象
                            SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
                            //添加数据(存储jwt和用户信息)
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("access_jwt", accessJwt);
                            editor.putString("refresh_jwt", refreshJwt);
                            editor.putString("avatar_url", avatarUrl);
                            editor.putString("nick_name",nickNameStr);
                            editor.putString("sex",sexStr);
                            editor.putInt("user_id", userId);
                            editor.apply();

                            CommonUtil.skipActivityByFade(FillUserInfoActivity.this,MainActivity.class);
                            register.setText("注册中……");

                            Intent intent = new Intent(FillUserInfoActivity.this, MainActivity.class);
                            intent.putExtra("page", 3);
                            //在此activity启动之前的任何与此activity相关联的task都会被清除
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(FillUserInfoActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                System.out.println("注册失败");
                System.out.println(t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== RequestCode.REQUEST_PERMISSION_CODE){
            if(requestStoragePermDialog!=null){
                requestStoragePermDialog.dismiss();
            }else if(requestCameraPermDialog!=null){
                requestCameraPermDialog.dismiss();
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
                try {
                    startActivityForResult(mPhotoHelper.getCropIntent(mPhotoHelper.getFilePathFromUri(data.getData()), 200, 200), REQUEST_CODE_CROP);
                } catch (Exception e) {
                    mPhotoHelper.deleteCropFile();
                    BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_crop);
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                try {
                    startActivityForResult(mPhotoHelper.getCropIntent(mPhotoHelper.getCameraFilePath(), 200, 200), REQUEST_CODE_CROP);
                } catch (Exception e) {
                    mPhotoHelper.deleteCameraFile();
                    mPhotoHelper.deleteCropFile();
                    BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_crop);
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_CROP) {
                BGAImage.display(ivAvatar, R.mipmap.bga_pp_ic_holder_light, mPhotoHelper.getCropFilePath(), BGABaseAdapterUtil.dp2px(200));
            }
            dialog.dismiss();
        } else {
            if (requestCode == REQUEST_CODE_CROP) {
                mPhotoHelper.deleteCameraFile();
                mPhotoHelper.deleteCropFile();
            }
            dialog.dismiss();
        }
    }
}
