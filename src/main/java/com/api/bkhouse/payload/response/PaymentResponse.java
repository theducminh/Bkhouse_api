package com.api.bkhouse.payload.response;

import java.time.Instant;

public class PaymentResponse {
    private String content;
    private Integer amount;
    private Long accountBalance;
    private Instant createAt;

    public PaymentResponse(String content, Integer amount, Long accountBalance, Instant createAt) {
        this.content = content;
        this.amount = amount;
        this.accountBalance = accountBalance;
        this.createAt = createAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }
}
