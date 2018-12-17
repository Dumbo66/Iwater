package com.app.dumbo.iwater.retrofit2.entity.reception;

/**
 * Created by dumbo on 2018/5/27
 **/

public class JwtReception {
    private int code;
    private String message;
    private JwtToken data;

    public class JwtToken {
        private String accessJwt;
        private String refreshJwt;

        public String getAccessJwt() {
            return accessJwt;
        }

        public void setAccessJwt(String accessJwt) {
            this.accessJwt = accessJwt;
        }

        public String getRefreshJwt() {
            return refreshJwt;
        }

        public void setRefreshJwt(String refreshJwt) {
            this.refreshJwt = refreshJwt;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JwtToken getData() {
        return data;
    }

    public void setData(JwtToken data) {
        this.data = data;
    }
}
