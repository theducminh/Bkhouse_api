package com.api.bkhouse.entity;

import org.springframework.stereotype.Controller;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Controller
public class SystemChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "message")
    @NotBlank
    @NotNull
    private String message;

    @Column(name = "is_send_by_admin")
    @NotNull
    @NotBlank
    private boolean isSendByAdmin;

    @Column(name = "send_at")
    @NotNull
    @NotBlank
    private Instant sendAt;

    @Column(name = "enable")
    @NotNull
    @NotBlank
    private boolean enable;

    @Column(name = "update_by")
    private String updateBy;

    @Column(name = "update_at")
    private Instant updateAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSendByAdmin() {
        return isSendByAdmin;
    }

    public void setSendByAdmin(boolean sendByAdmin) {
        isSendByAdmin = sendByAdmin;
    }

    public Instant getSendAt() {
        return sendAt;
    }

    public void setSendAt(Instant sendAt) {
        this.sendAt = sendAt;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Instant getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Instant updateAt) {
        this.updateAt = updateAt;
    }
}
