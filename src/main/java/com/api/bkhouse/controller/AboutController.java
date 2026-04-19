package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.entity.About;
import com.api.bkhouse.payload.dto.AboutDTO;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.service.AboutService;
import com.api.bkhouse.util.Util;

//import java.time.Instant;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class AboutController {
    @Autowired
    private AboutService service;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/api/no-auth/about")
    public ResponseEntity<BaseResponse> getAbout() {
        try {
            About about = service.get();
            return ResponseEntity.ok(new BaseResponse(convertToDTO(about), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy thông tin doanh nghiệp " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/api/v1/about")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> updateAbout(@RequestBody AboutDTO aboutDTO) {
        try {
            aboutDTO.setUpdateBy("admin");
            aboutDTO.setUpdateAt(Util.getCurrentDateTime());
            service.update(modelMapper.map(aboutDTO, About.class));
            return ResponseEntity.ok(new BaseResponse(null, "Cập nhật thông tin thành công.", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi cập nhật thông tin. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    private AboutDTO convertToDTO(About about) {
        return modelMapper.map(about, AboutDTO.class);
    }
}
