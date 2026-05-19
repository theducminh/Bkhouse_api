package com.api.bkhouse.repository;

import com.api.bkhouse.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository // Đánh dấu đây là Bean Repository để Spring Boot quản lý
public interface HouseRepository extends JpaRepository<House, Long> { // 🚨 Đã đổi Long thành UUID

    /**
     * Tìm thông tin chi tiết của Nhà đất (House) dựa vào ID của bài đăng gốc (RealEstatePost)
     * Spring Data JPA sẽ tự động build câu SQL: SELECT * FROM house WHERE real_estate_post_id = ?
     */
    Optional<House> findByRealEstatePostId(UUID realEstatePostId);

    /**
     * Xóa thông tin chi tiết Nhà đất khi bài đăng gốc bị xóa
     */
    void deleteByRealEstatePostId(UUID realEstatePostId);
}