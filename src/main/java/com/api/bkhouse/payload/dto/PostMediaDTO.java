package com.api.bkhouse.payload.dto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.constant.enumeric.EPostType;

public class PostMediaDTO {
   
    private String id;

    @NotNull
    private String mediaType;

    @NotBlank
    private String postId;

    @NotNull
    private String mediaUrl;

    @NotBlank
    private boolean isThumbnail;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }
    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public boolean isThumbnail() {
        return isThumbnail;
    }
    public void setThumbnail(boolean isThumbnail) {
        this.isThumbnail = isThumbnail;
    }
}
