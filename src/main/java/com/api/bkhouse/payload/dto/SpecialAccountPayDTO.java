package com.api.bkhouse.payload.dto;

import java.time.Instant;

public class SpecialAccountPayDTO {
    private Long id;
    private UserDTO user;
    private Integer amount;
    private Long accountBalance;
    private String content;
    private boolean monthlyPay;
    private String createBy;
    private Instant createAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Long accountBalance) {
        this.accountBalance = accountBalance;
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
}
