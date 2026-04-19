package com.api.bkhouse.payload.response;

import com.api.bkhouse.payload.dto.RealEstatePostAgencyDTO;
import com.api.bkhouse.payload.dto.post.RealEstatePostDTO;

public class REPAgencyResponse extends RealEstatePostAgencyDTO {
    private RealEstatePostDTO realEstatePostDTO;

    public RealEstatePostDTO getRealEstatePostDTO() {
        return realEstatePostDTO;
    }

    public void setRealEstatePostDTO(RealEstatePostDTO realEstatePostDTO) {
        this.realEstatePostDTO = realEstatePostDTO;
    }
}
