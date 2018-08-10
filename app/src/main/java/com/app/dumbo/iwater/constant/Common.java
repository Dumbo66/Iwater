package com.app.dumbo.iwater.constant;

import android.os.Environment;

import java.io.File;

/**
 * Created by dumbo on 2018/8/5
 **/

public class Common {
    //服务器IP
    public static final String HOST_IP ="192.168.1.67";

    //拍照存储路径
    public static final String TAKE_PHOTO_DIR= Environment.getExternalStorageDirectory()
                                               +File.separator+"Dumbo"+File.separator+"Photos";

    //压缩图片存储路径
    public static final String COMPRESSED_PHOTO_DIR= Environment.getExternalStorageDirectory()
            +File.separator+"Dumbo"+File.separator+"CompressedPhotos";

}
