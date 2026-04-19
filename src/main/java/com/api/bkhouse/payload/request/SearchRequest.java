package com.api.bkhouse.payload.request;

import com.sun.source.doctree.SeeTree;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SearchRequest {
    private Boolean sell;
    private String type;
    private String keyword;
    private String provinceCode;
    private String[] districtCode;
    private String[] wardCode;
    private Double startPrice;
    private Double endPrice;
    private Double startArea;
    private Double endArea;
    private Integer[] noOfBedrooms;
    private String[] direction;
    private String userId;
    private String deviceInfo;

    @NotBlank
    @NotNull
    private Integer limit;

    @NotNull
    @NotBlank
    private Integer offset;

    public Boolean getSell() {
        return sell;
    }

    public void setSell(Boolean sell) {
        this.sell = sell;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public Double getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(Double startPrice) {
        this.startPrice = startPrice;
    }

    public Double getEndPrice() {
        return endPrice;
    }

    public void setEndPrice(Double endPrice) {
        this.endPrice = endPrice;
    }

    public Double getStartArea() {
        return startArea;
    }

    public void setStartArea(Double startArea) {
        this.startArea = startArea;
    }

    public Double getEndArea() {
        return endArea;
    }

    public void setEndArea(Double endArea) {
        this.endArea = endArea;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String[] getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String[] districtCode) {
        this.districtCode = districtCode;
    }

    public String[] getWardCode() {
        return wardCode;
    }

    public void setWardCode(String[] wardCode) {
        this.wardCode = wardCode;
    }

    public Integer[] getNoOfBedrooms() {
        return noOfBedrooms;
    }

    public void setNoOfBedrooms(Integer[] noOfBedrooms) {
        this.noOfBedrooms = noOfBedrooms;
    }

    public String[] getDirection() {
        return direction;
    }

    public void setDirection(String[] direction) {
        this.direction = direction;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
