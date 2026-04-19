package com.api.bkhouse.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "special_account")
public class SpecialAccount {
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "monthly_charge")
    private Integer monthlyCharge;

    @Column(name = "is_agency")
    private boolean agency;

    @Column(name = "last_paid")
    private Instant lastPaid;

    @Column(name = "notify_before")
    private Integer notifyBefore;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Integer getMonthlyCharge() {
        return monthlyCharge;
    }

    public void setMonthlyCharge(Integer monthlyCharge) {
        this.monthlyCharge = monthlyCharge;
    }

    public boolean isAgency() {
        return agency;
    }

    public void setAgency(boolean agency) {
        this.agency = agency;
    }

    public Instant getLastPaid() {
        return lastPaid;
    }

    public void setLastPaid(Instant lastPaid) {
        this.lastPaid = lastPaid;
    }

    public Integer getNotifyBefore() {
        return notifyBefore;
    }

    public void setNotifyBefore(Integer notifyBefore) {
        this.notifyBefore = notifyBefore;
    }
}
