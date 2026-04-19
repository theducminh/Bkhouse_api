package com.api.bkhouse.entity.id;

import java.io.Serializable;
import java.util.Objects;

public class ForumPostLikeId implements Serializable {
    private String forumPostId;
    private String userId;

    public ForumPostLikeId() {
    }

    public ForumPostLikeId(String forumPostId, String userId) {
        this.forumPostId = forumPostId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForumPostLikeId that = (ForumPostLikeId) o;
        return Objects.equals(forumPostId, that.forumPostId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(forumPostId, userId);
    }
}
