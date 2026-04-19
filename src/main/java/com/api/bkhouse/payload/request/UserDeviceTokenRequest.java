package com.api.bkhouse.payload.request;
import java.util.UUID;

public class UserDeviceTokenRequest {
    private UUID userId;
    private String deviceInfo;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
