package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.bkhouse.entity.PostView;
import java.time.Instant;
import java.util.UUID;

@Repository
public interface PostViewRepository extends JpaRepository<PostView, Long> {

    // Đếm tổng số view của 1 bài viết
    long countByRealEstatePostId(UUID realEstatePostId);

    // KIỂM TRA CHỐNG SPAM: Check xem IP hoặc Device này đã view bài này sau thời điểm timeLimit chưa?
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM PostView p " +
           "WHERE p.realEstatePostId = :postId " +
           "AND (p.ipAddress = :ipAddress OR p.deviceId = :deviceId) " +
           "AND p.createdAt >= :timeLimit")
    boolean existsRecentView(@Param("postId") UUID postId, 
                             @Param("ipAddress") String ipAddress, 
                             @Param("deviceId") String deviceId, 
                             @Param("timeLimit") Instant timeLimit);
}