package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.config.annotation.CurrentUser;
import com.api.bkhouse.entity.ForumPost;
import com.api.bkhouse.entity.PostMedia;
import com.api.bkhouse.payload.dto.ForumPostDTO;
import com.api.bkhouse.payload.dto.PostMediaDTO;
import com.api.bkhouse.payload.request.LikeRequest;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.security.services.UserDetailsImpl;
import com.api.bkhouse.service.ForumPostService;
import com.api.bkhouse.service.PhotoService;
import com.api.bkhouse.service.PostMediaService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ForumPostController {
    @Autowired
    private ForumPostService service;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PostMediaService postMediaService;

    @Autowired
    private PhotoService photoService;

    @PostMapping("/api/v1/forum-post")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> create(@RequestBody ForumPostDTO body, @CurrentUser UserDetailsImpl currentUser) {
        try {
            service.save(modelMapper.map(body, ForumPost.class), currentUser.getId(), false);
            body.getPostMedia()
                    .stream()
                    .forEach(e -> {
                        postMediaService.save(modelMapper.map(e, PostMedia.class));
                    });
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đăng bài viết thành công.",
                    HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi tạo bài viết mới.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/api/v1/forum-post")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> update(@RequestBody ForumPostDTO body, @CurrentUser UserDetailsImpl currentUser) {
        try {
            if (!currentUser.getId().equals(body.getCreateBy())) {
                return ResponseEntity.ok(new BaseResponse(
                        null,
                        "Chỉ có chủ của bài viết mới có thể sửa bài viết.",
                        HttpStatus.NOT_ACCEPTABLE
                ));
            }
            if (!service.existsById(body.getId())) {
                return ResponseEntity.ok(new BaseResponse(
                        null,
                        "Bài viết không tồn tại.",
                        HttpStatus.NOT_ACCEPTABLE
                ));
            }
            service.save(modelMapper.map(body, ForumPost.class), currentUser.getId(), true);
            body.getPostMedia()
                    .stream()
                    .forEach(e -> {
                        postMediaService.save(modelMapper.map(e, PostMedia.class));
                    });
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Cập nhật bài viết thành công.",
                    HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi tạo bài viết mới.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/no-auth/forum-post/{id}")
    public ResponseEntity<BaseResponse> findById(@PathVariable("id") UUID id) {
        try {
            ForumPost forumPost = service.findById(id);
            if (forumPost == null) {
                return ResponseEntity.ok(new BaseResponse(
                        null,
                        "Không tìm thấy bài viết. ",
                        HttpStatus.NO_CONTENT
                ));
            }
            List<PostMedia> postMediaList = postMediaService.findByPostId(id);
            ForumPostDTO forumPostDTO = modelMapper.map(forumPost, ForumPostDTO.class);
            forumPostDTO.setPostMedia(
                    postMediaList.stream()
                            .map(e -> modelMapper.map(e, PostMediaDTO.class))
                            .collect(Collectors.toList()));
            return ResponseEntity.ok(new BaseResponse(forumPostDTO, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/forum-post/createBy")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> findByUser(
            @RequestParam("page") Integer page,
            @RequestParam("pageSize") Integer pageSize,
            @CurrentUser UserDetailsImpl userDetails) {
        try {
            Page<ForumPost> forumPosts = service.findByUser(userDetails.getId(), pageSize, page);
            List<ForumPostDTO> forumPostDTOS = new ArrayList<>();
            forumPosts.
                    stream()
                    .forEach(e -> {
                        List<PostMedia> postMediaList = postMediaService.findByPostId(e.getId());
                        ForumPostDTO forumPostDTO = modelMapper.map(e, ForumPostDTO.class);
                        forumPostDTO.setPostMedia(
                                postMediaList.stream()
                                        .map(ee -> modelMapper.map(ee, PostMediaDTO.class))
                                        .collect(Collectors.toList()));
                        forumPostDTOS.add(forumPostDTO);
                    });
            return ResponseEntity.ok(new BaseResponse(forumPostDTOS, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài viết của người dùng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/forum-post")
    public ResponseEntity<BaseResponse> getAllPageable(
            @RequestParam("page") Integer page,
            @RequestParam("pageSize") Integer pageSize) {
        try {
            Page<ForumPost> forumPosts = service.findAllWithPageable(pageSize, page);
            List<ForumPostDTO> forumPostDTOS = new ArrayList<>();
            forumPosts.
                    stream()
                    .forEach(e -> {
                        List<PostMedia> postMediaList = postMediaService.findByPostId(e.getId());
                        ForumPostDTO forumPostDTO = modelMapper.map(e, ForumPostDTO.class);
                        forumPostDTO.setPostMedia(
                                postMediaList.stream()
                                        .map(ee -> modelMapper.map(ee, PostMediaDTO.class))
                                        .collect(Collectors.toList()));
                        forumPostDTOS.add(forumPostDTO);
                    });
            return ResponseEntity.ok(new BaseResponse(forumPostDTOS, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @PostMapping("/api/v1/forum-post/like")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> like(@RequestBody LikeRequest request, @CurrentUser UserDetailsImpl userDetails) {
        try {
            if (!service.existsById(request.getPostId())) {
                return ResponseEntity.ok(new BaseResponse(null, "Bài viết không tồn tại", HttpStatus.NO_CONTENT));
            }
            boolean response = service.like(request.getPostId(), userDetails.getId());
            return ResponseEntity.ok(new BaseResponse(response, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi like bài viết.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/v1/forum-post/liked/{postId}")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> isLiked(@PathVariable("postId") UUID postId, @CurrentUser UserDetailsImpl userDetails) {
        try {
            if (!service.existsById(postId)) {
                return ResponseEntity.ok(new BaseResponse(null, "Bài viết không tồn tại", HttpStatus.NO_CONTENT));
            }
            return ResponseEntity.ok(new BaseResponse(service.isLiked(postId, userDetails.getId()), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin lượt thích.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }
    @GetMapping("/api/no-auth/forum-post/log/{postId}")
    public ResponseEntity<BaseResponse> getLog(@PathVariable("postId") UUID postId) {
        try {
            if (!service.existsById(postId)) {
                return ResponseEntity.ok(new BaseResponse(null, "Bài viết không tồn tại", HttpStatus.NO_CONTENT));
            }
            return ResponseEntity.ok(new BaseResponse(service.getLog(postId), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin bài viết." + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/api/v1/forum-post/{postId}")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> deletePost(@PathVariable UUID postId, @CurrentUser UserDetailsImpl userDetails) {
        try {
            ForumPost forumPost = service.findById(postId);
            if (forumPost == null) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài viết.", HttpStatus.NO_CONTENT));
            }
            if (!userDetails.getId().equals("admin")) {
                if (!forumPost.getCreateBy().equals(userDetails.getId())) {
                    return ResponseEntity.ok(new BaseResponse(null, "Bạn không thể xóa bài viết này.", HttpStatus.NO_CONTENT));
                }
            }
//            List<PostMedia> postMediaList = postMediaService.findByPostId(postId);
//            postMediaList.stream().forEach(e -> {
//                photoService.deletePhotoById(e.getId());
//            });
//            postMediaService.deleteByPostId(postId);
            service.deleteById(postId);
            return ResponseEntity.ok(new BaseResponse(null, "Xóa bài viết thành công.", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi xóa bài viết." + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/v1/forum-post/user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> findAllOfUser() {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.findAllNotByAdmin(),
                    "",
                    HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài viết của người dùng.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/v1/forum-post/statistic/chart1")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getChart1Data(@RequestParam Integer month, @RequestParam Integer year) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getChart1Data(month, year), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê bài viết trên diễn đàn.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/v1/forum-post/statistic/chart2")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getChart2Data(@RequestParam Integer month, @RequestParam Integer year) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getChart2Data(month, year), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê bài viết trên diễn đàn.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
