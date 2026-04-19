package com.api.bkhouse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.constant.EmailVerifyType;
import com.api.bkhouse.entity.User;
import com.api.bkhouse.payload.request.EmailVerify;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.service.EmailService;
import com.api.bkhouse.util.Util;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/no-auth/send-email")
public class EmailController {
    @Autowired
    private EmailService service;

    @PostMapping("")
    public ResponseEntity<BaseResponse> sendEmail(@RequestBody EmailVerify email) {
        try {
            if (email.getType().equalsIgnoreCase(EmailVerifyType.FORGOT_PASSWORD)) {
                User user = service.findByEmail(email.getEmail());
                if (user == null) {
                    return ResponseEntity.ok(new BaseResponse(null,
                            "Email chưa được đăng ký trên hệ thống",
                            HttpStatus.NO_CONTENT));
                } else {
                    if (user.getUsername().equals(user.getEmail() + "_user_bkland")) {
                        return ResponseEntity.ok(
                                new BaseResponse(null,
                                        "Email được sử dụng để đăng nhập thông qua google nên không thể đổi mật khẩu",
                                        HttpStatus.NOT_ACCEPTABLE)
                        );
                    } else {
                        String code = Util.getRandomNumberString();
                        service.sendEmail(email.getEmail(), email.getTitle(),
                                "Mã xác thực của bạn là: " + code);
                        return ResponseEntity.ok(new BaseResponse(code, "Đã gửi mã xác thực thành công.",
                                HttpStatus.OK));
                    }
                }
            } else {
                return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.OK));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi gửi email " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
