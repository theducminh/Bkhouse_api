package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController
@RequestMapping("/api/v1/user-device-token")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserDeviceTokenController {
    
    private final UserDeviceTokenService service;

    
    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserDeviceTokenController.class);

    public UserDeviceTokenController(UserDeviceTokenService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> insertUserDeviceToken(@RequestBody UserDeviceTokenDTO body) {
        try {
            body.setCreateAt(Util.getCurrentDateTime());
            body.setEnable(true);
            body.setLogout(false);
            body.setNotifyToken(body.getNotifyToken() != null ? body.getNotifyToken() : "");
            body.setDeviceInfo(body.getDeviceInfo() != null ? body.getDeviceInfo() : "");
            service.create(convertToEntity(body));

            return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi thêm thông tin người dùng nhận thông báo: ", e);
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
            if (userDeviceToken == null) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy thông tin thiết bị.", HttpStatus.NO_CONTENT));
            }
            return ResponseEntity.ok(new BaseResponse(modelMapper.map(userDeviceToken, UserDeviceTokenDTO.class), "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi lấy thông tin người dùng nhận thông báo: ", e);
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
            logger.error("Lỗi khi cập nhật User Device Token: ", e);
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy thông tin nhận thông báo trên thiết bị của người dùng." + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    private UserDeviceToken convertToEntity(UserDeviceTokenDTO dto) {
        return modelMapper.map(dto, UserDeviceToken.class);
    }
}
