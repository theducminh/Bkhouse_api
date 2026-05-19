package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.config.annotation.CurrentUser;
import com.api.bkhouse.entity.PostReport;
import com.api.bkhouse.payload.dto.PostReportDTO;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.security.services.UserDetailsImpl;
import com.api.bkhouse.service.PostReportService;
import com.api.bkhouse.util.Util;

import java.util.stream.Collectors;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/post-report")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PostReportController {
    
    private final PostReportService service;

    
    private final ModelMapper modelMapper;

    public PostReportController(PostReportService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    private static final Logger logger = LoggerFactory.getLogger(PostReportController.class);

    @PostMapping
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> create(@RequestBody PostReportDTO body, @CurrentUser UserDetailsImpl userDetails) {
        try {
            body.setCreateBy(userDetails.getId());
            body.setCreateAt(Util.getCurrentDateTime());

            // Mặc định khi tạo mới thì trạng thái sẽ là "PENDING" (Đang chờ xử lý)
            if (body.getStatus() == null || body.getStatus().trim().isEmpty()) {
                body.setStatus("PENDING");
            }

            service.save(modelMapper.map(body, PostReport.class));
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã gửi báo cáo bài viết tới quản trị viên.",
                    HttpStatus.OK
            ));
        } catch (Exception e) {
            logger.error("Lỗi khi tạo báo cáo bài viết của userId {}: {}", userDetails.getId(), e.getMessage());
             return ResponseEntity.ok(new BaseResponse(
                     null,
                     "Đã xảy ra lỗi khi gửi báo cáo bài viết. " + e.getMessage(),
                     HttpStatus.INTERNAL_SERVER_ERROR
             ));
        }
    }

    @GetMapping("/statistic")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getAllStatistic() {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.getAllStatistic(), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            logger.error("Lỗi khi lấy thông tin thống kê báo cáo bài viết: {}", e.getMessage());
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê báo cáo bài viết.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/post/{postId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> findByPostId(@PathVariable("postId") UUID postId) {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.findByPostId(postId)
                            .stream()
                            .map(e -> modelMapper.map(e, PostReportDTO.class))
                            .collect(Collectors.toList()), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách báo cáo bài viết với postId {}: {}", postId, e.getMessage());
             
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách báo cáo bài viết.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
