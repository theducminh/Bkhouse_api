package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.entity.District;
import com.api.bkhouse.payload.dto.DistrictDTO;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.service.DistrictService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/no-auth/district")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DistrictController {
    @Autowired
    private DistrictService districtService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/{code}")
    public ResponseEntity<BaseResponse> findByCode(@PathVariable("code") String code) {
        try {
            District district = districtService.findByCode(code);
            if (district == null) {
                return ResponseEntity.ok(new BaseResponse(null,
                        "Không tìm thấy thông tin phù hợp",
                        HttpStatus.NOT_FOUND));
            } else {
                return ResponseEntity.ok(new BaseResponse(convertToDTO(district), "", HttpStatus.OK));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi thực thi. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/province/{provinceCode}")
    public ResponseEntity<BaseResponse> findByProvinceCode(@PathVariable("provinceCode") String code) {
        try {
            List<District> districts = districtService.findByProvinceCode(code);
            if (districts == null || districts.isEmpty()) {
                return ResponseEntity.ok(new BaseResponse(null,
                        "Không tìm thấy thông tin phù hợp",
                        HttpStatus.NO_CONTENT));
            } else {
                return ResponseEntity.ok(new BaseResponse(districts.stream().map(this::convertToDTO).collect(Collectors.toList())
                        , "", HttpStatus.OK));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi thực thi. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private DistrictDTO convertToDTO(District district) {
        return this.modelMapper.map(district, DistrictDTO.class);
    }
}
