package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.constant.Message;
import com.api.bkhouse.entity.InfoPost;
import com.api.bkhouse.entity.InfoType;
import com.api.bkhouse.entity.User;
import com.api.bkhouse.payload.dto.InfoPostDTO;
import com.api.bkhouse.payload.dto.InfoTypeDTO;
import com.api.bkhouse.payload.dto.UserDTO;
import com.api.bkhouse.payload.request.InfoPostRequest;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.payload.response.InfoPostResponse;
import com.api.bkhouse.payload.response.TinTucResponse;
import com.api.bkhouse.service.InfoPostService;
import com.api.bkhouse.service.InfoTypeService;
import com.api.bkhouse.service.NotifyService;
import com.api.bkhouse.service.UserService;
import com.api.bkhouse.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class InfoPostController {
    @Autowired
    private InfoPostService service;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private InfoTypeService infoTypeService;

    @Autowired
    private NotifyService notifyService;

    @GetMapping("/api/v1/info-post")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getAll() {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.findAll().stream().map(this::getInfoPostResponse).collect(Collectors.toList()),
                    "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài đăng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @PostMapping("/api/v1/info-post")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> create(@RequestBody InfoPostRequest request) {
        try {
            request.getInfoPost().setCreateAt(Util.getCurrentDateTime());
            InfoPost infoPost = service.create(convertToEntity(request.getInfoPost()));
            if (request.getInfoPost().getInfoType().getId() == 6) {
                notifyService.notifyPriceFluctuation(Message.TAO_BAI_DANG, request.getDistrictCodes(), infoPost.getId());
            }
            return ResponseEntity.ok(new BaseResponse(
                    convertToDTO(infoPost),
                    "Tạo bài viết thành công.",
                    HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi tạo bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @PutMapping("/api/v1/info-post")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> update(@RequestBody InfoPostDTO request) {
        try {
            request.setUpdateAt(Util.getCurrentDateTime());
            InfoPost infoPost = service.update(convertToEntity(request));
            return ResponseEntity.ok(new BaseResponse(
                    convertToDTO(infoPost),
                    "Cập nhật bài viết thành công.",
                    HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi cập nhật bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/info-post/{id}")
    public ResponseEntity<BaseResponse> findById(@PathVariable("id") Long id) {
        try {
            InfoPost infoPost = service.findById(id);
            if (infoPost == null) {
                return ResponseEntity.ok(new BaseResponse(
                        null,
                        "Không tìm thấy bài viết tương ứng.",
                        HttpStatus.NO_CONTENT
                ));
            }
            return ResponseEntity.ok(new BaseResponse(
                    getInfoPostResponse(infoPost), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi tìm kiếm bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/info-post/user-view/{id}")
    public ResponseEntity<BaseResponse> findByIdWithIncreaseView(@PathVariable("id") Long id) {
        try {
            InfoPost infoPost = service.findById(id);
            if (infoPost == null) {
                return ResponseEntity.ok(new BaseResponse(
                        null,
                        "Không tìm thấy bài viết tương ứng.",
                        HttpStatus.NO_CONTENT
                ));
            }
            infoPost.setView(infoPost.getView() + 1);
            InfoPost infoPost1 = service.update(infoPost);
            return ResponseEntity.ok(new BaseResponse(
                    getInfoPostResponse(infoPost1), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi tìm kiếm bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @DeleteMapping("/api/v1/info-post/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> deleteInfoPost(@PathVariable String id) {
        try {
            Long postId = Long.valueOf(id);
            service.deleteById(postId);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Xóa bài viết thành công.",
                    HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi xóa bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/info-post/enterprise/{id}")
    public ResponseEntity<BaseResponse> getInfoPostByUserId(@PathVariable("id") UUID userId) {
        try {
            List<InfoPost> infoPosts = service.findByUserId(userId);
            return ResponseEntity.ok(new BaseResponse(
                    infoPosts.stream().map(this::getInfoPostResponse).collect(Collectors.toList()),
                    "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách dự án." + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/info-post/info-type/{infoType}")
    public ResponseEntity<BaseResponse> findByInfoType(@PathVariable("infoType") Integer infoType) {
        try {
            List<TinTucResponse> tinTucResponses = new ArrayList<>();
            if (infoType == 2) {
                List<InfoType> infoTypes = infoTypeService.getTinTucInfoType(6);
                for (InfoType infoType1: infoTypes) {
                    TinTucResponse tinTucResponse = new TinTucResponse();
                    tinTucResponse.setInfoType(modelMapper.map(infoType1, InfoTypeDTO.class));
                    List<InfoPostResponse> infoPostResponses = service.findByTypeId(infoType1.getId())
                            .stream()
                            .map(this::getInfoPostResponse)
                            .collect(Collectors.toList());
                    tinTucResponse.setInfoPosts(infoPostResponses);
                    tinTucResponses.add(tinTucResponse);
                }
            }
            InfoType infoType1 = infoTypeService.findById(infoType);
            List<InfoPost> infoPosts = service.findByTypeId(infoType);
            TinTucResponse tinTucResponse = new TinTucResponse();
            tinTucResponse.setInfoType(modelMapper.map(infoType1, InfoTypeDTO.class));
            tinTucResponse.setInfoPosts(infoPosts
                    .stream()
                    .map(this::getInfoPostResponse)
                    .collect(Collectors.toList()));
            if (infoType == 2) {
                tinTucResponses.add(0, tinTucResponse);
                return ResponseEntity.ok(new BaseResponse(tinTucResponses, "", HttpStatus.OK));
            }
            return ResponseEntity.ok(new BaseResponse(tinTucResponse, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài đăng.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/info-post/countByInfoType")
    public ResponseEntity<BaseResponse> countByInfoType(@RequestParam("infoTypeId") Integer infoTypeId) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.countByInfoTypeId(infoTypeId), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy số lượng bài viết của danh mục. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/info-post/load-more")
    public ResponseEntity<BaseResponse> loadMore(
            @RequestParam("infoTypeId") Integer infoTypeId,
            @RequestParam("limit") Integer limit,
            @RequestParam("page") Integer page
    ) {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.loadMore(infoTypeId, limit, page)
                            .stream()
                            .map(e -> getInfoPostResponse(e))
                            .collect(Collectors.toList()),
                    "",
                    HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài đăng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/info-post/homepage")
    public ResponseEntity<BaseResponse> getHomePagePosts() {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getHomePagePosts(), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài đăng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/info-post/homepage-du-an")
    public ResponseEntity<BaseResponse> getHomePageDuAnPosts() {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getHomePageDuAnPosts(), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài đăng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/info-post/statistic/chart1")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getChart1Data() {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getChar1Options(), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/info-post/statistic/chart2")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getChart2Data() {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getChar2Options(), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    private InfoPostResponse getInfoPostResponse(InfoPost infoPost) {
        User user = userService.findById(infoPost.getCreateBy());
        InfoPostDTO infoPostDTO = convertToDTO(infoPost);

        InfoPostResponse infoPostResponse = new InfoPostResponse();
        infoPostResponse.setInfoType(infoPostDTO.getInfoType());
        infoPostResponse.setUser(modelMapper.map(user, UserDTO.class));
        infoPostResponse.setContent(infoPostDTO.getContent());
        infoPostResponse.setId(infoPostDTO.getId());
        infoPostResponse.setCreateBy(infoPostDTO.getCreateBy());
        infoPostResponse.setDescription(infoPostDTO.getDescription());
        infoPostResponse.setTitle(infoPostDTO.getTitle());
        infoPostResponse.setImageUrl(infoPostDTO.getImageUrl());
        infoPostResponse.setView(infoPostDTO.getView());
        infoPostResponse.setCreateAt(infoPostDTO.getCreateAt());
        infoPostResponse.setUpdateAt(infoPostDTO.getUpdateAt());

        return infoPostResponse;
    }

    private InfoPostDTO convertToDTO(InfoPost infoPost) {
        return modelMapper.map(infoPost, InfoPostDTO.class);
    }

    private InfoPost convertToEntity(InfoPostDTO infoPostDTO) {
        return modelMapper.map(infoPostDTO, InfoPost.class);
    }
}
