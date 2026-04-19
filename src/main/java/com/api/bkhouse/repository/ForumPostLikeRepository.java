package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.bkhouse.entity.ForumPostLike;
import com.api.bkhouse.entity.id.ForumPostLikeId;
import java.util.UUID;

public interface ForumPostLikeRepository extends JpaRepository<ForumPostLike, ForumPostLikeId> {
    boolean existsByForumPostIdAndUserId(UUID forumPostId, UUID userId);

    void deleteByForumPostIdAndUserId(UUID forumPostId, UUID userId);
    long countByForumPostId(UUID postId);
}
