package com.api.bkhouse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "special_account_pay")
public class SpecialAccountPay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @NotBlank
    @Column(name = "amount")
    private Integer amount;

    @NotNull
    @NotBlank
    @Column(name = "account_balance")
    private Long accountBalance;

    @NotNull
    @NotBlank
    @Column(name = "content")
    private String content;

    @Column(name = "is_monthly_pay")
    @NotNull
    @NotBlank
    private boolean monthlyPay;

    @NotNull
    @NotBlank
    @Column(name = "create_by")
    private UUID createBy;

    @NotNull
    @NotBlank
    @Column(name = "create_at")
    private Instant createAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isMonthlyPay() {
        return monthlyPay;
    }

    public void setMonthlyPay(boolean monthlyPay) {
        this.monthlyPay = monthlyPay;
    }

    public Long getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Long accountBalance) {
        this.accountBalance = accountBalance;
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
}
