package com.api.bkhouse.repository;

import com.api.bkhouse.entity.Plot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlotRepository extends JpaRepository<Plot, Long> { // Giữ nguyên Long theo ý bác
    
    // Tìm chi tiết Đất nền theo ID bài đăng gốc
    Optional<Plot> findByRealEstatePostId(UUID realEstatePostId);

    // 🚨 BẮT BUỘC: Thêm 2 annotation này để Spring Boot cho phép chạy lệnh XÓA (DELETE/UPDATE)
    @Transactional
    @Modifying
    void deleteByRealEstatePostId(UUID realEstatePostId);
}