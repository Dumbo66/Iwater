package com.app.dumbo.iwater.activity.pageFour.loginAndRegister;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.AnimFadeActivity;
import com.app.dumbo.iwater.activity.superClass.BaseActivity;
import com.app.dumbo.iwater.constant.Common;
import com.app.dumbo.iwater.constant.RequestCode;
import com.app.dumbo.iwater.util.PermissionUtil;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.util.BGAPhotoHelper;
import cn.bingoogolapple.photopicker.util.BGAPhotoPickerUtil;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.functions.Consumer;

/**
 * Created by dumbo on 2018/8/14
 **/

public class UserInfoActivity extends AnimFadeActivity {
    private static final int REQUEST_CODE_PERMISSION_CHOOSE_PHOTO = 1;
    private static final int REQUEST_CODE_PERMISSION_TAKE_PHOTO = 2;

    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    private static final int REQUEST_CODE_TAKE_PHOTO = 2;
    private static final int REQUEST_CODE_CROP = 3;

    private ActionSheetDialog dialog;
    private SweetAlertDialog requestStoragePermDialog;
    private SweetAlertDialog requestCameraPermDialog;

    private RelativeLayout avatar;
    private BGAPhotoHelper mPhotoHelper;

    private  RelativeLayout nickName;
    private  RelativeLayout userId;
    private  RelativeLayout sex;
    private  RelativeLayout birthday;
    private  RelativeLayout address;

    private ImageView ivAvatar;
    private TextView tvNickName;
    private TextView tvUserId;
    private TextView tvSex;
    private TextView tvBirthday;
    private TextView tvAddress;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_info);
        super.onCreate(savedInstanceState);

        // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
        File takePhotoDir = new File(Common.TAKE_PHOTO_DIR);
        mPhotoHelper = new BGAPhotoHelper(takePhotoDir);

        //从SharedPreferences中取出信息
        SharedPreferences sp=getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String avatarUrl="http://"+ Common.HOST_IP +sp.getString("avatar_url",null);
        String nickName=sp.getString("nick_name",null);
        String sex=sp.getString("sex",null);
        int userId=sp.getInt("user_id",0);

        //显示信息
        BGAImage.display(ivAvatar, R.mipmap.bga_pp_ic_holder_light,avatarUrl, BGABaseAdapterUtil.dp2px(40));
        tvNickName.setText(nickName);
        tvUserId.setText(""+userId);
        tvSex.setText(sex);
    }

    @Override
    public void initView() {
        super.initView();
        avatar=findViewById(R.id.rl_avatar);
        nickName=findViewById(R.id.rl_nick_name);
        userId=findViewById(R.id.rl_user_id);
        sex=findViewById(R.id.rl_sex);
        birthday=findViewById(R.id.rl_birthday);
        address=findViewById(R.id.rl_address);

        ivAvatar=findViewById(R.id.iv_avatar);
        tvNickName=findViewById(R.id.tv_nick_name);
        tvUserId=findViewById(R.id.tv_user_id);
        tvSex=findViewById(R.id.tv_sex);
        tvBirthday=findViewById(R.id.tv_birthday);
        tvAddress=findViewById(R.id.tv_address);
    }

    @Override
    public void setListener() {
        avatar.setOnClickListener(this);
        nickName.setOnClickListener(this);
        userId.setOnClickListener(this);
        sex.setOnClickListener(this);
        birthday.setOnClickListener(this);
        address.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //头像栏监听
            case R.id.rl_avatar:
                String[] str={"拍照","从相册选择"};
                dialog=new ActionSheetDialog(UserInfoActivity.this,str,null);
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

            //昵称栏监听
            case R.id.rl_nick_name:
                break;

            //性别栏监听
            case R.id.rl_sex:
                break;

            //输入栏监听
            case R.id.rl_birthday:
                break;

            //地区栏监听
            case R.id.rl_address:
                break;
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
                            Toast.makeText(UserInfoActivity.this, "该功能需要开启存储权限，请您开启！", Toast.LENGTH_SHORT).show();
                        } else {
                            // 用户拒绝了该权限,且勾选不在询问
                            showOpenPermissionDialog();
                        }
                    }

                    private void showOpenPermissionDialog() {
                        requestStoragePermDialog = new SweetAlertDialog(UserInfoActivity.this);
                        requestStoragePermDialog.setTitleText("权限申请")
                                .setContentText("该功能需要在“设置-应用-掌上治水-权限”中开启存储权限，请您开启！")
                                .setConfirmText("去打开")
                                .setCancelText("取消")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        PermissionUtil permPageUtil = new PermissionUtil(UserInfoActivity.this);
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
        rxPermissions.requestEach(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            try {
                                startActivityForResult(mPhotoHelper.getTakePhotoIntent(), REQUEST_CODE_TAKE_PHOTO);
                            } catch (Exception e) {
                                BGAPhotoPickerUtil.show(R.string.bga_pp_not_support_take_photo);
                            }
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限,未勾选不在询问
                            Toast.makeText(UserInfoActivity.this, "该功能需要开启存储权限，请您开启！", Toast.LENGTH_SHORT).show();
                        } else {
                            // 用户拒绝了该权限,且勾选不在询问
                            showOpenPermissionDialog();
                        }
                    }

                    private void showOpenPermissionDialog() {
                        requestCameraPermDialog = new SweetAlertDialog(UserInfoActivity.this);
                        requestCameraPermDialog.setTitleText("权限申请")
                                .setContentText("该功能需要在“设置-应用-掌上治水-权限”中开启相机权限，请您开启！")
                                .setConfirmText("去打开")
                                .setCancelText("取消")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        PermissionUtil permPageUtil = new PermissionUtil(UserInfoActivity.this);
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
