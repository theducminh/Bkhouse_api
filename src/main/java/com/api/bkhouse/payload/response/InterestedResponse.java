package com.api.bkhouse.payload.response;

import com.api.bkhouse.payload.dto.InterestedDTO;
import com.api.bkhouse.payload.dto.post.RealEstatePostDTO;

public class InterestedResponse extends InterestedDTO {
    private RealEstatePostDTO realEstatePost;

    public RealEstatePostDTO getRealEstatePost() {
        return realEstatePost;
    }

    public void setRealEstatePost(RealEstatePostDTO realEstatePost) {
        this.realEstatePost = realEstatePost;
    }
}
