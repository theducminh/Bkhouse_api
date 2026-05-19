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
import com.api.bkhouse.entity.PostComment;
import com.api.bkhouse.entity.User;
import com.api.bkhouse.payload.dto.PostCommentDTO;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.payload.response.CommentResponse;
import com.api.bkhouse.security.services.UserDetailsImpl;
import com.api.bkhouse.service.PostCommentService;
import com.api.bkhouse.service.UserService;
import com.api.bkhouse.util.Util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class PostCommentController {

    private final PostCommentService service;

    private final UserService userService;

    private final ModelMapper modelMapper;

    public PostCommentController(PostCommentService service, UserService userService, ModelMapper modelMapper) {
        this.service = service;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    private static final Logger logger = LoggerFactory.getLogger(PostCommentController.class);

    private static final UUID ANONYMOUS_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @GetMapping("/api/no-auth/post-comment/all/{postId}")
    public ResponseEntity<BaseResponse> findAllByPostId(@PathVariable UUID postId) {
        try {
            List<PostComment> postComments = service.findByPostId(postId);
            List<CommentResponse> commentResponses = postComments
                    .stream()
                    .map(e -> modelMapper.map(e, CommentResponse.class))
                    .collect(Collectors.toList());
            commentResponses.forEach(e -> {
                User user = userService.findById(e.getCreateBy());
                // Xử lý an toàn nếu user bị xóa nhưng comment vẫn còn (Tránh NullPointerException)
                if (user != null) {
                    e.setFullName(setFullName(user.getFirstName(), user.getMiddleName(), user.getLastName()));
                    e.setAvatarUrl(user.getAvatarUrl());
                } else {
                    e.setFullName("Người dùng ẩn danh");
                    e.setAvatarUrl("");
                }
            });
            return ResponseEntity.ok(new BaseResponse(commentResponses, "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách bình luận của bài viết với postId {}: {}", postId, e.getMessage());
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bình luận của bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @PostMapping("/api/no-auth/post-comment")
    public ResponseEntity<BaseResponse> noAuthCreate(@RequestBody PostCommentDTO body) {
        try {
            body.setCreateBy(ANONYMOUS_USER_ID);
            body.setCreateAt(Util.getCurrentDateTime());
            PostComment postComment = service.save(modelMapper.map(body, PostComment.class));
            CommentResponse commentResponse = modelMapper.map(postComment, CommentResponse.class);
            commentResponse.setAvatarUrl("");
            commentResponse.setFullName("Ẩn danh");
            return ResponseEntity.ok(new BaseResponse(commentResponse, "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi gửi bình luận không xác thực: {}", e.getMessage());
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Gửi bình luận không thành công. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @PostMapping("/api/v1/post-comment")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> authCreate(
            @RequestBody PostCommentDTO body,
            @CurrentUser UserDetailsImpl userDetails
            ) {
        try {
            body.setCreateBy(userDetails.getId());
            body.setCreateAt(Util.getCurrentDateTime());
            PostComment postComment = service.save(modelMapper.map(body, PostComment.class));
            CommentResponse commentResponse = modelMapper.map(postComment, CommentResponse.class);
            User user = userService.findById(userDetails.getId());

            if (user != null) {
                commentResponse.setFullName(setFullName(user.getFirstName(), user.getMiddleName(), user.getLastName()));
                commentResponse.setAvatarUrl(user.getAvatarUrl());
            }
            return ResponseEntity.ok(new BaseResponse(commentResponse, "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi gửi bình luận có xác thực của userId {}: {}", userDetails.getId(), e.getMessage());
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Gửi bình luận không thành công. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @PutMapping("/api/v1/post-comment")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> update(
            @RequestBody PostCommentDTO body,
            @CurrentUser UserDetailsImpl userDetails
    ) {
        try {
            if (!userDetails.getId().equals(body.getCreateBy())) {
                return ResponseEntity.ok(new BaseResponse(
                        null,
                        "Chỉ có người viết bình luận mới có thể sửa được bình luận.",
                        HttpStatus.NOT_ACCEPTABLE));
            }
            body.setUpdateBy(userDetails.getId());
            body.setUpdateAt(Util.getCurrentDateTime());
            PostComment postComment = service.save(modelMapper.map(body, PostComment.class));
            return ResponseEntity.ok(new BaseResponse(postComment.getId(), "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật bình luận có id {} của userId {}: {}", body.getId(), userDetails.getId(), e.getMessage());
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Cập nhật bình luận không thành công. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @DeleteMapping("/api/v1/post-comment/{id}")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> delete(@PathVariable UUID id,
                                               @CurrentUser UserDetailsImpl userDetails) {
        try {
            if (!service.existsById(id)) {
                return ResponseEntity.ok(new BaseResponse(
                        null,
                        "Bình luận không tồn tại",
                        HttpStatus.NO_CONTENT
                ));
            }
            if (!service.canDelete(id, userDetails.getId())) {
                return ResponseEntity.ok(new BaseResponse(
                        null,
                        "Bạn không thể xóa bình luận này",
                        HttpStatus.NOT_ACCEPTABLE
                ));
            }
            service.deleteById(id);
            return ResponseEntity.ok(new BaseResponse(null, "Đã xóa bình luận.", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi xóa bình luận có id {} của userId {}: {}", id, userDetails.getId(), e.getMessage());
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi xóa bình luận. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    private String setFullName(String firstName, String middleName, String lastName) {
        return Stream.of(firstName, middleName, lastName)
                .filter(s -> s != null && !s.trim().isEmpty())
                .collect(Collectors.joining(" "));
    }
}
