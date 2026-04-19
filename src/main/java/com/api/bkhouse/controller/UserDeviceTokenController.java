package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.entity.UserDeviceToken;
import com.api.bkhouse.payload.dto.UserDeviceTokenDTO;
import com.api.bkhouse.payload.request.UserDeviceTokenRequest;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.service.UserDeviceTokenService;
import com.api.bkhouse.util.Util;

//import java.time.Instant;

@RestController
@RequestMapping("/api/v1/user-device-token")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserDeviceTokenController {
    @Autowired
    private UserDeviceTokenService service;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> insertUserDeviceToken(@RequestBody UserDeviceTokenDTO body) {
        try {
            body.setCreateAt(Util.getCurrentDateTime());
            service.create(convertToEntity(body));
            return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi thêm thông tin người dùng nhận thông báo. "
                            + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/findByUserIdAndDeviceInfo")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> getUserDeviceToken(@RequestBody UserDeviceTokenRequest request) {
        try {
            UserDeviceToken userDeviceToken = service.findByUserIdAndDeviceInfo(request.getUserId(), request.getDeviceInfo());
            return ResponseEntity.ok(new BaseResponse(modelMapper.map(userDeviceToken, UserDeviceTokenDTO.class), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy thông tin nhận thông báo trên thiết bị của người dùng." + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @PutMapping
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> updateUserDeviceToken(@RequestBody UserDeviceTokenDTO body) {
        try {
            body.setUpdateAt(Util.getCurrentDateTime());
            service.update(modelMapper.map(body, UserDeviceToken.class));
            return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy thông tin nhận thông báo trên thiết bị của người dùng." + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    private UserDeviceToken convertToEntity(UserDeviceTokenDTO dto) {
        return modelMapper.map(dto, UserDeviceToken.class);
    }
}
