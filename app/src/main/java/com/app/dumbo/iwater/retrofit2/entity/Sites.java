package com.app.dumbo.iwater.retrofit2.entity;

public class Sites {
    private String siteId;

    private Double latWgs84;

    private String latType;

    private Double lngWgs84;

    private String lngType;

    private String workState;

    private String description;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId == null ? null : siteId.trim();
    }

    public Double getLatWgs84() {
        return latWgs84;
    }

    public void setLatWgs84(Double latWgs84) {
        this.latWgs84 = latWgs84;
    }

    public String getLatType() {
        return latType;
    }

    public void setLatType(String latType) {
        this.latType = latType == null ? null : latType.trim();
    }

    public Double getLngWgs84() {
        return lngWgs84;
    }

    public void setLngWgs84(Double lngWgs84) {
        this.lngWgs84 = lngWgs84;
    }

    public String getLngType() {
        return lngType;
    }

    public void setLngType(String lngType) {
        this.lngType = lngType == null ? null : lngType.trim();
    }

    public String getWorkState() {
        return workState;
    }

    public void setWorkState(String workState) {
        this.workState = workState == null ? null : workState.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}