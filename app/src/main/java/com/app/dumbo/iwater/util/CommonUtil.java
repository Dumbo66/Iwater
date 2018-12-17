package com.app.dumbo.iwater.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.app.dumbo.iwater.activity.MainActivity;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.jaeger.library.StatusBarUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by baidu on 17/1/23.
 */

public class CommonUtil {
    /**
     * 判断GPS是否已打开
     * @param context
     * @return boolean
     */
    public static boolean GpsIsOpened(Context context) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 将字符串转化为十六进制字节流
     * @param str 输入字符串
     * @return 十六进制字节流
     */
    public static byte[] getHexBytes(String str) {
        int len = str.length() / 2;
        char[] chars = str.toCharArray();
        String[] hexStr = new String[len];
        byte[] bytes = new byte[len];
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
        }
        return bytes;
    }

    /**
     * 将十六进制字节流转化为字符串
     * @param bytes 输入字节流
     * @param begin 开始字节
     * @param bytes 停止字节
     * @return 十六进制字节流
     */
    public static String bytesToHexStr(byte[] bytes, int begin, int end) {
        //字节转十进制
        StringBuilder hexBuilder = new StringBuilder(2 * (end - begin));
        for(int i=begin ;i < end; i++) {
            hexBuilder.append(Character.forDigit((bytes[i] & 0xF0) >> 4, 16)); // 转化高四位
            hexBuilder.append(Character.forDigit((bytes[i] & 0x0F), 16)); // 转化低四位
        }
        String str=hexBuilder.toString().toUpperCase();
        return str;
    }

    /**
     * 将layout变为BitMap
     */
    public static BitmapDescriptor getBitmap(Context context, int resource){
        View view = LayoutInflater.from(context).inflate(resource, null);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if(bitmap == null){
            Log.e("", "bitmap is null");
        }
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /*判断是否为手机号*/
    public static boolean isPhone(String telNumber){
//        中国电信号段
//        133、149、153、173、177、180、181、189、199
//        中国联通号段
//        130、131、132、145、155、156、166、175、176、185、186
//        中国移动号段
//        134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188、198
//        其他号段
//        14号段以前为上网卡专属号段，如中国联通的是145，中国移动的是147等等。
//        虚拟运营商
//        电信：1700、1701、1702
//        移动：1703、1705、1706
//        联通：1704、1707、1708、1709、171
//        卫星通信：1349
        String regExp = "^((13[0-9])|(14[5,7,9])|(15[^4])|(166)|(17[3,5,6,7,8])|(18[0-9])|19[8,9])\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(telNumber);
        return m.matches() && (!telNumber.substring(0,4).equals("1349"));
    }

    /**跳转到目的Activity*/
    public static void skipActivityByFade(Activity activity,Class descClass){
        Intent intent=new Intent(activity,descClass);
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
    public static void skipActivityBySlide(Activity activity,Class descClass){
        Intent intent=new Intent(activity,descClass);
        activity.startActivity(intent);
    }

    /**
     * sp转换成px
     */
    public static int sp2px(Context context,float spValue){
        float fontScale=context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue*fontScale+0.5f);
    }
}
