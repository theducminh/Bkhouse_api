package com.api.bkhouse.payload.request;

import java.util.List;

import com.api.bkhouse.payload.dto.PostMediaDTO;

public class ListImageUpload {
    private List<PostMediaDTO> images;

    public List<PostMediaDTO> getImages() {
        return images;
    }

    public void setImages(List<PostMediaDTO> images) {
        this.images = images;
    }
}
