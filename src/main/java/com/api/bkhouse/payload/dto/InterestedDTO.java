package com.api.bkhouse.payload.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public class InterestedDTO {
    private Long id;
    private UUID userId;
    private String deviceInfo;
    @NotNull
    @NotBlank
    private UUID realEstatePostId;
    private UUID createBy;
    private Instant createAt;
    private UUID updateBy;
    private Instant updateAt;

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

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public UUID getRealEstatePostId() {
        return realEstatePostId;
    }

    public void setRealEstatePostId(UUID realEstatePostId) {
        this.realEstatePostId = realEstatePostId;
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
