package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.constant.enumeric.EProjectType;
import com.api.bkhouse.entity.*;
import com.api.bkhouse.payload.dto.*;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.service.PostReportService;
import com.api.bkhouse.service.ProjectService;
import com.api.bkhouse.service.ReportTypeService;
import com.api.bkhouse.util.Util;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.UUID;

@RestController
@RequestMapping("/api/no-auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NoAuthController {
    private ReportTypeService reportTypeService;

    private PostReportService postReportService;

    private ModelMapper modelMapper;

    private ProjectService projectService;

    public NoAuthController(ReportTypeService reportTypeService, PostReportService postReportService, ModelMapper modelMapper, ProjectService projectService) {
        this.reportTypeService = reportTypeService;
        this.postReportService = postReportService;
        this.modelMapper = modelMapper;
        this.projectService = projectService;
    }

    private static final UUID ANONYMOUS_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private Logger logger = LoggerFactory.getLogger(NoAuthController.class);

    @GetMapping("/report-type/rep")
    public ResponseEntity<BaseResponse> getAllReportType() {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    reportTypeService
                            .getAllByIsForum(false)
                            .stream()
                            .map(e -> modelMapper.map(e, ReportTypeDTO.class))
                            .collect(Collectors.toList()), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách danh mục báo cáo: ", e);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách danh mục báo cáo của bài đăng bán / cho thuê.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/post-report")
    public ResponseEntity<BaseResponse> create(@RequestBody PostReportDTO body) {
        try {
            body.setCreateBy(ANONYMOUS_USER_ID);
            body.setCreateAt(Util.getCurrentDateTime());
            postReportService.save(modelMapper.map(body, PostReport.class));
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã gửi báo cáo bài viết tới quản trị viên.",
                    HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi báo cáo bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/project/isInterested")
    public ResponseEntity<BaseResponse> isInterested(
            @RequestParam(value = "userId", required = false) String userIdStr, // BƯỚC 1: Đổi UUID thành String
            @RequestParam("deviceInfo") String deviceInfo,
            @RequestParam("projectId") UUID projectId) {
        try {
            // BƯỚC 2: Tự xử lý ép kiểu bằng tay để chống lỗi chuỗi rỗng "" của Angular
            UUID currentUserId = ANONYMOUS_USER_ID; // Mặc định là khách ẩn danh
            
            if (userIdStr != null && !userIdStr.trim().isEmpty() && !userIdStr.equals("null") && !userIdStr.equals("undefined")) {
                currentUserId = UUID.fromString(userIdStr); // Nếu có ID thật thì mới ép kiểu
            }
            
            return ResponseEntity.ok(new BaseResponse(
                    projectService.isInterested(currentUserId, projectId, deviceInfo), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            logger.error("Lỗi khi check isInterested: ", e);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin người dùng quan tâm của bài viết.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @PostMapping("/project/interested")
    public ResponseEntity<BaseResponse> anonymousInterested(@RequestBody ProjectInterestedDTO body) {
        try {
            if (!projectService.existsByIdAndEnable(body.getProjectId())) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng phù hợp.", HttpStatus.NOT_FOUND));
            }
            Optional<ProjectInterested> interestedOptional = projectService.findByDeviceInfoAndProjectId(body.getDeviceInfo(), body.getProjectId());
            if (interestedOptional.isEmpty()) {
                body.setCreateAt(Util.getCurrentDateTime());
                body.setId(null);
                body.setUserId(ANONYMOUS_USER_ID);
                ProjectInterested interested = modelMapper.map(body, ProjectInterested.class);
                return ResponseEntity.ok(new BaseResponse(
                        modelMapper.map(projectService.saveInterested(interested), ProjectInterestedDTO.class),
                        "", HttpStatus.OK
                ));
            } else {
                projectService.deleteInterested(interestedOptional.get().getId());
                return ResponseEntity.ok(new BaseResponse(interestedOptional.get().getId(), "DELETED", HttpStatus.OK));
            }
        } catch (Exception e) {
            logger.error("Lỗi khi xử lý quan tâm bài viết: ", e);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lưu thông tin quan tâm bài đăng.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }


    @GetMapping("/project/type")
    public ResponseEntity<BaseResponse> findByType(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam String type) {
        try {
            List<Project> projects = projectService.findByTypePageable(page, pageSize, EProjectType.valueOf(type));
            List<ProjectDTO> response = projects.stream().map(e -> modelMapper.map(e, ProjectDTO.class)).collect(Collectors.toList());
            return ResponseEntity.ok(new BaseResponse(response, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài viết dự án. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/project/increase-view/{id}")
    public ResponseEntity<BaseResponse> increaseView(@PathVariable UUID id) {
        try {
            if (!projectService.existsByIdAndEnable(id)) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy thông tin dự án", HttpStatus.OK));
            }

            Long increaseViewId = projectService.increaseView(id);
            return ResponseEntity.ok(new BaseResponse(increaseViewId, "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi tăng lượt xem dự án: ", e);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài viết dự án. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }
}
