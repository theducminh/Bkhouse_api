package com.api.bkhouse.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.payload.dto.PostViewDTO;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.service.PostViewService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/no-auth/post-view")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PostViewController {

    
    private PostViewService service;
    public PostViewController(PostViewService service) {
        this.service = service;
    }
    private static final Logger logger = LoggerFactory.getLogger(PostViewController.class);

    // API để Frontend gọi mỗi khi vào xem chi tiết bài đăng
    @PostMapping("/record")
    public ResponseEntity<BaseResponse> recordView(@RequestBody PostViewDTO body, HttpServletRequest request) {
        try {
            // Tự động lấy IP của người dùng qua thư viện HttpServletRequest
            String ipAddress = request.getHeader("X-FORWARDED-FOR");  
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {  
                ipAddress = request.getRemoteAddr();  
            } else {
                // Nếu có nhiều IP (do đi qua proxy/load balancer), chỉ lấy IP thật đầu tiên
                ipAddress = ipAddress.split(",")[0].trim();
            }

            service.recordView(body, ipAddress);
            
            return ResponseEntity.ok(new BaseResponse(null, "Ghi nhận view thành công", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi ghi nhận view: ", e);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Lỗi khi ghi nhận view: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}