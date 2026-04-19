package com.api.bkhouse.payload.response;

import java.util.List;

import com.api.bkhouse.payload.dto.DistrictDTO;
import com.api.bkhouse.payload.dto.SpecialAccountDTO;

public class AgencyInfoResponse {
    private SpecialAccountDTO specialAccount;
    private List<DistrictDTO> districts;

    public SpecialAccountDTO getSpecialAccount() {
        return specialAccount;
    }

    public void setSpecialAccount(SpecialAccountDTO specialAccount) {
        this.specialAccount = specialAccount;
    }

    public List<DistrictDTO> getDistricts() {
        return districts;
    }

    public void setDistricts(List<DistrictDTO> districts) {
        this.districts = districts;
    }
}
