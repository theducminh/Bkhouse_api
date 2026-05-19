package com.api.bkhouse.entity;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "post_view")
public class PostView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "real_estate_post_id", nullable = false)
    private UUID realEstatePostId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "created_at")
    private Instant createdAt;

    // ================= GETTERS & SETTERS =================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getRealEstatePostId() { return realEstatePostId; }
    public void setRealEstatePostId(UUID realEstatePostId) { this.realEstatePostId = realEstatePostId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}