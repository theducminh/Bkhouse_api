package com.api.bkhouse.controller;

import com.api.bkhouse.config.annotation.CurrentUser;
import com.api.bkhouse.entity.User;
import com.api.bkhouse.payload.dto.*;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.payload.response.UserInfoResponse;
import com.api.bkhouse.security.services.UserDetailsImpl;
import com.api.bkhouse.service.DistrictService;
import com.api.bkhouse.service.ProvinceService;
import com.api.bkhouse.service.UserService;
import com.api.bkhouse.service.WardService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    @Autowired
    private UserService service;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private DistrictService districtService;

    @Autowired
    private WardService wardService;

    @GetMapping("/api/v1/user/{userId}")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> getUserById(@PathVariable("userId") UUID userId) {
        try {
            User user = service.findById(userId);
            if (user == null) {
                return ResponseEntity.ok(new BaseResponse(null,
                        "Không tìm thấy thông tin người dùng.",
                        HttpStatus.NO_CONTENT));
            } else {
                return ResponseEntity.ok(new BaseResponse(convertToDTO(user),
                        "", HttpStatus.OK));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy thông tin người dùng " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/v1/user/info/{userId}")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> getUserInfoById(@PathVariable("userId") UUID userId) {
        try {
            User user = service.findById(userId);
            if (user == null) {
                return ResponseEntity.ok(new BaseResponse(null,
                        "Không tìm thấy thông tin người dùng.",
                        HttpStatus.NO_CONTENT));
            } else {
                DistrictDTO districtDTO = modelMapper.map(districtService.findByCode(user.getDistrictCode()), DistrictDTO.class);
                ProvinceDTO provinceDTO = modelMapper.map(provinceService.findByCode(user.getProvinceCode()), ProvinceDTO.class);
                WardDTO wardDTO = modelMapper.map(wardService.findByCode(user.getWardCode()), WardDTO.class);

                UserInfoResponse userInfoResponse = new UserInfoResponse();
                userInfoResponse.setAddress(user.getAddress());
                userInfoResponse.setAccountBalance(user.getAccountBalance());
                userInfoResponse.setAvatarUrl(user.getAvatarUrl());
                userInfoResponse.setEmail(user.getEmail());
                userInfoResponse.setCreatedAt(user.getCreatedAt());
                userInfoResponse.setDistrict(districtDTO);
                userInfoResponse.setProvince(provinceDTO);
                userInfoResponse.setWard(wardDTO);
                userInfoResponse.setEnable(user.isEnabled());
                userInfoResponse.setGender(user.getGender());
                userInfoResponse.setId(user.getId());
                userInfoResponse.setCreatedBy(user.getCreatedBy());
                userInfoResponse.setIdentification(user.getIdentification());
                userInfoResponse.setEmail(user.getEmail());
                userInfoResponse.setDateOfBirth(user.getDateOfBirth());
                userInfoResponse.setFirstName(user.getFirstName());
                userInfoResponse.setMiddleName(user.getMiddleName());
                userInfoResponse.setLastName(user.getLastName());
                userInfoResponse.setUpdatedAt(user.getUpdatedAt());
                userInfoResponse.setUpdatedBy(userInfoResponse.getUpdatedBy());
                userInfoResponse.setRoles(user.getRoles().stream().map(e -> modelMapper.map(e, RoleDTO.class)).collect(Collectors.toSet()));
                userInfoResponse.setPassword(user.getPassword());
                userInfoResponse.setPhoneNumber(user.getPhoneNumber());
                userInfoResponse.setUsername(user.getUsername());
                return ResponseEntity.ok(new BaseResponse(userInfoResponse,
                        "", HttpStatus.OK));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy thông tin người dùng " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/api/v1/user")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> updateUserInfo(@RequestBody UserDTO userDTO) {
        try {
            service.updateUserInfo(modelMapper.map(userDTO, User.class));
            return ResponseEntity.ok(new BaseResponse(null, "Cập nhật thông tin người dùng thành công.", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi cập nhật thông tin người dùng " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/v1/user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getAllUsers() {
        try {
            List<User> users = service.findAll();
            if (users.isEmpty()) {
                return ResponseEntity.ok(new BaseResponse(null,
                        "Không có người dùng nào được tìm thấy.",
                        HttpStatus.NO_CONTENT));
            }
            return ResponseEntity.ok(new BaseResponse(
                    users.stream().map(e -> modelMapper.map(e, UserDTO.class)).collect(Collectors.toList()),
                    "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy danh sách người dùng " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/api/v1/user/lock/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> lockOrUnlock(@PathVariable("id") UUID id) {
        try {
            User user = service.findById(id);
            if (user == null) {
                return ResponseEntity.ok(new BaseResponse(null,
                        "Không tìm thấy tài khoản trên hệ thống", HttpStatus.NOT_FOUND));
            }
            if (user.isEnabled()) {
                user.setEnabled(false);
                service.updateUserInfo(user);
                return ResponseEntity.ok(new BaseResponse(0,
                        "Khóa tài khoản thành công.", HttpStatus.OK));
            } else {
                user.setEnabled(false);
                service.updateUserInfo(user);
                return ResponseEntity.ok(new BaseResponse(1,
                        "Mở khóa tài khoản thành công.", HttpStatus.OK));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi cập nhật trạng thái tài khoản. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/no-auth/user/{userId}")
    public ResponseEntity<BaseResponse> getUserNoAuthById(@PathVariable("userId") UUID userId) {
        try {
            User user = service.findById(userId);
            if (user == null) {
                return ResponseEntity.ok(new BaseResponse(null,
                        "Không tìm thấy thông tin người dùng.",
                        HttpStatus.NO_CONTENT));
            } else {
                return ResponseEntity.ok(new BaseResponse(convertToDTO(user),
                        "", HttpStatus.OK));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy thông tin người dùng " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/v1/user/roles")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> getRolesOfUser(@CurrentUser UserDetailsImpl userDetails) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.listRoles(userDetails.getId()), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy thông tin quyền của người dùng " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private UserDTO convertToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
