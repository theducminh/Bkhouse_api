package com.api.bkhouse.controller;

import javax.validation.Valid;

// Thay đổi các import Swagger cũ thành OpenAPI 3
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.entity.UserDeviceToken;
import com.api.bkhouse.payload.dto.UserDTO;
import com.api.bkhouse.payload.dto.UserDeviceTokenDTO;
import com.api.bkhouse.payload.request.ForgotPassword;
import com.api.bkhouse.payload.request.LoginRequest;
import com.api.bkhouse.payload.request.TokenRefreshRequest;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.service.AuthService;
import com.api.bkhouse.service.UserDeviceTokenService;
import com.api.bkhouse.util.Util;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authorization", description = "Các API xác thực và tài khoản (không yêu cầu đăng nhập)")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    private UserDeviceTokenService userDeviceTokenService;

    @Operation(summary = "Đăng nhập hệ thống")
    @PostMapping("/signin")
    public ResponseEntity<BaseResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @Operation(summary = "Đăng ký tài khoản mới")
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse> registerUser(@Valid @RequestBody UserDTO signUpRequest) {
        return ResponseEntity.ok(authService.registerUser(signUpRequest));
    }

    @Operation(summary = "Làm mới Token (Refresh Token)")
    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {
        return ResponseEntity.ok(authService.refreshToken(tokenRefreshRequest));
    }

    @Operation(summary = "Kiểm tra Email đã tồn tại hay chưa")
    @GetMapping("/email-exist/{email}")
    public ResponseEntity<BaseResponse> emailExist(@PathVariable("email") String email) {
        return ResponseEntity.ok(authService.emailExist(email));
    }

    @Operation(summary = "Đăng xuất khỏi thiết bị")
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout(@RequestBody UserDeviceTokenDTO userDeviceTokenDTO) {
        try {
            UserDeviceToken userDeviceToken = userDeviceTokenService
                    .findByUserIdAndDeviceId(userDeviceTokenDTO.getUserId(), userDeviceTokenDTO.getDeviceInfo());
            if (userDeviceToken == null) {
                return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.OK));
            }
            userDeviceToken.setLogout(true);
            userDeviceToken.setUpdateBy(userDeviceTokenDTO.getUpdateBy());
            userDeviceToken.setUpdateAt(Util.getCurrentDateTime());
            userDeviceTokenService.update(userDeviceToken);
            return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null, "Đã xảy ra lỗi khi đăng xuất",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @Operation(summary = "Quên mật khẩu hoặc thay đổi mật khẩu")
    @PostMapping("/change-password")
    public ResponseEntity<BaseResponse> changePassword(@RequestBody ForgotPassword forgotPassword) {
        try {
            if (authService.changePassword(forgotPassword)) {
                return ResponseEntity.ok(new BaseResponse(null, "Đổi mật khẩu thành công",
                        HttpStatus.OK));
            } else {
                return ResponseEntity.ok(new BaseResponse(null, "Đã xảy ra lỗi khi đổi mật khẩu",
                        HttpStatus.INTERNAL_SERVER_ERROR));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi đổi mật khẩu " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
