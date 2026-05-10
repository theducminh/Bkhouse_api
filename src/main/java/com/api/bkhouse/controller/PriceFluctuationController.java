package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.config.annotation.CurrentUser;
import com.api.bkhouse.entity.District;
import com.api.bkhouse.entity.PriceFluctuation;
import com.api.bkhouse.entity.User;
import com.api.bkhouse.payload.dto.DistrictDTO;
import com.api.bkhouse.payload.dto.UserDTO;
import com.api.bkhouse.payload.request.EnablePFRequest;
import com.api.bkhouse.payload.request.PriceFluctuationRequest;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.payload.response.PriceFluctuationResponse;
import com.api.bkhouse.security.services.UserDetailsImpl;
import com.api.bkhouse.service.DistrictService;
import com.api.bkhouse.service.PriceFluctuationService;
import com.api.bkhouse.service.UserService;
import com.api.bkhouse.util.Util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/price-fluctuation")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PriceFluctuationController {
    @Autowired
    private PriceFluctuationService service;

    @Autowired
    private UserService userService;

    @Autowired
    private DistrictService districtService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> findByUserId(@PathVariable("userId") UUID userId) {
        try {
            List<PriceFluctuation> priceFluctuations = service.findByUserId(userId);
            if (priceFluctuations.isEmpty()) {
                return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.NO_CONTENT));
            }
            PriceFluctuationResponse response = new PriceFluctuationResponse();
            User user = userService.findById(userId);
            response.setUser(modelMapper.map(user, UserDTO.class));
            List<DistrictDTO> districts = new ArrayList<>();
            for (PriceFluctuation priceFluctuation: priceFluctuations) {
                District district = districtService.findByCode(priceFluctuation.getDistrictCode());
                districts.add(modelMapper.map(district, DistrictDTO.class));
            }
            response.setDistricts(districts);

            PriceFluctuation priceFluctuation = priceFluctuations.get(0);
            response.setCreateAt(priceFluctuation.getCreateAt());
            response.setEnable(priceFluctuation.isEnable());
            response.setUpdateAt(priceFluctuation.getUpdateAt());
            response.setCreateBy(priceFluctuation.getCreateBy());
            response.setUpdateBy(priceFluctuation.getUpdateBy());

            return ResponseEntity.ok(new BaseResponse(response, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin đăng ký biến động giá của người dùng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> insert(
            @RequestBody PriceFluctuationRequest request,
            @CurrentUser UserDetailsImpl currentUser) {
        try {
            if (request.getDistricts().isEmpty()) {
                return ResponseEntity.ok(new BaseResponse(
                        null,
                        "Danh sách quận/huyện/thị xã trống.",
                        HttpStatus.NO_CONTENT));
            }
            PriceFluctuation priceFluctuation = new PriceFluctuation();
            priceFluctuation.setId(null);
            priceFluctuation.setDistrictPrice(0L);
            priceFluctuation.setCreateAt(Util.getCurrentDateTime());
            priceFluctuation.setEnable(true);
            priceFluctuation.setCreateBy(currentUser.getId());
            priceFluctuation.setUserId(request.getUserId());

            for (String districtCode: request.getDistricts()) {
                priceFluctuation.setDistrictCode(districtCode);
                service.save(priceFluctuation);
            }

            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đăng ký nhận thông báo biến động giá thành công.",
                    HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi đăng ký nhận thông báo biến động giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> update(
            @RequestBody PriceFluctuationRequest request,
            @CurrentUser UserDetailsImpl currentUser) {
        try {
            if (request.getDistricts().isEmpty()) {
                return ResponseEntity.ok(new BaseResponse(
                        null,
                        "Danh sách quận/huyện/thị xã trống.",
                        HttpStatus.NO_CONTENT));
            }
            service.deleteByUserId(request.getUserId());

            PriceFluctuation priceFluctuation = new PriceFluctuation();
            priceFluctuation.setId(null);
            priceFluctuation.setDistrictPrice(0L);
            priceFluctuation.setCreateAt(Util.getCurrentDateTime());
            priceFluctuation.setEnable(true);
            priceFluctuation.setCreateBy(currentUser.getId());
            priceFluctuation.setUserId(request.getUserId());
            priceFluctuation.setUpdateAt(Util.getCurrentDateTime());
            priceFluctuation.setUpdateBy(currentUser.getId());

            for (String districtCode: request.getDistricts()) {
                priceFluctuation.setDistrictCode(districtCode);
                service.save(priceFluctuation);
            }

            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Cập nhật thông tin đăng ký nhận thông báo biến động giá thành công.",
                    HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi đăng ký nhận thông báo biến động giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> deleteByUserId(@CurrentUser UserDetailsImpl currentUser) {
        try {
            service.deleteByUserId(currentUser.getId());
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Hủy đăng ký nhận thông báo biến động giá thành công.",
                    HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi hủy đăng ký nhận thông báo biến động giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @PostMapping("/enable")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> enable(
            @RequestBody EnablePFRequest request,
            @CurrentUser UserDetailsImpl currentUser) {
        try {
            List<PriceFluctuation> priceFluctuations = service.findByUserId(request.getUserId());
            priceFluctuations
                    .stream()
                    .forEach(e -> {
                        e.setUpdateBy(currentUser.getId());
                        e.setUpdateAt(Util.getCurrentDateTime());
                        service.save(e);
                    });
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Cập nhật trạng thái nhận thông báo biến động giá thành công.",
                    HttpStatus.OK
                    ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi cập nhật trạng thái nhận thông báo biến động giá. ",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }
}
