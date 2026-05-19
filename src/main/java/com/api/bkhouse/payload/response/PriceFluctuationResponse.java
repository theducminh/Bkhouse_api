package com.api.bkhouse.payload.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.api.bkhouse.constant.enumeric.EType;
import com.api.bkhouse.payload.dto.DistrictDTO;
import com.api.bkhouse.payload.dto.UserDTO;

public class PriceFluctuationResponse {
    private UserDTO user;
    
    // 🚨 Thêm 3 trường cấu hình mới
    private String provinceCode;
    private EType propertyType;

    private List<DistrictDTO> districts;
    
    // 🚨 Đã xóa districtPrice
    
    private boolean enable;
    private UUID createBy;
    private UUID updateBy;
    private Instant createAt;
    private Instant updateAt;

    // === GETTERS & SETTERS ===

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
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

    public List<DistrictDTO> getDistricts() {
        return districts;
    }

    public void setDistricts(List<DistrictDTO> districts) {
        this.districts = districts;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public UUID getCreateBy() {
        return createBy;
    }

    public void setCreateBy(UUID createBy) {
        this.createBy = createBy;
    }

    public UUID getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(UUID updateBy) {
        this.updateBy = updateBy;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    public Instant getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Instant updateAt) {
        this.updateAt = updateAt;
    }
}