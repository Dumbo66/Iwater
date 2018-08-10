package com.app.dumbo.iwater.constant;

/**
 * Created by Dumbo on 2018/6/17
 **/

public class ResponseCode {
    public static final int OK=6666;                             //统一的成功请求
    public static final int BAD_REQUEST=4444;                    //统一的无效请求

    /**用户登录注册相关*/
    public static final int UNREGISTERED=2001;                   //账号未注册
    public static final int REGISTERED=2002;                     //账号已注册
    public static final int PHONE_OR_PASW_ERROR=2003;            //账号或密码错误
    public static final int PASW_IS_NULL=2004;                   //账号未设置密码
    public static final int TOKEN_IS_INVALID=2005;               //令牌无效
    public static final int ACCESS_TOKEN_IS_EXPIRED=2006;        //access_token已过期,重新获取token
    public static final int REFRESH_TOKEN_IS_EXPIRED=2007;       //refresh_token已过期,重新登录

    /**业务相关*/
    public static final int SITE_IS_NULL=3001;                   //站点号不存在


}
