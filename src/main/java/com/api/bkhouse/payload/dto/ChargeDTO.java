package com.api.bkhouse.payload.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.constant.enumeric.EChargeStatus;
import com.api.bkhouse.constant.enumeric.EChargeType;

import java.time.Instant;

public class ChargeDTO {
    private Long id;

    private Long soTien;

    private UserDTO user;

    @NotNull
    @NotBlank
    private Long accountBalance;

    @NotNull
    @NotBlank
    private EChargeType chargeType;

    private String createBy;

    private Instant createAt;

    @NotNull
    @NotBlank
    private EChargeStatus status;

    private String imageUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSoTien() {
        return soTien;
    }

    public void setSoTien(Long soTien) {
        this.soTien = soTien;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Long getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Long accountBalance) {
        this.accountBalance = accountBalance;
    }

    public EChargeType getChargeType() {
        return chargeType;
    }

    public void setChargeType(EChargeType chargeType) {
        this.chargeType = chargeType;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    public EChargeStatus getStatus() {
        return status;
    }

    public void setStatus(EChargeStatus status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
