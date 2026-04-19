package com.api.bkhouse.payload.request;

import com.api.bkhouse.constant.enumeric.EStatus;
import java.util.UUID;

public class UpdatePostStatusRequest {
    private UUID postId;
    private EStatus status;

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }
}
