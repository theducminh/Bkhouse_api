package com.api.bkhouse.entity;

import com.api.bkhouse.constant.enumeric.EType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "price_fluctuation")
public class PriceFluctuation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    @NotNull // Chỉ dùng NotNull cho UUID, KHÔNG dùng NotBlank
    private UUID userId;

    @Column(name = "province_code", length = 20)
    private String provinceCode;

    @Column(name = "district_code", length = 20, nullable = false)
    @NotNull
    @NotBlank // NotBlank hợp lệ vì đây là kiểu String
    private String districtCode;


    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", length = 50, nullable = false)
    @NotNull
    private EType propertyType;

    // Đổi tên cột thành is_enabled theo đúng script SQL của Supabase
    @Column(name = "is_enabled", nullable = false) 
    private boolean enable = true;

    @Column(name = "created_by") // Khớp với created_by trong DB
    private UUID createBy;

    @Column(name = "created_at") // Khớp với created_at trong DB
    private Instant createAt;

    @Column(name = "updated_by") // Khớp với updated_by trong DB
    private UUID updateBy;

    @Column(name = "updated_at") // Khớp với updated_at trong DB
    private Instant updateAt;

    // ==========================================
    // GETTERS & SETTERS
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public UUID getCreateBy() {
        return createBy;
    }

    public void setCreateBy(UUID createBy) {
        this.createBy = createBy;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    public UUID getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(UUID updateBy) {
        this.updateBy = updateBy;
    }

    public Instant getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Instant updateAt) {
        this.updateAt = updateAt;
    }
}