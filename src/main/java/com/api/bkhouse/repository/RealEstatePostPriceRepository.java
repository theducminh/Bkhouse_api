package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.api.bkhouse.entity.RealEstatePostPrice;

import java.util.List;
import java.util.UUID;

@Repository
public interface RealEstatePostPriceRepository extends JpaRepository<RealEstatePostPrice, Long> { 
    // Giữ nguyên Long vì khóa chính (id) của bảng lịch sử giá đang là kiểu bigserial (Long)

    /**
     * LẤY LỊCH SỬ BIẾN ĐỘNG GIÁ
     * Lấy toàn bộ các lần đổi giá của 1 bài đăng, sắp xếp theo thời gian mới nhất (DESC).
     * Phục vụ cho việc vẽ biểu đồ hoặc hiển thị nhãn "Chủ nhà vừa giảm giá".
     */
    List<RealEstatePostPrice> findByRealEstatePostIdOrderByCreateAtDesc(UUID realEstatePostId);

    /**
     * XÓA LỊCH SỬ GIÁ
     * Mặc dù dưới Database ta đã cài sẵn "ON DELETE CASCADE" (xóa bài thì tự mất giá),
     * nhưng vẫn nên khai báo sẵn hàm này trên JPA phòng trường hợp cần thao tác riêng.
     */
    @Transactional
    @Modifying
    void deleteByRealEstatePostId(UUID realEstatePostId);
}