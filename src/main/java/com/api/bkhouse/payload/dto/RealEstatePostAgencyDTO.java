package com.api.bkhouse.payload.dto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.constant.enumeric.ERepAgencyStatus;

import java.time.Instant;
import java.util.UUID;

public class RealEstatePostAgencyDTO {
    @NotBlank
    @NotNull
    private Long id;
    @NotBlank
    @NotNull
    private UUID realEstatePostId;
    @NotBlank
    @NotNull
    private UUID agencyId;
    @NotBlank
    @NotNull
    @Enumerated(EnumType.STRING)
    private ERepAgencyStatus status;
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

    public UUID getRealEstatePostId() {
        return realEstatePostId;
    }

    public void setRealEstatePostId(UUID realEstatePostId) {
        this.realEstatePostId = realEstatePostId;
    }

    public UUID getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(UUID agencyId) {
        this.agencyId = agencyId;
    }

    public ERepAgencyStatus getStatus() {
        return status;
    }

    public void setStatus(ERepAgencyStatus status) {
        this.status = status;
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
