/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.api.bkhouse.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.api.bkhouse.entity.Photo;
import com.api.bkhouse.entity.PostMedia;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.payload.response.MediaResponse;
import com.api.bkhouse.service.PhotoService;
import com.api.bkhouse.service.PostMediaService;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ducnm
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class PhotoController {

    private PhotoService photoService;
    private PostMediaService postMediaService;

    public PhotoController(PhotoService photoService, PostMediaService postMediaService) {
        this.photoService = photoService;
        this.postMediaService = postMediaService;
    }

    private static final Logger logger = LoggerFactory.getLogger(PhotoController.class);
    
    @PostMapping("/api/v1/photos")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> addPhoto(@RequestParam("title") String title,
                                                 @RequestParam("image") MultipartFile image) {
        try {
            String id = photoService.addPhoto(title, image);
            return ResponseEntity.ok(new BaseResponse(id, "", HttpStatus.OK));
        } catch (Exception e) {
             return ResponseEntity.ok(new BaseResponse(null,
                     "Đã xảy ra lỗi khi tải ảnh lên. " + e.getMessage(),
                     HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/api/v1/photos/photo/{id}")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> deletePhotoById(@PathVariable("id") UUID postMediaId) {
        try {
            // 1. Tìm bản ghi link ảnh trong PostgreSQL bằng UUID
            // (Lưu ý: Nếu service của bạn chưa có hàm findById thì hãy gọi thẳng repository.findById(postMediaId).get() nhé)
            PostMedia postMedia = postMediaService.findById(postMediaId);
            
            if (postMedia != null) {
                String mediaUrl = postMedia.getMediaUrl();
                
                // 2. Cắt lấy ID của MongoDB từ đường link
                if (mediaUrl != null && mediaUrl.contains("/")) {
                    String mongoId = mediaUrl.substring(mediaUrl.lastIndexOf("/") + 1);
                    
                    // 3. Xóa file vật lý trong MongoDB
                    try {
                        photoService.deletePhotoById(mongoId);
                    } catch (Exception ex) {
                        logger.error("Lỗi xóa ảnh trên Mongo ID: " + mongoId, ex);
                    }
                }
                
                // 4. Xóa bản ghi đường link trong PostgreSQL
                postMediaService.deleteById(postMediaId);
            }

            return ResponseEntity.ok(new BaseResponse(null, "Xóa ảnh thành công.", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi xóa ảnh với ID: " + postMediaId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(null, "Đã xảy ra lỗi khi xóa ảnh. " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    @GetMapping("/api/no-auth/photos/{id}")
    public ResponseEntity<BaseResponse> getPhoto(@PathVariable String id) { // 🚨 Đã xóa 'Model model' thừa
        try {
            Photo photo = photoService.getPhoto(id);
            
            //  Bổ sung check null do bên PhotoService ta đã đổi thành .orElse(null) thay vì văng Exception
            if (photo == null) {
                 return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy ảnh", HttpStatus.NO_CONTENT));
            }
            
            MediaResponse response = new MediaResponse(photo.getId().toString(),
                    photo.getTitle(),
                    Base64.getEncoder().encodeToString(photo.getImage().getData()));
                    
            return ResponseEntity.ok(new BaseResponse(response, "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi lấy ảnh: ", e); // 🚨 Ghi log
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy ảnh. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
