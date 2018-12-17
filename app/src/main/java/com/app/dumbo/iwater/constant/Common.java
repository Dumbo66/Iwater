package com.app.dumbo.iwater.constant;

import android.os.Environment;

import java.io.File;

/**
 * Created by dumbo on 2018/8/5
 **/

public class Common {
    //服务器IP
//    public static final String HOST_IP ="192.168.1.67";
    public static final String HOST_IP ="39.106.170.35";

    //拍照存储路径
    public static final String TAKE_PHOTO_DIR= Environment.getExternalStorageDirectory()
                                               +File.separator+"Dumbo"+File.separator+"Photos";

    //压缩图片存储路径
    public static final String COMPRESSED_PHOTO_DIR= Environment.getExternalStorageDirectory()
            +File.separator+"Dumbo"+File.separator+"CompressedPhotos";

    //Moments每次请求的记录数目
    public static final int MOMENTS_PAGE_SIZE=20;
}
