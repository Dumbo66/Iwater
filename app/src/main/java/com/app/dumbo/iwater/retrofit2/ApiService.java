package com.app.dumbo.iwater.retrofit2;

import android.database.Observable;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.retrofit2.entity.DataReception;
import com.app.dumbo.iwater.retrofit2.entity.JwtReception;
import com.app.dumbo.iwater.retrofit2.entity.MobileData;
import com.app.dumbo.iwater.retrofit2.entity.MobileDataReception;
import com.app.dumbo.iwater.retrofit2.entity.MomentsReception;
import com.app.dumbo.iwater.retrofit2.entity.Reception;
import com.app.dumbo.iwater.retrofit2.entity.Sites;
import com.app.dumbo.iwater.retrofit2.entity.SitesReception;
import com.app.dumbo.iwater.retrofit2.entity.Users;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
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
    @POST("users")
    Call<JwtReception> registerByPasw(@Body Users users);

    @GET("users")
    Call<Reception> selectUserByPhone(@Query("phone") String phone);

    @POST("tokens")
    Call<JwtReception> loginByPasw(@Body Users users);

    @POST("access_token")
    Call<JwtReception> postAccessJwt(@Query("accessJwt") String accessJwt);

    @POST("refresh_token")
    Call<JwtReception> postRefreshJwt(@Query("refreshJwt") String refreshJwt);

    /*****************************************监测点信息********************************************/
    @POST("sites")
    Call<Reception> postSite(@Body Sites sites);

    @DELETE("sites")
    Call<Reception> deleteSite(@Query("site") int site);

    @PUT("sites")
    Call<Reception> putSite(@Body Sites sites);

    @GET("sites")
    Call<SitesReception> getSite(@Query("site") int site);

    @GET("all_sites")
    Call<SitesReception> getAllSites();

    /****************************************定点监测数据*******************************************/
    @GET("all_latest_data")
    Call<DataReception> getMonitorData(@Query("count") int count);

    @GET("per_hour_data")
    Call<DataReception> getPerHourData(@Query("site") int site, @Query("date") String date);

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
    Call<Reception> uploadPictures(@PartMap Map<String,RequestBody> map,
                                   @Part MultipartBody.Part[] body);

    @GET("moments")
    Call<MomentsReception> getMoments(@Query("count") int count);

    @GET("regeo")
    Call<ResponseBody> getGaodeAddress(@Query("key") String key, @Query("location") String location);
}
