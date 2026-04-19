package com.api.bkhouse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.constant.enumeric.ERepAgencyStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "real_estate_post_agency")
public class RealEstatePostAgency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "real_estate_post_id")
    @NotBlank
    @NotNull
    private UUID realEstatePostId;

    @Column(name = "agency_id")
    @NotNull
    @NotBlank
    private UUID agencyId;

    @Column(name = "status")
    @NotNull
    @NotBlank
    @Enumerated(EnumType.STRING)
    private ERepAgencyStatus status;

    @Column(name = "create_by", updatable = false)
    private UUID createBy;

    @Column(name = "create_at", updatable = false)
    private Instant createAt;

    @Column(name = "update_by")
    private UUID updateBy;

    @Column(name = "update_at")
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
