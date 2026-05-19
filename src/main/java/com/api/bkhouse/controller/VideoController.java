/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.api.bkhouse.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.api.bkhouse.payload.dto.VideoDTO;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.service.VideoService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

/**
 *
 * @author ducnm
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);
    @PostMapping("/api/v1/videos/add")
    public ResponseEntity<BaseResponse> addVideo(@RequestParam("title") String title,
                                                 @RequestParam("file") MultipartFile file) { // 🚨 Dọn dẹp Model model
        try {
            String id = videoService.addVideo(title, file);
            return ResponseEntity.ok(new BaseResponse(id, "Tải video lên thành công.", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi tải video lên: ", e);
            return ResponseEntity.ok(new BaseResponse(null, "Lỗi khi tải video: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/no-auth/videos/{id}")
    public ResponseEntity<BaseResponse> getVideoInfo(@PathVariable String id) {
        try {
            VideoDTO video = videoService.getVideo(id);
            if (video == null) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy video.", HttpStatus.NO_CONTENT));
            }
            
            // 🚨 ĐÃ GỠ BOM OOM (Out Of Memory): Chỉ trả về đường dẫn Stream để Frontend nhét vào <video src="...">
            String streamUrl = "/api/no-auth/videos/stream/" + id;
            return ResponseEntity.ok(new BaseResponse(streamUrl, "Lấy thông tin video thành công", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi lấy thông tin video: ", e);
            return ResponseEntity.ok(new BaseResponse(null, "Lỗi: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/no-auth/videos/stream/{id}")
    public void streamVideo(@PathVariable String id, HttpServletResponse response) {
        try {
            VideoDTO video = videoService.getVideo(id);
            if (video != null && video.getStream() != null) {
                // 🚨 Báo cho trình duyệt biết đây là luồng Video để nó bật Media Player
                response.setContentType("video/mp4");
                // Stream dữ liệu từng phần (chunk) chứ không nạp hết vào RAM
                FileCopyUtils.copy(video.getStream(), response.getOutputStream());
            } else {
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            logger.error("Lỗi khi stream video: ", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @DeleteMapping("/api/v1/videos/{id}")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> deleteVideo(@PathVariable String id) {
        try {
            videoService.deleteVideo(id);
            return ResponseEntity.ok(new BaseResponse(null, "Xóa video thành công.", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi xóa video: ", e);
            return ResponseEntity.ok(new BaseResponse(null, "Lỗi khi xóa video: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
