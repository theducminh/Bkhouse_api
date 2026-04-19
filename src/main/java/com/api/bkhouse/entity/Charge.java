package com.api.bkhouse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.constant.enumeric.EChargeStatus;
import com.api.bkhouse.constant.enumeric.EChargeType;

import java.time.Instant;

@Entity
@Table(name = "charge")
public class Charge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "so_tien")
    private Long soTien;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "account_balance")
    @NotNull
    @NotBlank
    private Long accountBalance;

    @Enumerated(EnumType.STRING)
    @NotNull
    @NotBlank
    @Column(name = "charge_type")
    private EChargeType chargeType;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "create_at")
    private Instant createAt;

    @Column(name = "status")
    @NotNull
    @NotBlank
    @Enumerated(EnumType.STRING)
    private EChargeStatus status;

    @Column(name = "image_url")
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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
