package com.api.bkhouse.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.payload.dto.DistrictDTO;

import java.util.List;
import java.util.UUID;

public class AgencyRegisterRequest {
    @NotNull
    private UUID userId;
    @NotEmpty
    private List<DistrictDTO> districts;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public List<DistrictDTO> getDistricts() {
        return districts;
    }

    public void setDistricts(List<DistrictDTO> districts) {
        this.districts = districts;
    }
}
