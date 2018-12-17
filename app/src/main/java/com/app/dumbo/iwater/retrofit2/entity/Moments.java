package com.app.dumbo.iwater.retrofit2.entity;

import java.util.Date;

public class Moments {
    private Integer momentId;

    private Integer userId;

    private String nickName;

    private String avatarUrl;

    private Date recordTime;

    private String description;

    private String picturesUrl;

    private Double latBd09ll;

    private Double lngBd09ll;

    private String address;

    public Integer getMomentId() {
        return momentId;
    }

    public void setMomentId(Integer momentId) {
        this.momentId = momentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl == null ? null : avatarUrl.trim();
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getPicturesUrl() {
        return picturesUrl;
    }

    public void setPicturesUrl(String picturesUrl) {
        this.picturesUrl = picturesUrl == null ? null : picturesUrl.trim();
    }

    public Double getLatBd09ll() {
        return latBd09ll;
    }

    public void setLatBd09ll(Double latBd09ll) {
        this.latBd09ll = latBd09ll;
    }

    public Double getLngBd09ll() {
        return lngBd09ll;
    }

    public void setLngBd09ll(Double lngBd09ll) {
        this.lngBd09ll = lngBd09ll;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }
}