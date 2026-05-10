package com.api.bkhouse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.api.bkhouse.constant.enumeric.EChargeStatus;
import com.api.bkhouse.constant.enumeric.EChargeType;

import java.time.Instant;

@Entity
@Table(name = "charge") // Hoặc "charges" tùy tên bảng thực tế của bạn
public class Charge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id") // Khớp DB
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "charge_type") // Khớp DB
    @NotNull
    private EChargeType chargeType;

    @Column(name = "amount") // ĐÃ SỬA: DB là amount, không phải so_tien
    private Long soTien;

    @Column(name = "balance_after") // ĐÃ SỬA: DB là balance_after, không phải account_balance
    @NotNull
    private Long accountBalance;

    @Column(name = "status") // Khớp DB
    @NotNull
    @Enumerated(EnumType.STRING)
    private EChargeStatus status;

    @Column(name = "evidence_url") // ĐÃ SỬA: DB là evidence_url, không phải image_url
    private String imageUrl;

    @Column(name = "note") // ĐÃ THÊM: DB có cột note
    private String note;

    @Column(name = "created_at") // ĐÃ SỬA: DB là created_at (thêm "ed")
    private Instant createAt;

    @Column(name = "updated_at") // ĐÃ THÊM: DB có cột updated_at
    private Instant updatedAt;

    // --- BẠN TỰ GEN LẠI CÁC HÀM GETTER / SETTER Ở ĐÂY NHÉ ---
    // (Bao gồm cả get/set cho note và updatedAt mới thêm)

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSoTien() { return soTien; }
    public void setSoTien(Long soTien) { this.soTien = soTien; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Long getAccountBalance() { return accountBalance; }
    public void setAccountBalance(Long accountBalance) { this.accountBalance = accountBalance; }

    public EChargeType getChargeType() { return chargeType; }
    public void setChargeType(EChargeType chargeType) { this.chargeType = chargeType; }

    public Instant getCreateAt() { return createAt; }
    public void setCreateAt(Instant createAt) { this.createAt = createAt; }

    public EChargeStatus getStatus() { return status; }
    public void setStatus(EChargeStatus status) { this.status = status; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}