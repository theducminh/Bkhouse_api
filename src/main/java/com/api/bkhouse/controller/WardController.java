package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.entity.Ward;
import com.api.bkhouse.payload.dto.WardDTO;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.service.WardService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/no-auth/ward")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WardController {
    @Autowired
    private WardService service;

    @Autowired
    private ModelMapper modelMapper;

//    @GetMapping("/{id}")
//    public ResponseEntity<BaseResponse> getByCode(@PathVariable(name = "id") String id) {
//        try {
//            Ward serviceResponse = wardService.findByCode(id);
//            if (serviceResponse == null) {
//                return ResponseEntity.ok(new BaseResponse(null, "Khong tim thay ban ghi nao", HttpStatus.NOT_FOUND));
//            } else {
//                return ResponseEntity.ok(new BaseResponse(convertToDto(serviceResponse), "", HttpStatus.OK));
//            }
//        } catch (Exception e) {
//            return ResponseEntity.ok(new BaseResponse(null, "Da xay ra loi khi thuc hien", HttpStatus.INTERNAL_SERVER_ERROR));
//        }
//    }

    @GetMapping("/district/{districtCode}")
    public ResponseEntity<BaseResponse> getAllWardsOfDistrict(@PathVariable("districtCode") String districtCode) {
        try {
            List<Ward> wards = service.getAllWardsOfDistrict(districtCode);
            if (wards.isEmpty()) {
                return ResponseEntity.ok(new BaseResponse(null,
                        "Không tìm thấy thông tin phù hợp",
                        HttpStatus.NO_CONTENT));
            } else {
                return ResponseEntity.ok(new BaseResponse(
                        wards.stream().map(this::convertToDto).collect(Collectors.toList()),
                        "",
                        HttpStatus.OK));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi thực thi. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    private WardDTO convertToDto(Ward ward) {
        return modelMapper.map(ward, WardDTO.class);
    }
}
