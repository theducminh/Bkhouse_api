package com.api.bkhouse.repository;

import com.api.bkhouse.constant.enumeric.EType; // 🚨 Bổ sung Import Enum
import com.api.bkhouse.entity.PriceFluctuation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface PriceFluctuationRepository extends JpaRepository<PriceFluctuation, Long> {
    
    List<PriceFluctuation> findByUserId(UUID userId);

    // 🚨 BẮT BUỘC có 2 annotation này để xóa an toàn trong JPA
    @Transactional
    @Modifying
    void deleteByUserId(UUID userId);

    /**
     * HÀM CỐT LÕI CHO LUỒNG BẮN NOTIFICATION "GIÁ HỜI"
     * Tìm những người đang BẬT thông báo (isEnabled = true) khớp với Loại nhà và Địa chỉ.
     * Logic thông minh: Lấy cả những người theo dõi đích danh Phường đó, 
     */
    @Query("SELECT p FROM PriceFluctuation p " +
           "WHERE p.enable = true " +
           "AND p.propertyType = :propertyType " +
           "AND p.districtCode = :districtCode ")
    List<PriceFluctuation> findSubscribersToNotify(
            @Param("districtCode") String districtCode,
            @Param("propertyType") EType propertyType); // 🚨 Đã sửa String thành EType
}