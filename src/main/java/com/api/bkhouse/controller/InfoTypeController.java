package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.entity.InfoType;
import com.api.bkhouse.payload.dto.InfoTypeDTO;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.service.InfoTypeService;
import com.api.bkhouse.repository.UserRepository;
import com.api.bkhouse.util.Util;
import org.springframework.security.core.Authentication;
import com.api.bkhouse.entity.User;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/info-type")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InfoTypeController {
    @Autowired
    private InfoTypeService service;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/skip")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getAllSkip() {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.getAllSkip6().stream().map(e -> convertToDTO(e)).collect(Collectors.toList()),
                    "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách danh mục tin tức. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getAll() {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.getAll().stream().map(e -> convertToDTO(e)).collect(Collectors.toList()),
                    "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách danh mục tin tức. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> findById(@PathVariable("id") Integer id) {
        try {
            InfoType infoType = service.findById(id);
            if (infoType == null) {
                return ResponseEntity.ok(new BaseResponse(
                        null,
                        "Không tìm thấy thông tin danh mục.",
                        HttpStatus.NO_CONTENT
                ));
            }
            return ResponseEntity.ok(new BaseResponse(
                    convertToDTO(infoType), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin danh mục. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> insert(@RequestBody InfoTypeDTO infoTypeDTO) {
        try {
            //lấy id của admin trong bảng users để set vào trường createBy
            // 1. Lấy Username của người đang đăng nhập từ SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            // 2. Tìm User trong Database dựa vào Username
            // (Nếu hàm findByUsername của bạn trả về Optional, hãy dùng .get() hoặc .orElseThrow())
            User adminUser = userRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản Admin"));

            // 3. Set UUID vào trường createBy
            infoTypeDTO.setCreateBy(adminUser.getId());
            infoTypeDTO.setCreateAt(Util.getCurrentDateTime());
            InfoType infoType = service.createInfoType(convertToEntity(infoTypeDTO));
            return ResponseEntity.ok(new BaseResponse(convertToDTO(infoType),
                    "Tạo danh mục tin tức mới thành công.",
                    HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi tạo danh mục tin tức. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> update(@RequestBody InfoTypeDTO infoTypeDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            User adminUser = userRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản Admin"));

            infoTypeDTO.setUpdateBy(adminUser.getId());
            infoTypeDTO.setUpdateAt(Util.getCurrentDateTime());
            InfoType infoType = service.updateInfoType(convertToEntity(infoTypeDTO));
            return ResponseEntity.ok(new BaseResponse(convertToDTO(infoType),
                    "Cập nhật danh mục tin tức thành công.",
                    HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi cập nhật danh mục tin tức. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> delete(@PathVariable("id") String id) {
        try {
            Integer infoTypeId = Integer.valueOf(id);
            service.deleteInfoPostByInfoTypeId(infoTypeId);
            service.deleteInfoType(infoTypeId);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Xóa danh mục tin tức thành công.",
                    HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi xóa danh mục tin tức. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    private InfoType convertToEntity(InfoTypeDTO infoTypeDTO) {
        return modelMapper.map(infoTypeDTO, InfoType.class);
    }

    private InfoTypeDTO convertToDTO(InfoType infoType) {
        return modelMapper.map(infoType, InfoTypeDTO.class);
    }
}
