package com.api.bkhouse.entity;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "special_accounts")
public class SpecialAccount {
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "monthly_charge")
    private int monthlyCharge;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public int getMonthlyCharge() {
        return monthlyCharge;
    }

    public void setMonthlyCharge(int monthlyCharge) {
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
