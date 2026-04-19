package com.api.bkhouse.payload.request;
import java.util.UUID;

public class EnablePFRequest {
    private UUID userId;
    private boolean enable;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
