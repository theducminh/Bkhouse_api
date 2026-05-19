package com.api.bkhouse.repository;

import com.api.bkhouse.constant.enumeric.EType;
import com.api.bkhouse.entity.AreaPriceBenchmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AreaPriceBenchmarkRepository extends JpaRepository<AreaPriceBenchmark, Long> {

    /**
     * Lấy ra bảng giá chuẩn của một Phường cụ thể cho một Loại BĐS cụ thể.
     * Dùng để check xem bài đăng mới có phải là "Giá Hời" hay không.
     * @param propertyType Loại BĐS (APARTMENT, HOUSE, PLOT)
     * @return Bảng giá benchmark (nếu đã được hệ thống AI tổng hợp)
     */
    Optional<AreaPriceBenchmark> findByProvinceCodeAndDistrictCodeAndPropertyType(String provinceCode, String districtCode, EType propertyType);
    /**
     * (Tùy chọn) Bác có thể viết thêm hàm lấy theo Quận (District) 
     * phòng trường hợp Phường đó quá mới, hệ thống AI chưa cào đủ data để tính giá Phường.
     */
    Optional<AreaPriceBenchmark> findByDistrictCodeAndPropertyType(String districtCode, EType propertyType);
}