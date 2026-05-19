package com.api.bkhouse.payload.request;

import java.util.List;
import java.util.UUID;

import com.api.bkhouse.constant.enumeric.EType; // 🚨 Nhớ import Enum EType

public class PriceFluctuationRequest {
    private UUID userId;
    
    // 🚨 Thêm 3 trường cấu hình mới
    private String provinceCode;
    private EType propertyType; 
    
    private List<String> districts;
    
    // 🚨 Đã xóa districtPrice
    
    private boolean enable;

    // === GETTERS & SETTERS ===

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    

    public EType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(EType propertyType) {
        this.propertyType = propertyType;
    }

    public List<String> getDistricts() {
        return districts;
    }

    public void setDistricts(List<String> districts) {
        this.districts = districts;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}