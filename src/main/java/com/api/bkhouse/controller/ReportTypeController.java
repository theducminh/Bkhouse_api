package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.config.annotation.CurrentUser;
import com.api.bkhouse.entity.ReportType;
import com.api.bkhouse.payload.dto.ReportTypeDTO;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.payload.response.ReportTypeResponse;
import com.api.bkhouse.security.services.UserDetailsImpl;
import com.api.bkhouse.service.ReportTypeService;
import com.api.bkhouse.util.Util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/report-type")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportTypeController {
    @Autowired
    private ReportTypeService service;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> create(
            @RequestBody ReportTypeDTO reportTypeDTO,
            @CurrentUser UserDetailsImpl userDetails
    ) {
        try {
            reportTypeDTO.setCreateBy(userDetails.getId());
            reportTypeDTO.setCreateAt(Util.getCurrentDateTime());
            ReportType reportType = service.save(modelMapper.map(reportTypeDTO, ReportType.class));
            return ResponseEntity.ok(new BaseResponse(
                    modelMapper.map(reportType, ReportTypeDTO.class),
                    "Tạo danh mục báo cáo thành công.",
                    HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi tạo danh mục báo cáo.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> update(
            @RequestBody ReportTypeDTO reportTypeDTO,
            @CurrentUser UserDetailsImpl userDetails
    ) {
        try {
            reportTypeDTO.setUpdateBy(userDetails.getId());
            reportTypeDTO.setUpdateAt(Util.getCurrentDateTime());
            ReportType reportType = service.save(modelMapper.map(reportTypeDTO, ReportType.class));
            return ResponseEntity.ok(new BaseResponse(
                    modelMapper.map(reportType, ReportTypeDTO.class),
                    "Cập nhật danh mục báo cáo thành công.",
                    HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi cập nhật danh mục báo cáo.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> delete(@PathVariable Integer id) {
        try {
            service.deletePostReportTypeByReportTypeId(id);
            service.deletePostReportType(id);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Xóa danh mục báo cáo thành công.",
                    HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi xóa danh mục báo cáo.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getAllReportType() {
        try {
            List<ReportTypeResponse> reportTypeResponses = new ArrayList<>();
            service
                .getAll()
                .stream()
                .forEach(e -> {
                    ReportTypeResponse response = modelMapper.map(e, ReportTypeResponse.class);
                    response.setCount(service.countByReportTypeId(e.getId()));
                    reportTypeResponses.add(response);
                });
            return ResponseEntity.ok(new BaseResponse(reportTypeResponses, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách danh mục báo cáo.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/fp")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> getAllReportTypeFP() {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service
                            .getAllByIsForum(true)
                            .stream()
                            .map(e -> modelMapper.map(e, ReportTypeDTO.class))
                            .collect(Collectors.toList()), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách danh mục báo cáo của bài đăng trên diễn đàn." + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
