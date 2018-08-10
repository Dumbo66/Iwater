package com.app.dumbo.iwater.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.telephony.TelephonyManager;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.constant.SecretKey;
import com.app.dumbo.iwater.retrofit2.ApiService;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.SortType;
import com.baidu.trace.model.TraceLocation;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by baidu on 17/2/9.
 */

public class MapUtil {
    private static String address;
    private static DecimalFormat df = new DecimalFormat("######0.00");
    public static final double DISTANCE = 0.0001;

    /**
     * 将轨迹实时定位点转换为地图坐标
     * @param location 将轨迹实时定位坐标对象
     * @return 地图坐标对象
     */
    public static LatLng convertTraceLocation2Map(TraceLocation location) {
        if (null == location) {
            return null;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        if (Math.abs(latitude - 0.0) < 0.000001 && Math.abs(longitude - 0.0) < 0.000001) {
            return null;
        }
        LatLng currentLatLng = new LatLng(latitude, longitude);
        if (CoordType.wgs84 == location.getCoordType()) {
            LatLng sourceLatLng = currentLatLng;
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            converter.coord(sourceLatLng);
            currentLatLng = converter.convert();
        }
        return currentLatLng;
    }

    /**
     * 将地图坐标转换轨迹坐标
     * @param latLng 地图坐标对象
     * @return 轨迹坐标对象
     */
    public static com.baidu.trace.model.LatLng convertMap2Trace(LatLng latLng) {
        return new com.baidu.trace.model.LatLng(latLng.latitude, latLng.longitude);
    }

    /**
     * 将轨迹坐标对象转换为地图坐标对象
     * @param traceLatLng  轨迹坐标对象
     * @return 地图坐标对象
     */
    public static LatLng convertTrace2Map(com.baidu.trace.model.LatLng traceLatLng) {
        return new LatLng(traceLatLng.latitude, traceLatLng.longitude);
    }

    /**
     * 在地图上绘制历史轨迹
     * @param
     * @return null
     */
    public static void drawHistoryTrack(Context context,BaiduMap baiduMap, List<LatLng> points, SortType sortType) {
        //路线覆盖物
        Overlay polylineOverlay=null;

        if (points==null){
            return;
        }

        //获取起点和终点坐标
        LatLng startPoint;
        LatLng endPoint;
        if (sortType == SortType.asc) {
            startPoint = points.get(0);
            endPoint = points.get(points.size() - 1);
        } else {
            startPoint = points.get(points.size() - 1);
            endPoint = points.get(0);
        }

        //将图片转为Bitmap
        BitmapDescriptor mapMarkerStart=CommonUtil.getBitmap(context,R.layout.map_marker_start);
        BitmapDescriptor mapMarkerEnd=CommonUtil.getBitmap(context,R.layout.map_marker_end);

        // 添加起点图标
        OverlayOptions startOptions = new MarkerOptions()
                .icon(mapMarkerStart)
                .position(startPoint)
                .zIndex(9)
                .draggable(true);

        // 添加终点图标
        OverlayOptions endOptions = new MarkerOptions()
                .icon(mapMarkerEnd)
                .position(endPoint)
                .zIndex(9)
                .draggable(true);

        // 添加路线（轨迹）
        OverlayOptions polylineOptions = new PolylineOptions()
                .width(10)
                .color(Color.BLUE)
                .points(points);
        baiduMap.addOverlay(startOptions);
        baiduMap.addOverlay(endOptions);
        polylineOverlay = baiduMap.addOverlay(polylineOptions);
    }

    /**
     * 将bd09坐标转换为gcj02坐标
     * @param latLng_bd09 bd09坐标
     * @return gcj02坐标对象
     */
    public static LatLng convertBd09ToGcj02(LatLng latLng_bd09){
        CoordinateConverter converter=new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.BD09LL);
        converter.coord(latLng_bd09);
        return converter.convert();
    }

    /**
     * 发送经纬度转换为详细地址
     * @param latLng_gcj02 gcj02坐标
     * @return 地址字符串
     */
    public static String getGaodeAddress(LatLng latLng_gcj02){
        //Retrofit初始化
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://restapi.amap.com/v3/geocode/")// 设置网络请求baseUrl
                .addConverterFactory(GsonConverterFactory.create())// 设置使用Gson解析
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())// 支持RxJava
                .build();
        ApiService service= retrofit.create(ApiService.class);

        //location=lng,lat
        String location=latLng_gcj02.longitude+","+latLng_gcj02.latitude;

        //请求解析
        String str="例：https://restapi.amap.com/v3/geocode/regeo?key=7ed9747e56de217e51e6fdf710eb3faf&location=117.127354,39.250132";
        Call<ResponseBody> call=service.getGaodeAddress(SecretKey.GAODE_REQUEST_KEY, location);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    String jsonStr=response.body().string();
                    if(jsonStr!=null){
                        address = JsonPath.read(jsonStr,"$.regeocode.formatted_address");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("获取地址失败");
            }
        });
        return address;
    }

    /**
     * 添加marker
     * @param baiduMap 百度地图
     * @param latLng 经纬度
     * @param grade 等级
     * @return Marker
     */
    public static Marker addMarkers(Activity activity,BaiduMap baiduMap, LatLng latLng, char grade) {
        int resource = 0;
        switch (grade){
            case 'A':
                resource=R.layout.map_marker_a;break;
            case 'B':
                resource=R.layout.map_marker_b;break;
            case 'C':
                resource=R.layout.map_marker_c;break;
            case 'D':
                resource=R.layout.map_marker_d;break;
            case 'E':
                resource=R.layout.map_marker_e;break;
            case 'F':
                resource=R.layout.map_marker_f;break;
        }

        //构建marker图标
        BitmapDescriptor bitmap= CommonUtil.getBitmap(activity,resource);
        //构建MarkerOption，用于在地图上添加Marker
        MarkerOptions option= new MarkerOptions().position(latLng).icon(bitmap);
        //在地图上添加Marker，并显示
        option.animateType(MarkerOptions.MarkerAnimateType.grow);
        return (Marker) baiduMap.addOverlay(option);
    }






















    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    /**
     * 获取当前时间戳(单位：秒)
     *
     * @return
     */
    public static long getCurrentTime() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 校验double数值是否为0
     *
     * @param value
     *
     * @return
     */
    public static boolean isEqualToZero(double value) {
        return Math.abs(value - 0.0) < 0.01 ? true : false;
    }

    /**
     * 经纬度是否为(0,0)点
     *
     * @return
     */
    public static boolean isZeroPoint(double latitude, double longitude) {
        return isEqualToZero(latitude) && isEqualToZero(longitude);
    }

    /**
     * 将字符串转为时间戳
     */
    public static long toTimeStamp(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA);
        Date date;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return date.getTime() / 1000;
    }

    /**
     * 获取时分秒
     *
     * @param timestamp 时间戳（单位：毫秒）
     *
     * @return
     */
    public static String getHMS(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            return sdf.format(new Timestamp(timestamp));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(timestamp);
    }

    /**
     * 获取年月日 时分秒
     *
     * @param timestamp 时间戳（单位：毫秒）
     *
     * @return
     */
    public static String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.format(new Timestamp(timestamp));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(timestamp);
    }

    public static String formatSecond(int second) {
        String format = "%1$,02d:%2$,02d:%3$,02d";
        Integer hours = second / (60 * 60);
        Integer minutes = second / 60 - hours * 60;
        Integer seconds = second - minutes * 60 - hours * 60 * 60;
        Object[] array = new Object[] {hours, minutes, seconds};
        return String.format(format, array);
    }

    public static final String formatDouble(double doubleValue) {
        return df.format(doubleValue);
    }

    /**
     * 计算x方向每次移动的距离
     */
    public static double getXMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
    }

    /**
     * 根据点和斜率算取截距
     */
    public static double getInterception(double slope, LatLng point) {
        return point.latitude - slope * point.longitude;
    }

    /**
     * 算斜率
     */
    public static double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        return (toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude);
    }

    /**
     * 根据两点算取图标转的角度
     */
    public static double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        return 180 * (radio / Math.PI) + deltAngle - 90;
    }

//    /**
//     * 保存当前定位点
//     */
//    public static void saveCurrentLocation(TrackApplication trackApp) {
//        SharedPreferences.Editor editor = trackApp.trackConf.edit();
//        StringBuffer locationInfo = new StringBuffer();
//        locationInfo.append(CurrentLocation.locTime);
//        locationInfo.append(";");
//        locationInfo.append(CurrentLocation.latitude);
//        locationInfo.append(";");
//        locationInfo.append(CurrentLocation.longitude);
//        editor.putString(BaiduTraceConstant.LAST_LOCATION, locationInfo.toString());
//        editor.apply();
//    }

    /**
     * 获取设备IMEI码
     *
     * @param context
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getImei(Context context) {
        String imei;
        try {
            imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception e) {
            imei = "myTrace";
        }
        return imei;
    }
}

