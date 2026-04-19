package com.api.bkhouse.payload.dto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.constant.enumeric.EPostType;

public class PostMediaDTO {
    @NotNull
    @NotBlank
    private String id;

    @NotNull
    @NotBlank
    private String mediaType;

    @NotNull
    @NotBlank
    private String postId;

    @NotNull
    @NotBlank
    @Enumerated(EnumType.STRING)
    private EPostType postType;

    @NotNull
    @NotBlank
    private String name;

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

    public EPostType getPostType() {
        return postType;
    }

    public void setPostType(EPostType postType) {
        this.postType = postType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
