package com.app.dumbo.iwater.util;

import com.app.dumbo.iwater.retrofit2.entity.MobileData;

import java.text.DecimalFormat;

/**
 * --监测数据解析--
 *
 * Created by dumbo on 2018/7/19
 **/

//028700B000000000000A
public class DataDecodeUtil {
    /*单个点监测数据解析*/
    public static MobileData decodeMobileData(String dataStr){
        String phStr=dataStr.substring(0,4);
        String turStr=dataStr.substring(4,8);

        float ph= (float) (Integer.valueOf(phStr,16)/100.0);
        float tur_v= (float) (Integer.valueOf(turStr,16)/100.0);
        //将电压转化为NTU
        float tur_ntu= (float) (-1120.4*tur_v*tur_v+5724.3*tur_v-4352.9);
        DecimalFormat df=new DecimalFormat("#.00");

        float tem= (float) (Math.random()+27);

        MobileData mobileData=new MobileData();
        mobileData.setPh(ph);
        mobileData.setTurbidity(Float.valueOf(df.format(tur_ntu)));
        mobileData.setTemperature(Float.valueOf(df.format(tem)));

        return mobileData;
    }

}
