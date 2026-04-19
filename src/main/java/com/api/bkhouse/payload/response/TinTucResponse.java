package com.api.bkhouse.payload.response;

import java.util.List;

import com.api.bkhouse.payload.dto.InfoTypeDTO;

public class TinTucResponse {
    private InfoTypeDTO infoType;
    private List<InfoPostResponse> infoPosts;

    public InfoTypeDTO getInfoType() {
        return infoType;
    }

    public void setInfoType(InfoTypeDTO infoType) {
        this.infoType = infoType;
    }

    public List<InfoPostResponse> getInfoPosts() {
        return infoPosts;
    }

    public void setInfoPosts(List<InfoPostResponse> infoPosts) {
        this.infoPosts = infoPosts;
    }
}
