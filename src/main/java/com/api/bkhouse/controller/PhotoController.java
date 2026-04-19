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
 * @author dieppv
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @Autowired
    private PostMediaService postMediaService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @PostMapping("/api/v1/photos")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> addPhoto(@RequestParam("title") String title,
                                                 @RequestParam("image") MultipartFile image, Model model) {
        try {
            String id = photoService.addPhoto(title, image);
            return ResponseEntity.ok(new BaseResponse(id, "", HttpStatus.OK));
        } catch (Exception e) {
             return ResponseEntity.ok(new BaseResponse(null,
                     "Đã xảy ra lỗi khi tải ảnh lên. " + e.getMessage(),
                     HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/api/v1/photos/{postId}")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> deletePhotoByPostId(@PathVariable("postId") UUID postId) {
        try {
            List<PostMedia> postMediaList = postMediaService.findByPostId(postId);
            for (PostMedia postMedia :
                    postMediaList) {
                photoService.deletePhotoById(postMedia.getId());
            }
            postMediaService.deleteByPostId(postId);
            return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi xóa ảnh. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/api/v1/photos/photo/{id}")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> deletePhotoById(@PathVariable("id") String photoId) {
        try {
            photoService.deletePhotoById(photoId);
            postMediaService.deleteById(photoId);
            return ResponseEntity.ok(new BaseResponse(null, "Xóa ảnh thành công.", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi xóa ảnh.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/no-auth/photos/{id}")
    public ResponseEntity<BaseResponse> getPhoto(@PathVariable String id, Model model) {
        try {
            Photo photo = photoService.getPhoto(id);
            MediaResponse response = new MediaResponse(photo.getId().toString(),
                    photo.getTitle(),
                    Base64.getEncoder().encodeToString(photo.getImage().getData()));
//        model.addAttribute("title", photo.getTitle());
//        model.addAttribute("image",
//                Base64.getEncoder().encodeToString(photo.getImage().getData()));
//            return Base64.getEncoder().encodeToString(photo.getImage().getData());
            return ResponseEntity.ok(new BaseResponse(response, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy ảnh. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
