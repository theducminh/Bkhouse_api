package com.api.bkhouse.entity;

import com.api.bkhouse.constant.enumeric.EType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "area_price_benchmark")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AreaPriceBenchmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === MÃ HÀNH CHÍNH ===
    @Column(name = "province_code", length = 20, nullable = false)
    private String provinceCode;

    @Column(name = "district_code", length = 20, nullable = false)
    private String districtCode;

    // === LOẠI BẤT ĐỘNG SẢN ===
    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", length = 50, nullable = false)
    private EType propertyType; // Đồng bộ với Enum EType (APARTMENT, HOUSE, PLOT) của bác

    // === CÁC CHỈ SỐ ĐỊNH GIÁ (Do SQL tự động tính) ===
    @Column(name = "avg_price_per_m2")
    private Double avgPricePerM2;

    @Column(name = "min_price")
    private Double minPrice;

    @Column(name = "max_price")
    private Double maxPrice;

    @Column(name = "sample_count")
    private Integer sampleCount;

    // === THỜI GIAN CẬP NHẬT ===
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "is_sell")
    private Boolean isSell;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getDistrictCode() {
        return districtCode;
    }


    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }


    

    public EType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(EType propertyType) {
        this.propertyType = propertyType;
    }


    public Double getAvgPricePerM2() {
        return avgPricePerM2;
    }

    public void setAvgPricePerM2(Double avgPricePerM2) {
        this.avgPricePerM2 = avgPricePerM2;
    }


    public Double getMinPrice() {
        return minPrice;
    }


    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }


    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }
    public Integer getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(Integer sampleCount) {
        this.sampleCount = sampleCount;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsSell() {
        return isSell;
    }

    public void setIsSell(Boolean isSell) {
        this.isSell = isSell;
    }
}