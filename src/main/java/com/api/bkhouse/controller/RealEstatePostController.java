package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.config.annotation.CurrentUser;
import com.api.bkhouse.constant.Message;
import com.api.bkhouse.constant.PayContent;
import com.api.bkhouse.constant.enumeric.EDirection;
import com.api.bkhouse.constant.enumeric.ERole;
import com.api.bkhouse.constant.enumeric.EStatus;
import com.api.bkhouse.constant.enumeric.EType;
import com.api.bkhouse.entity.*;
import com.api.bkhouse.payload.dto.InterestedDTO;
import com.api.bkhouse.payload.dto.PostMediaDTO;
import com.api.bkhouse.payload.dto.post.*;
import com.api.bkhouse.payload.request.*;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.payload.response.RealEstatePostResponse;
import com.api.bkhouse.security.services.UserDetailsImpl;
import com.api.bkhouse.service.*;
import com.api.bkhouse.util.Util;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class RealEstatePostController {
    @Autowired
    private RealEstatePostService service;

    @Autowired
    private PlotService plotService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PostMediaService postMediaService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostPayService postPayService;

    @Autowired
    private SpecialAccountService specialAccountService;

    @Autowired
    private NotifyService notifyService;

    @GetMapping("/api/no-auth/real-estate-post/{id}")
    public ResponseEntity<BaseResponse> findById(@PathVariable("id") UUID id) {
        try {
            if (!service.existsByIdAndEnable(id)) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng phù hợp.", HttpStatus.NOT_FOUND));
            }
            List<String> strings = Arrays.asList(id.toString().split("-"));
            String type = strings.get(0);
            BasePost basePost = null;
            if (type.equalsIgnoreCase(EType.PLOT.toString())) {
                Plot plot = plotService.findByRealEstatePostId(id);
                if (plot != null) {
                    basePost = modelMapper.map(plot, PlotDTO.class);
                }
            } else if (type.equalsIgnoreCase(EType.APARTMENT.toString())) {
                Apartment apartment = apartmentService.findByRealEstatePostId(id);
                if (apartment != null) {
                    basePost = modelMapper.map(apartment, ApartmentDTO.class);
                }
            } else if (type.equalsIgnoreCase(EType.HOUSE.toString())) {
                House house = houseService.findByRealEstatePostId(id);
                if (house != null) {
                    basePost = modelMapper.map(house, HouseDTO.class);
                }
            }
            if (basePost == null) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng phù hợp.", HttpStatus.NOT_FOUND));
            }
            List<PostMedia> postMedia = postMediaService.findByPostId(id);

            return ResponseEntity.ok(new BaseResponse(
                    new RealEstatePostResponse(
                            basePost,
                            postMedia.stream().map(e -> modelMapper.map(e, PostMediaDTO.class)).collect(Collectors.toList())),
                    "",
                    HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy thông tin bài đăng " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/no-auth/real-estate-post/user-view/{id}")
    public ResponseEntity<BaseResponse> findByIdWithIncreaseView(@PathVariable("id") UUID id) {
        try {
            if (!service.existsByIdAndEnable(id)) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng phù hợp.", HttpStatus.NOT_FOUND));
            }
            List<String> strings = Arrays.asList(id.toString().split("-"));
            String type = strings.get(0);
            BasePost basePost = null;
            if (type.equalsIgnoreCase(EType.PLOT.toString())) {
                Plot plot = plotService.findByRealEstatePostId(id);
                if (plot != null) {
                    basePost = modelMapper.map(plot, PlotDTO.class);
                }
            } else if (type.equalsIgnoreCase(EType.APARTMENT.toString())) {
                Apartment apartment = apartmentService.findByRealEstatePostId(id);
                if (apartment != null) {
                    basePost = modelMapper.map(apartment, ApartmentDTO.class);
                }
            } else if (type.equalsIgnoreCase(EType.HOUSE.toString())) {
                House house = houseService.findByRealEstatePostId(id);
                if (house != null) {
                    basePost = modelMapper.map(house, HouseDTO.class);
                }
            }
            if (basePost == null) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng phù hợp.", HttpStatus.NOT_FOUND));
            }
            List<PostMedia> postMedia = postMediaService.findByPostId(id);
            service.updateView(id);
            return ResponseEntity.ok(new BaseResponse(
                    new RealEstatePostResponse(
                            basePost,
                            postMedia.stream().map(e -> modelMapper.map(e, PostMediaDTO.class)).collect(Collectors.toList())),
                    "",
                    HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy thông tin bài đăng " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/api/no-auth/real-estate-post/click-info")
    public ResponseEntity<BaseResponse> clickUserDetail(@RequestBody ClickedUserInfo body) {
        try {
            if (!service.existsByIdAndEnable(body.getPostId())) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng phù hợp.", HttpStatus.NOT_FOUND));
            }
            service.updateClickedView(body.getPostId());
            return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy thông tin người đăng bài. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/api/v1/real-estate-post")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> createRealEstatePost(@RequestBody RealEstatePostRequest request, @CurrentUser UserDetailsImpl userDetails) {
        try {
            RealEstatePostDTO realEstatePostDTO = request.getRealEstatePost();
            User user = userService.findById(realEstatePostDTO.getOwnerId().getId());
            Role role = new Role();
            role.setId(1);
            role.setName(ERole.ROLE_USER);
            if (!agencyPeriodPriority(user, request.getRealEstatePost().getDistrict().getCode())
                    && user.getRoles().contains(role)) {
                if (user.getAccountBalance() < Util.calculatePostPrice(realEstatePostDTO.getPriority(), realEstatePostDTO.getPeriod(), realEstatePostDTO.isSell())) {
                    return ResponseEntity.ok(new BaseResponse(null,
                            "Số dư trong tài khoản không đủ để thực hiện giao dịch.",
                            HttpStatus.INTERNAL_SERVER_ERROR));
                }
            }
            realEstatePostDTO.setCreateAt(Util.getCurrentDateTime());
            realEstatePostDTO.setCreateBy(userDetails.getId());
            RealEstatePost realEstatePost = modelMapper.map(realEstatePostDTO, RealEstatePost.class);
            service.create(realEstatePost);

            if (realEstatePostDTO.getType().equals(EType.PLOT)) {
                Plot plotEntity = modelMapper.map(request.getPlot(), Plot.class);
                plotEntity.setRealEstatePost(realEstatePost);
                plotService.create(plotEntity);
            } else if (realEstatePostDTO.getType().equals(EType.APARTMENT)) {
                Apartment apartmentEntity = modelMapper.map(request.getApartment(), Apartment.class);
                apartmentEntity.setRealEstatePost(realEstatePost);
                apartmentService.create(apartmentEntity);
            } else if (realEstatePostDTO.getType().equals(EType.HOUSE)) {
                House houseEntity = modelMapper.map(request.getHouse(), House.class);
                houseEntity.setRealEstatePost(realEstatePost);
                houseService.create(houseEntity);
            }
            List<PostMediaDTO> postMediaDTOS = request.getImages();

            for (PostMediaDTO postMediaDTO: postMediaDTOS) {
                postMediaService.save(modelMapper.map(postMediaDTO, PostMedia.class));
            }
            if (!agencyPeriodPriority(user, request.getRealEstatePost().getDistrict().getCode())
                && user.getRoles().contains(role)) {
                int pay = Util.calculatePostPrice(realEstatePostDTO.getPriority(), realEstatePostDTO.getPeriod(), realEstatePostDTO.isSell());
                if (pay > 0) {
                    PostPay postPay = new PostPay();
                    postPay.setId(0L);
                    postPay.setUser(user);
                    postPay.setRealEstatePost(realEstatePost);
                    postPay.setPrice(pay);
                    postPay.setContent(PayContent.POST_PAY);
                    postPay.setAccountBalance(user.getAccountBalance() - pay);
                    postPay.setCreateAt(Util.getCurrentDateTime());
                    this.postPayService.createPostPay(postPay);

                    user.setAccountBalance(user.getAccountBalance() - pay);
                    user.setUpdatedAt(Util.getCurrentDateTime());
                    user.setUpdatedBy(user.getId());
                    userService.updateUserInfo(user);
                }
            }
            notifyService.notifyToAdmin(Message.NEW_REP_ADMIN, realEstatePost.getId());
            return ResponseEntity.ok(new
                    BaseResponse(null,
                    "Đã tạo bài viết thành công. Chờ quản trị viên kiểm duyệt",
                    HttpStatus.OK));
        } catch (Exception e) {
            service.deleteById(request.getRealEstatePost().getId());
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi tạo bài đăng " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private boolean agencyPeriodPriority(User user, String districtCode) {
        Role role = new Role();
        role.setId(2);
        role.setName(ERole.ROLE_AGENCY);

        if (user.getRoles().contains(role)) {
            List<String> districtCodes = specialAccountService.getAllDistrictCodeOfAgency(user.getId());
            if (districtCodes.stream().anyMatch(s -> s.equals(districtCode))) {
                return true;
            }
        }
        return false;
    }
    
    @PutMapping("/api/v1/real-estate-post")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> updatePost(@RequestBody RealEstatePostRequest request, @CurrentUser UserDetailsImpl userDetails) {
        try {
            if (!service.existsByIdAndEnable(request.getRealEstatePost().getId())) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng phù hợp.", HttpStatus.NOT_FOUND));
            }
            RealEstatePost realEstatePostDB = service.findByIdAndEnable(request.getRealEstatePost().getId());
            Double currPrice = realEstatePostDB.getPrice();

            RealEstatePostDTO realEstatePostDTO = request.getRealEstatePost();
            realEstatePostDTO.setUpdateAt(Util.getCurrentDateTime());
            realEstatePostDTO.setUpdateBy(userDetails.getId());
            RealEstatePost realEstatePost = convertToEntity(realEstatePostDTO);
            service.update(realEstatePost);
            if (realEstatePostDTO.getType().equals(EType.PLOT)) {
                Plot plotEntity = convertToPlotEntity(request.getPlot());
                plotEntity.setRealEstatePost(realEstatePost);
                plotService.update(plotEntity);
            } else if (realEstatePostDTO.getType().equals(EType.APARTMENT)) {
                Apartment apartmentEntity = convertToApartmentEntity(request.getApartment());
                apartmentEntity.setRealEstatePost(realEstatePost);
                apartmentService.update(apartmentEntity);
            } else if (realEstatePostDTO.getType().equals(EType.HOUSE)) {
                House houseEntity = convertToHouseEntity(request.getHouse());
                houseEntity.setRealEstatePost(realEstatePost);
                houseService.update(houseEntity);
            }
            List<PostMediaDTO> postMediaDTOS = request.getImages();
            if (!postMediaDTOS.isEmpty()) {
                for (PostMediaDTO postMediaDTO :
                        postMediaDTOS) {
                    postMediaService.save(modelMapper.map(postMediaDTO, PostMedia.class));
                }
            }

            if (currPrice.doubleValue() != request.getRealEstatePost().getPrice().doubleValue()) {
                service.createRepPrice(request.getRealEstatePost().getPrice(), request.getRealEstatePost().getId(), userDetails.getId());
                notifyService.notifyAgencyREPUpdate(Message.CAP_NHAT_REP, realEstatePostDTO.getDistrict().getCode(), realEstatePostDTO.getId(), true);
                notifyService.notifyInterested(Message.getCAP_NHAT_REP_INTERESTED(realEstatePostDTO.getTitle()), realEstatePostDTO.getId());
            }

            return ResponseEntity.ok(new
                    BaseResponse(realEstatePost.getId(),
                    "Đã cập nhật bài viết thành công.",
                    HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi cập nhật bài đăng " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private RealEstatePost convertToEntity(RealEstatePostDTO realEstatePostDTO) {
        RealEstatePost realEstatePost = new RealEstatePost();
        realEstatePost.setArea(realEstatePostDTO.getArea());
        realEstatePost.setAddressShow(realEstatePostDTO.getAddressShow());
        realEstatePost.setDescription(realEstatePostDTO.getDescription());
        realEstatePost.setClickedView(realEstatePostDTO.getClickedView());
        realEstatePost.setEnable(realEstatePostDTO.isEnable());
        realEstatePost.setDistrict(modelMapper.map(realEstatePostDTO.getDistrict(), District.class));
        realEstatePost.setDirection(realEstatePostDTO.getDirection());
        realEstatePost.setLat(realEstatePostDTO.getLat());
        realEstatePost.setLng(realEstatePostDTO.getLng());
        realEstatePost.setPrice(realEstatePostDTO.getPrice());
        realEstatePost.setCreateAt(realEstatePostDTO.getCreateAt());
        realEstatePost.setCreateBy(realEstatePostDTO.getCreateBy());
        realEstatePost.setId(realEstatePostDTO.getId());
        realEstatePost.setOwnerId(modelMapper.map(realEstatePostDTO.getOwnerId(), User.class));
        realEstatePost.setPeriod(realEstatePostDTO.getPeriod());
        realEstatePost.setPriority(realEstatePostDTO.getPriority());
        realEstatePost.setProvince(modelMapper.map(realEstatePostDTO.getProvince(), Province.class));
        realEstatePost.setSell(realEstatePostDTO.isSell());
        realEstatePost.setStatus(realEstatePostDTO.getStatus());
        realEstatePost.setStreet(realEstatePostDTO.getStreet());
        realEstatePost.setTitle(realEstatePostDTO.getTitle());
        realEstatePost.setType(realEstatePostDTO.getType());
        realEstatePost.setUpdateAt(realEstatePostDTO.getUpdateAt());
        realEstatePost.setUpdateBy(realEstatePostDTO.getUpdateBy());
        realEstatePost.setView(realEstatePostDTO.getView());
        realEstatePost.setWard(modelMapper.map(realEstatePostDTO.getWard(), Ward.class));
        return realEstatePost;
    }

    private Plot convertToPlotEntity(PlotDTO plotDTO) {
        Plot plot = new Plot();
        plot.setId(plotDTO.getId());
        plot.setBehindWidth(plotDTO.getBehindWidth());
        plot.setFrontWidth(plotDTO.getFrontWidth());
        return plot;
    }

    private Apartment convertToApartmentEntity(ApartmentDTO apartmentDTO) {
        Apartment apartment = new Apartment();
        apartment.setConstruction(apartmentDTO.getConstruction());
        apartment.setFurniture(apartmentDTO.getFurniture());
        apartment.setId(apartmentDTO.getId());
        apartment.setFloorNo(apartmentDTO.getFloorNo());
        apartment.setNoBathroom(apartmentDTO.getNoBathroom());
        apartment.setNoBedroom(apartmentDTO.getNoBedroom());
        apartment.setBalconyDirection(apartmentDTO.getBalconyDirection());
        return apartment;
    }

    private House convertToHouseEntity(HouseDTO houseDTO) {
        House house = new House();
        house.setFrontWidth(houseDTO.getFrontWidth());
        house.setId(houseDTO.getId());
        house.setFurniture(houseDTO.getFurniture());
        house.setBehindWidth(houseDTO.getBehindWidth());
        house.setBalconyDirection(houseDTO.getBalconyDirection());
        house.setNoBathroom(houseDTO.getNoBathroom());
        house.setNoBedroom(houseDTO.getNoBedroom());
        house.setNoFloor(houseDTO.getNoFloor());
        house.setStreetWidth(houseDTO.getStreetWidth());
        return house;
    }

    @GetMapping("/api/v1/real-estate-post/recordsOfUser")
    public ResponseEntity<BaseResponse> findRecordsByUserId(@CurrentUser UserDetailsImpl userDetails) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getNoOfPostsByUserId(userDetails.getId()), "", HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(null, "Xảy ra lỗi khi lấy số lượng bài viết của người dùng", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @GetMapping("/api/v1/real-estate-post/user")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> findByOwnerId(
            @RequestParam Integer page,
            @RequestParam Integer rows,
            @CurrentUser UserDetailsImpl userDetails
    ) {
        try {
            List<RealEstatePost> realEstatePosts = service.findByOwnerId(userDetails.getId(), page, rows);
            return ResponseEntity.ok(new BaseResponse(
                    realEstatePosts.stream().map(e -> modelMapper.map(e, RealEstatePostDTO.class)).collect(Collectors.toList()),
                    "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy danh sách bài đăng của người dùng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/v1/real-estate-post/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> findAll() {
        try {
            return ResponseEntity.ok(new BaseResponse(service.findAll(), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy danh sách bài đăng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/v1/real-estate-post/all/page")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> findAll(@RequestParam Integer first, @RequestParam Integer rows) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.findAll(first, rows), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy danh sách bài đăng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/api/v1/real-estate-post/disable/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> disablePostById(@PathVariable("id") UUID id) {
        try {
            if (!service.existsByIdAndEnable(id)) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng phù hợp.", HttpStatus.NOT_FOUND));
            }
            service.disablePostById(id);
            return ResponseEntity.ok(new BaseResponse(null, "Ẩn bài viết thành công", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi ẩn bài đăng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/api/v1/real-estate-post/enable/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> disableOrEnablePostById(@PathVariable("id") UUID id) {
        try {
            RealEstatePost realEstatePost = service.findById(id);
            if (realEstatePost.isEnable()) {
                realEstatePost.setEnable(false);
                service.update(realEstatePost);
                return ResponseEntity.ok(new BaseResponse(0, "Ẩn bài viết thành công", HttpStatus.OK));
            } else {
                realEstatePost.setEnable(true);
                service.update(realEstatePost);
                return ResponseEntity.ok(new BaseResponse(1, "Hiện bài viết thành công", HttpStatus.OK));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi ẩn / hiện bài đăng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/api/v1/real-estate-post/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> updateStatus(@RequestBody UpdatePostStatusRequest request) {
        try {
            if (!service.existsByIdAndEnable(request.getPostId())) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng phù hợp.", HttpStatus.NOT_FOUND));
            }
            service.updatePostStatus(request.getStatus().toString(), request.getPostId());
            RealEstatePost realEstatePost = service.findById(request.getPostId());
            if (request.getStatus().equals(EStatus.BI_TU_CHOI)) {
                notifyService.notifyAcceptRejectREP(
                        "Bài viết " + realEstatePost.getTitle() + " đã bị quản trị viên từ chối",
                        realEstatePost.getOwnerId().getId(), realEstatePost.getId(), true);
            } else if (request.getStatus().equals(EStatus.DA_KIEM_DUYET)) {
                notifyService.notifyAcceptRejectREP(
                        "Bài viết " + realEstatePost.getTitle() + " đã được quản trị viên chấp nhận",
                        realEstatePost.getOwnerId().getId(), realEstatePost.getId(), false);
                notifyService.notifyAgencyREPUpdate(Message.TAO_REP, realEstatePost.getDistrict().getCode(), realEstatePost.getId(), false);
            } else if (request.getStatus().equals(EStatus.DA_HOAN_THANH)) {

            }
            return ResponseEntity.ok(new BaseResponse(
                    request.getStatus().toString(),
                    "Cập nhật trạng thái bài viết thành công.",
                    HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi ẩn / hiện bài đăng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/api/v1/real-estate-post/complete-status")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> completeStatus(@RequestParam UUID realEstatePostId) {
        try {
            if (!service.existsByIdAndEnable(realEstatePostId)) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng phù hợp.", HttpStatus.NOT_FOUND));
            }
            RealEstatePost realEstatePost = service.findById(realEstatePostId);
            if (!realEstatePost.getStatus().equals(EStatus.DA_KIEM_DUYET)) {
                return ResponseEntity.ok(new BaseResponse(null, "Không thể đánh dấu đã hoàn thành cho bài viết vì bài viết không ở trạng thái đã kiểm duyệt.", HttpStatus.NOT_ACCEPTABLE));
            }
            service.updatePostStatus(EStatus.DA_HOAN_THANH.toString(), realEstatePostId);
            notifyService.thongBaoHoanThanhBaiDang("Bài viết " + realEstatePost.getTitle() + " đã được " + (realEstatePost.isSell() ? "bán" : "cho thuê"), realEstatePostId);
            return ResponseEntity.ok(new BaseResponse(
                    EStatus.DA_HOAN_THANH.toString(),
                    "Cập nhật trạng thái bài viết thành công.",
                    HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi ẩn / hiện bài đăng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/v1/real-estate-post/enable-request")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<BaseResponse> enableRequest(@CurrentUser UserDetailsImpl userDetails) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.enableRequestRep(userDetails.getId()), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài đăng đủ điều kiện nhờ môi giới giúp đỡ.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/v1/real-estate-post/user-requested")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<BaseResponse> userRequested(@CurrentUser UserDetailsImpl userDetails) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.repRequested(userDetails.getId()), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài đăng đã nhờ môi giới giúp đỡ của người dùng.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/api/v1/real-estate-post/agency-requested")
    @PreAuthorize("hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> agencyRequested(@CurrentUser UserDetailsImpl userDetails) {
        try {
            if (!specialAccountService.isAgency(userDetails.getId())) {
                return ResponseEntity.ok(new BaseResponse(null, "Người dùng không phải là môi giới.", HttpStatus.NOT_ACCEPTABLE));
            }
            return ResponseEntity.ok(new BaseResponse(service.requestedOfAgency(userDetails.getId()), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài đăng đã nhờ môi giới giúp đỡ.",
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/api/no-auth/real-estate-post/interested")
    public ResponseEntity<BaseResponse> anonymousInterested(@RequestBody InterestedDTO body) {
        try {
            if (!service.existsByIdAndEnable(body.getRealEstatePostId())) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng phù hợp.", HttpStatus.NOT_FOUND));
            }
            Optional<Interested> interestedOptional = service.findByDeviceIdAndRealEstatePostId(body.getDeviceId(), body.getRealEstatePostId());
            if (interestedOptional.isEmpty()) {
                body.setCreateBy(UUID.fromString("00000000-0000-0000-0000-000000000000"));
                body.setCreateAt(Util.getCurrentDateTime());
                body.setId(0L);
                body.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
                Interested interested = modelMapper.map(body, Interested.class);
                return ResponseEntity.ok(new BaseResponse(
                        modelMapper.map(service.saveInterested(interested), InterestedDTO.class),
                        "", HttpStatus.OK
                ));
            } else {
                service.deleteInterested(interestedOptional.get().getId());
                return ResponseEntity.ok(new BaseResponse(interestedOptional.get().getId(), "DELETED", HttpStatus.OK));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lưu thông tin quan tâm bài đăng.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @PostMapping("/api/v1/real-estate-post/interested")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_USER')")
    public ResponseEntity<BaseResponse> userInterested(@RequestBody InterestedDTO body, @CurrentUser UserDetailsImpl userDetails) {
        try {
            if (!service.existsByIdAndEnable(body.getRealEstatePostId())) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng phù hợp.", HttpStatus.NOT_FOUND));
            }
            Optional<Interested> interestedOptional = service.findByUserIdAndRealEstatePostId(userDetails.getId(), body.getRealEstatePostId());
            if (interestedOptional.isEmpty()) {
                body.setCreateBy(userDetails.getId());
                body.setCreateAt(Util.getCurrentDateTime());
                body.setId(0L);
                body.setUserId(userDetails.getId());
                Interested interested = modelMapper.map(body, Interested.class);
                return ResponseEntity.ok(new BaseResponse(
                        modelMapper.map(service.saveInterested(interested), InterestedDTO.class),
                        "", HttpStatus.OK
                ));
            } else {
                service.deleteInterested(interestedOptional.get().getId());
                return ResponseEntity.ok(new BaseResponse(interestedOptional.get().getId(), "DELETED", HttpStatus.OK));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lưu thông tin quan tâm bài đăng.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/real-estate-post/interested")
    public ResponseEntity<BaseResponse> findByUserIdAndDeviceInfo(@RequestParam("userId") UUID userId, @RequestParam("deviceInfo") String deviceInfo) {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.findListInterestPostsOfUser(userId, deviceInfo), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài đăng đã quan tâm của người dùng.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/real-estate-post/interested/count")
    public ResponseEntity<BaseResponse> countByUserIdAndDeviceInfo(@RequestParam("userId") String userId, @RequestParam("deviceInfo") String deviceInfo) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.countInterested(userId, deviceInfo), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài đăng đã quan tâm của người dùng.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/real-estate-post/contact")
    public ResponseEntity<BaseResponse> findContactOfPost(@RequestParam UUID id) {
        try {
            Object response = service.findContact(id);
            if (response == null) {
                return ResponseEntity.ok(new BaseResponse(
                        null,
                        "Không tìm thấy thông tin liên lạc của bài viết.",
                        HttpStatus.NO_CONTENT
                ));
            }
            return ResponseEntity.ok(new BaseResponse(response, "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin liên lạc của bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/real-estate-post/isInterested")
    public ResponseEntity<BaseResponse> isInterested(@RequestParam("userId") UUID userId,
                                                     @RequestParam("deviceInfo") String deviceInfo,
                                                     @RequestParam("realEstatePostId") UUID realEstatePostId) {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.isInterested(userId, realEstatePostId, deviceInfo), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin người dùng quan tâm của bài viết.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/real-estate-post/detailPageData")
    public ResponseEntity<BaseResponse> detailPageData(@RequestParam("userId") UUID userId,
                                                       @RequestParam("deviceInfo") String deviceInfo,
                                                       @RequestParam("sell") Byte sell,
                                                       @RequestParam("type") String type,
                                                       @RequestParam("limit") Integer limit,
                                                       @RequestParam("offset") Integer offset) {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.detailPageData(sell, type, limit, offset, userId, deviceInfo), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài viết.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/real-estate-post/countTotalBySellAndTypeClient")
    public ResponseEntity<BaseResponse> detailPageData(@RequestParam("sell") Byte sell,
                                                       @RequestParam("type") String type) {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.countTotalBySellAndTypeClient(sell, type), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy số lượng bài viết hợp lệ.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/real-estate-post/interestAndComment")
    public ResponseEntity<BaseResponse> countNoOfInterestAndComment(@RequestParam("postId") UUID postId) {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.countNoOfInterestAndComment(postId), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê của bài viết.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/real-estate-post/mostInterested")
    public ResponseEntity<BaseResponse> getPostsByMostInterested() {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.findByMostInterested(), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài viết được quan tâm nhiều nhất. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/real-estate-post/mostView")
    public ResponseEntity<BaseResponse> getPostsByMostView() {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.findByMostView(), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài viết được xem nhiều nhất. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/real-estate-post/newest")
    public ResponseEntity<BaseResponse> getPostsByNewest() {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.findByNewest(), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài viết được xem nhiều nhất. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @PostMapping("/api/no-auth/real-estate-post/search")
    public ResponseEntity<BaseResponse> search(@RequestBody SearchRequest request) {
        try {
            if (request.getType() != null
                    && !request.getType().equals(EType.HOUSE.toString())
                    && !request.getType().equals(EType.APARTMENT.toString())
                    && !request.getType().equals(EType.PLOT.toString())) {
                return ResponseEntity.ok(new BaseResponse(null, "Kiểu bất động sản không hợp lệ", HttpStatus.NOT_ACCEPTABLE));
            }
            if (request.getStartPrice() != null && request.getEndPrice() != null && request.getStartPrice() > request.getEndPrice()) {
                return ResponseEntity.ok(new BaseResponse(null, "Khoảng giá không hợp lệ", HttpStatus.NOT_ACCEPTABLE));
            }
            if (request.getStartPrice() != null && request.getStartPrice() < 0) {
                return ResponseEntity.ok(new BaseResponse(null, "Giá trị của giá bắt đầu phải lớn hoặc bằng 0", HttpStatus.NOT_ACCEPTABLE));
            }
            if (request.getEndPrice() != null && request.getEndPrice() <= 0) {
                return ResponseEntity.ok(new BaseResponse(null, "Giá trị của giá kết thúc phải lớn hơn 0", HttpStatus.NOT_ACCEPTABLE));
            }
            if (request.getStartArea() != null && request.getEndArea() != null && request.getStartArea() > request.getEndArea()) {
                return ResponseEntity.ok(new BaseResponse(null, "Khoảng diện tích không hợp lệ", HttpStatus.NOT_ACCEPTABLE));
            }
            if (request.getStartArea() != null && request.getStartArea() < 0) {
                return ResponseEntity.ok(new BaseResponse(null, "Giá trị của diện tích bắt đầu phải lớn hơn hoặc bằng 0", HttpStatus.NOT_ACCEPTABLE));
            }
            if (request.getEndArea() != null && request.getEndArea() <= 0) {
                return ResponseEntity.ok(new BaseResponse(null, "Giá trị của diện tích kết thúc phải lớn hơn 0", HttpStatus.NOT_ACCEPTABLE));
            }
            if (request.getLimit() <= 0) {
                return ResponseEntity.ok(new BaseResponse(null, "Giá trị của limit phải lớn hơn hoặc bằng 0", HttpStatus.NOT_ACCEPTABLE));
            }
            if (request.getOffset() < 0) {
                return ResponseEntity.ok(new BaseResponse(null, "Giá trị của offset phải lớn hơn hoặc bằng 0", HttpStatus.NOT_ACCEPTABLE));
            }
            return ResponseEntity.ok(new BaseResponse(service.search(request), "", HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi tìm kiếm bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/chart1")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getChart1Data(@RequestParam Byte sell,
                                                      @RequestParam String type,
                                                      @RequestParam String provinceCode,
                                                      @RequestParam Integer year) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.baiVietChart1(sell, type, provinceCode, year), "", HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/chart2")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getChart2Data(@RequestParam Byte sell,
                                                      @RequestParam String type,
                                                      @RequestParam String provinceCode,
                                                      @RequestParam Integer year) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.baiVietChart2(sell, type, provinceCode, year), "", HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/price-fluctuation")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> getPriceFluctuationStatistic(@RequestParam Byte sell,
                                                                     @RequestParam String type,
                                                                     @RequestParam String provinceCode,
                                                                     @RequestParam String districtCode,
                                                                     @RequestParam String wardCode,
                                                                     @RequestParam Integer month,
                                                                     @RequestParam Integer year) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getPriceFluctuationStatistic(sell, type, provinceCode, districtCode, wardCode, month, year), "", HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê lịch sử biến động giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/most-change-price")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getPriceFluctuationStatistic() {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getLstMostChangePrice(), "", HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê lịch sử biến động giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/price")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> getPriceChartOption(@RequestParam String postId) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getPriceOption(postId), "", HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/view")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> getViewChartOption(@RequestParam String postId,
                                                           @RequestParam Integer month,
                                                           @RequestParam Integer year) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getViewChartOption(postId, month, year), "", HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/comment")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> getCommentChartOption(@RequestParam String postId,
                                                              @RequestParam Integer month,
                                                              @RequestParam Integer year) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getCommentChartOption(postId, month, year), "", HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/interested")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> getInterestedChartOption(@RequestParam String postId,
                                                                 @RequestParam Integer month,
                                                                 @RequestParam Integer year) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getInterestedChartOption(postId, month, year), "", HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/report")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> getReportChartOption(@RequestParam String postId,
                                                             @RequestParam Integer month,
                                                             @RequestParam Integer year) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getReportChartOption(postId, month, year), "", HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/click")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> getClickedViewChartOption(@RequestParam String postId,
                                                             @RequestParam Integer month,
                                                             @RequestParam Integer year) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getClickedViewChartOption(postId, month, year), "", HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/all-of-user")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> getAllPostOfUser(@CurrentUser UserDetailsImpl userDetails) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getAllRealEstatePostOfUser(userDetails.getId()), "", HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài viết của người dùng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/interested/all-user")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> getAllInterestedUsers(@RequestParam UUID postId) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getAllInterestedUsersOfPost(postId), "", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy danh sách người dùng quan tâm bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
