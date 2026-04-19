package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.entity.SpecialAccount;
import com.api.bkhouse.payload.dto.SpecialAccountDTO;
import com.api.bkhouse.payload.dto.SpecialAccountPayDTO;
import com.api.bkhouse.payload.request.AgencyRegisterRequest;
import com.api.bkhouse.payload.response.AgencyInfoResponse;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.service.SpecialAccountService;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class SpecialAccountController {
    @Autowired
    private SpecialAccountService service;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/api/no-auth/special-account")
    public ResponseEntity<BaseResponse> createSpecialAccount(@RequestBody SpecialAccountDTO specialAccountDTO) {
        try {
            SpecialAccount serviceResponse = service.addSpecialAccount(convertToEntity(specialAccountDTO));
            return ResponseEntity.ok(new BaseResponse(convertToDTO(serviceResponse),
                    "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi thực thi. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/v1/special-account/agency/{userId}")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getAgencyInfo(@PathVariable("userId") UUID userId) {
        try {
            AgencyInfoResponse agencyInfoResponse = service.findAgencyInfo(userId);
            if (agencyInfoResponse == null) {
                return ResponseEntity.ok(new BaseResponse(null,
                        "Không tìm thấy thông tin đăng ký môi giới của tài khoản này.",
                        HttpStatus.NO_CONTENT));
            }
            return ResponseEntity.ok(new BaseResponse(agencyInfoResponse, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy thông tin đăng ký tài khoản môi giới. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/api/v1/special-account/agency")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<BaseResponse> createAgency(@RequestBody AgencyRegisterRequest request) {
        if (service.isAgency(request.getUserId())) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Tài khoản đã được đăng ký là môi giới trước đó.",
                    HttpStatus.NOT_ACCEPTABLE));
        }
        return ResponseEntity.ok(service.agencyRegister(request));
    }

    @PutMapping("/api/v1/special-account/agency")
    @PreAuthorize("hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> updateAgency(@RequestBody AgencyRegisterRequest request) {
        return ResponseEntity.ok(service.agencyUpdate(request));
    }

    @DeleteMapping("/api/v1/special-account/agency/{userId}")
    @PreAuthorize("hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> deleteAgency(@PathVariable("userId") UUID userId) {
//        return ResponseEntity.ok(service.deleteAgency(userId));
        try {
            service.userRoleDeleteByUserId(userId);
            service.agencyDistrictDeleteByUserId(userId);
            service.deleteByUserId(userId);
            return ResponseEntity.ok(new BaseResponse(null,
                    "Hủy đăng ký tài khoản môi giới thành công.",
                    HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi hủy đăng ký tài khoản môi giới. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/v1/special-account/{userId}")
    @PreAuthorize("hasRole('ROLE_ENTERPRISE') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> findById(@PathVariable("userId") UUID userId) {
        try {
            SpecialAccount specialAccount = service.findById(userId);
            if (specialAccount == null) {
                return ResponseEntity.ok(new BaseResponse(
                        null,
                        "Không tìm thấy thôn tin tài khoản",
                        HttpStatus.NO_CONTENT));
            }
            return ResponseEntity.ok(new BaseResponse(
                    modelMapper.map(specialAccount, SpecialAccountDTO.class),
                    "", HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin người dùng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/api/v1/special-account")
    @PreAuthorize("hasRole('ROLE_ENTERPRISE') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> update(@RequestBody SpecialAccountDTO specialAccountDTO) {
        try {
            service.update(modelMapper.map(specialAccountDTO, SpecialAccount.class));
            return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi cập nhật thông tin người dùng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/v1/special-account/rep/{repId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<BaseResponse> listAgencyByRepDistrict(@PathVariable String repId) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.listAgencyByRepDistrict(repId), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách môi giới hoạt động trong khu vực đăng bài.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    private SpecialAccount convertToEntity(SpecialAccountDTO specialAccountDTO) {
        return modelMapper.map(specialAccountDTO, SpecialAccount.class);
    }

    private SpecialAccountDTO convertToDTO(SpecialAccount specialAccount) {
        return modelMapper.map(specialAccount, SpecialAccountDTO.class);
    }
}
