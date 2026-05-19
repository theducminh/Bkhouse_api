package com.api.bkhouse.payload.dto;

import javax.validation.constraints.NotNull;
import java.util.UUID;
public class PostViewDTO {
    @NotNull
    private UUID realEstatePostId;
    
    // Nếu khách vãng lai thì trường này sẽ rỗng
    private UUID userId;
    
    // Lấy thông tin thiết bị/trình duyệt từ Frontend gửi lên
    private String deviceId;

    public UUID getRealEstatePostId() { return realEstatePostId; }
    public void setRealEstatePostId(UUID realEstatePostId) { this.realEstatePostId = realEstatePostId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
}
