package com.api.bkhouse.payload.request;

import java.util.List;

import com.api.bkhouse.payload.dto.InfoPostDTO;

public class InfoPostRequest {
    private InfoPostDTO infoPost;
    private List<String> districtCodes;

    public InfoPostDTO getInfoPost() {
        return infoPost;
    }

    public void setInfoPost(InfoPostDTO infoPost) {
        this.infoPost = infoPost;
    }

    public List<String> getDistrictCodes() {
        return districtCodes;
    }

    public void setDistrictCodes(List<String> districtCodes) {
        this.districtCodes = districtCodes;
    }
}
