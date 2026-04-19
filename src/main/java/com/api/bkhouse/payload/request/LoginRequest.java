package com.api.bkhouse.payload.request;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginRequest {
    @NotBlank
    @Schema(example = "duc_hust_2004")
    private String username;

    @NotBlank
    @Schema(example = "123456")
    private String password;

    @NotBlank
    private String deviceInfo;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
