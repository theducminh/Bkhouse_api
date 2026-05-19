package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.api.bkhouse.entity.PostMedia;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostMediaRepository extends JpaRepository<PostMedia, UUID> {
    List<PostMedia> findByPostId(UUID postId);
    List<PostMedia> findByPostIdIn(List<UUID> postIds);

    @Transactional
    @Modifying
    void deleteByPostId(UUID postId);

    @Query(value = "select cast(id as varchar) from post_media where post_id = :postId limit 1", nativeQuery = true)
    Optional<String> getOneImageOfPost(@Param("postId") UUID postId);

    @Query(value = "delete from post_media where id = :id", nativeQuery = true)
    Optional<UUID> deleteById1(@Param("id") UUID id);
}
