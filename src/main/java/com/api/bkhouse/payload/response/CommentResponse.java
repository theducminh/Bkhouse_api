package com.api.bkhouse.payload.response;

import com.api.bkhouse.payload.dto.PostCommentDTO;

public class CommentResponse extends PostCommentDTO {
    private String fullName;
    private String avatarUrl;

    public CommentResponse() {
        super();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
