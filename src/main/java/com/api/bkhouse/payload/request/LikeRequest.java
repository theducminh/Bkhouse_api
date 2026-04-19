package com.api.bkhouse.payload.request;
import java.util.UUID;

public class LikeRequest {
    private UUID postId;

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }
}
