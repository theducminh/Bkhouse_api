package com.api.bkhouse.payload.request;

import java.util.List;
import java.util.UUID;

import com.api.bkhouse.payload.dto.DistrictDTO;

public class PriceFluctuationRequest {
    private UUID userId;
    private List<String> districts;
    private Long districtPrice;
    private boolean enable;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public List<String> getDistricts() {
        return districts;
    }

    public void setDistricts(List<String> districts) {
        this.districts = districts;
    }

    public Long getDistrictPrice() {
        return districtPrice;
    }

    public void setDistrictPrice(Long districtPrice) {
        this.districtPrice = districtPrice;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
