package com.api.bkhouse.entity;

import javax.persistence.*;

import com.api.bkhouse.entity.id.ForumPostLikeId;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "forum_post_like")
@IdClass(ForumPostLikeId.class)
public class ForumPostLike {
    @Id
    @Column(name = "forum_post_id")
    private UUID forumPostId;

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "create_by")
    private UUID createBy;

    @Column(name = "create_at")
    private Instant createAt;

    public UUID getForumPostId() {
        return forumPostId;
    }

    public void setForumPostId(UUID forumPostId) {
        this.forumPostId = forumPostId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getCreateBy() {
        return createBy;
    }

    public void setCreateBy(UUID createBy) {
        this.createBy = createBy;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }
}
