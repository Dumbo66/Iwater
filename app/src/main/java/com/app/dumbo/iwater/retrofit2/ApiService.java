package com.app.dumbo.iwater.retrofit2;

import com.app.dumbo.iwater.retrofit2.entity.reception.AirDataReception;
import com.app.dumbo.iwater.retrofit2.entity.reception.JwtReception;
import com.app.dumbo.iwater.retrofit2.entity.MobileData;
import com.app.dumbo.iwater.retrofit2.entity.reception.MobileDataReception;
import com.app.dumbo.iwater.retrofit2.entity.reception.MomentsReception;
import com.app.dumbo.iwater.retrofit2.entity.reception.Reception;
import com.app.dumbo.iwater.retrofit2.entity.Sites;
import com.app.dumbo.iwater.retrofit2.entity.reception.SitesReception;
import com.app.dumbo.iwater.retrofit2.entity.Users;
import com.app.dumbo.iwater.retrofit2.entity.reception.WaterDataReception;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 *  --定义Retrofit2接口--
 *
 * Created by dumbo on 2018/4/23
 **/

public interface ApiService {
    /******************************************登录注册*********************************************/
    @Multipart
    @POST("users")
    Call<ResponseBody> register(@PartMap Map<String,RequestBody> map, @Part MultipartBody.Part body);

    @GET("users")
    Call<Reception> selectUserByPhone(@Query("phone") String phone);

    @POST("ver_codes")
    Call<ResponseBody> loginByVerCode(@Body Users users);

    @POST("tokens")
    Call<ResponseBody> loginByPasw(@Body Users users);

    @POST("access_token")
    Call<JwtReception> postAccessJwt(@Query("accessJwt") String accessJwt);

    @POST("refresh_token")
    Call<JwtReception> postRefreshJwt(@Query("refreshJwt") String refreshJwt);

    /*****************************************监测点信息********************************************/
    @POST("sites")
    Call<Reception> postSite(@Body Sites sites);

    @DELETE("sites")
    Call<Reception> deleteSite(@Query("siteId") int siteId);

    @PUT("sites")
    Call<Reception> putSite(@Body Sites sites);

    @GET("sites")
    Call<SitesReception> getSite(@Query("siteId") int siteId);

    @GET("all_sites")
    Call<SitesReception> getAllSites();

    /****************************************水质监测数据*******************************************/
    @GET("all_latest_water_data")
    Call<WaterDataReception> getWaterData(@Query("count") int count);

    @GET("per_hour_water_data")
    Call<WaterDataReception> getPerHourWaterData(@Query("siteId") String siteId, @Query("date") String date);

    /****************************************空气质量监测数据*******************************************/
    @GET("all_latest_air_data")
    Call<AirDataReception> getAirData(@Query("count") int count);

    @GET("per_hour_air_data")
    Call<AirDataReception> getPerHourAirData(@Query("siteId") String siteId, @Query("date") String date);

    /****************************************移动监测数据*******************************************/
    @POST("mobile_data")
    Call<Reception> postMobileData(@Body MobileData mobileData);

    @DELETE("mobile_data")
    Call<Reception> deleteMobileData(@Query("date") String date);

    @PUT("mobile_data")
    Call<Reception> putMobileData(@Body MobileData mobileData);

    @GET("mobile_data")
    Call<MobileDataReception> getMobileData(@Query("date") String date);

    /******************************************图文上传*********************************************/
    @Multipart
    @POST("moments")
    Call<Reception> uploadPictures(@PartMap Map<String,RequestBody> map, @Part MultipartBody.Part[] body);

    @GET("moments")
    Call<MomentsReception> getMoments(@Query("pageNum") int pageNum,@Query("pageSize") int pageSize);

    @GET("regeo")
    Call<ResponseBody> getGaodeAddress(@Query("key") String key, @Query("location") String location);
}
