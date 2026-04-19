package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.api.bkhouse.entity.PostMedia;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostMediaRepository extends JpaRepository<PostMedia, String> {
    List<PostMedia> findByPostId(UUID postId);

    @Modifying
    void deleteByPostId(UUID postId);

    @Query(value = "select id from post_media where post_id = :postId limit 1", nativeQuery = true)
    Optional<String> getOneImageOfPost(UUID postId);

    @Query(value = "delete from post_media where id = :id", nativeQuery = true)
    Optional<String> deleteById1(UUID id);
}
