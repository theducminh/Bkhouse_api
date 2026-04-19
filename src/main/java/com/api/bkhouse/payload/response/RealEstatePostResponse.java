package com.api.bkhouse.payload.response;

import java.util.List;

import com.api.bkhouse.payload.dto.PostMediaDTO;
import com.api.bkhouse.payload.dto.post.BasePost;

public class RealEstatePostResponse {
    private BasePost basePost;
    private List<PostMediaDTO> images;

    public RealEstatePostResponse(BasePost basePost, List<PostMediaDTO> postMediaDTOS) {
        this.basePost = basePost;
        this.images = postMediaDTOS;
    }

    public BasePost getBasePost() {
        return basePost;
    }

    public void setBasePost(BasePost basePost) {
        this.basePost = basePost;
    }

    public List<PostMediaDTO> getPostMediaDTOS() {
        return images;
    }

    public void setPostMediaDTOS(List<PostMediaDTO> postMediaDTOS) {
        this.images = postMediaDTOS;
    }
}
