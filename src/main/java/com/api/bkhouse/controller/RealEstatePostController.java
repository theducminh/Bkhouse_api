package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.geom.Point;


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
import com.api.bkhouse.payload.response.chart.ChartOption;
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
    private final RealEstatePostService service;

    private final PlotService plotService;

    private final HouseService houseService;

    private final ApartmentService apartmentService;

    private final ModelMapper modelMapper;

    private final PostMediaService postMediaService;

    private final UserService userService;

    private final PostPayService postPayService;

    private final SpecialAccountService specialAccountService;

    private final NotifyService notifyService;

    public RealEstatePostController(
            RealEstatePostService service,
            PlotService plotService,
            HouseService houseService,
            ApartmentService apartmentService,
            ModelMapper modelMapper,
            PostMediaService postMediaService,
            UserService userService,
            PostPayService postPayService,
            SpecialAccountService specialAccountService,
            NotifyService notifyService) {
        this.service = service;
        this.plotService = plotService;
        this.houseService = houseService;
        this.apartmentService = apartmentService;
        this.modelMapper = modelMapper;
        this.postMediaService = postMediaService;
        this.userService = userService;
        this.postPayService = postPayService;
        this.specialAccountService = specialAccountService;
        this.notifyService = notifyService;
    }

    private static final Logger logger = LoggerFactory.getLogger(RealEstatePostController.class);
    private static final UUID ANONYMOUS_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @GetMapping("/api/no-auth/real-estate-post/{id}")
    public ResponseEntity<BaseResponse> findById(@PathVariable("id") UUID id) {
        try {
            // 🚨 SỬA CHÍ MẠNG: Lấy bài viết từ DB lên trước để đọc Type, KHÔNG split chuỗi UUID nữa
            // Giả sử bác đang dùng repository hoặc service. Dưới đây em gọi tạm service.findById(id), 
            // bác chỉnh lại hàm findById theo đúng tên hàm trong Service của bác nhé!
            RealEstatePost optPost = service.findById(id); 
            if (optPost == null || !optPost.getEnable()) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng phù hợp. 1111", HttpStatus.NOT_FOUND));
            }

            RealEstatePost mainPost = optPost;
            EType type = mainPost.getType(); // Lấy Type chuẩn (VD: "HOUSE", "APARTMENT")

            BasePost basePost = null;
            if (type == EType.PLOT) {
                Plot plot = plotService.findByRealEstatePostId(id);
                if (plot != null) {
                    basePost = modelMapper.map(plot, PlotDTO.class);
                }
            } else if (type == EType.APARTMENT) {
                Apartment apartment = apartmentService.findByRealEstatePostId(id);
                if (apartment != null) {
                    basePost = modelMapper.map(apartment, ApartmentDTO.class);
                }
            } else if (type == EType.HOUSE) {
                House house = houseService.findByRealEstatePostId(id);
                if (house != null) {
                    basePost = modelMapper.map(house, HouseDTO.class);
                }
            }
            
            if (basePost == null) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng phù hợp. 2222", HttpStatus.NOT_FOUND));
            }

            // 🌟 ÉP TỌA ĐỘ BẢN ĐỒ (Giống API trước, nếu không có cái này Leaflet sẽ lỗi null Location)
            if (basePost.getRealEstatePost() != null && mainPost != null) {
                // 1. Ép thủ công Tọa độ (Location)
                if (mainPost.getLocation() != null) {
                    basePost.getRealEstatePost().setLocation(mainPost.getLocation().toText());
                }

                // 2. ✨ Ép thủ công Enable và Period để hết bị Null ở Frontend
                basePost.getRealEstatePost().setEnable(mainPost.getEnable());
                basePost.getRealEstatePost().setPeriod(mainPost.getPeriod());
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
        // 1. Tìm kiếm và kiểm tra thực thể bài viết trong bảng chính trước
        RealEstatePost postOptional = service.findById(id); // Sử dụng hàm findById có sẵn của JpaRepository
        if (postOptional == null || !postOptional.getEnable()) {
            return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng phù hợp. 1", HttpStatus.NOT_FOUND));
        }
        
        RealEstatePost mainPost = postOptional;
        // Lấy chuẩn dữ liệu Enum loại hình (EType) từ bản ghi thực tế dưới Supabase
        EType postType = mainPost.getType(); 

        BasePost basePost = null;

        // 2. Nhảy vào đúng nhánh bảng phụ dựa trên loại hình thực tế của bài đăng
        if (postType == EType.PLOT) {
            Plot plot = plotService.findByRealEstatePostId(id);
            if (plot != null) {
                basePost = modelMapper.map(plot, PlotDTO.class);
            }
        } else if (postType == EType.APARTMENT) {
            Apartment apartment = apartmentService.findByRealEstatePostId(id);
            if (apartment != null) {
                basePost = modelMapper.map(apartment, ApartmentDTO.class);
            }
        } else if (postType == EType.HOUSE) {
            House house = houseService.findByRealEstatePostId(id);
            if (house != null) {
                basePost = modelMapper.map(house, HouseDTO.class);
            }
        }

       if (basePost != null && basePost.getRealEstatePost() != null && mainPost.getLocation() != null) {
            // Lấy đối tượng Point từ entity gốc vừa query lên
            org.locationtech.jts.geom.Point point = mainPost.getLocation();
            
            // Hàm toText() sẽ tự động xuất ra chuỗi chuẩn WKT dạng "POINT (105.8 21.0)"
            basePost.getRealEstatePost().setLocation(point.toText());
        }

        if (basePost == null) {
            return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng phù hợp. 2", HttpStatus.NOT_FOUND));
        }

        // 3. Tiến hành lấy danh sách ảnh và cộng lượt xem
        List<PostMedia> postMedia = postMediaService.findByPostId(id);
        service.updateView(id);

        return ResponseEntity.ok(new BaseResponse(
                new RealEstatePostResponse(
                        basePost,
                        postMedia.stream().map(e -> modelMapper.map(e, PostMediaDTO.class)).collect(Collectors.toList())),
                "",
                HttpStatus.OK));

    } catch (Exception e) {
        logger.error("Lỗi khi findByIdWithIncreaseView: ", e);
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
            logger.error("Lỗi click info: ", e);
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
            boolean isFree = agencyPeriodPriority(user, request.getRealEstatePost().getDistrict().getCode());

            if (!isFree) {
                int pay = Util.calculatePostPrice(realEstatePostDTO.getPriority(), realEstatePostDTO.getPeriod(), realEstatePostDTO.getSell());
                //in ra log pay và số dư để dễ debug
                logger.info("Tính phí đăng bài: " + pay);
                logger.info("Số dư trong tài khoản: " + user.getAccountBalance());
                if (user.getAccountBalance() == null || user.getAccountBalance() < pay) {
                    return ResponseEntity.ok(new BaseResponse(null,
                            "Số dư trong tài khoản không đủ để thực hiện giao dịch.",
                            HttpStatus.INTERNAL_SERVER_ERROR));
                }
            }
            realEstatePostDTO.setCreatedAt(Util.getCurrentDateTime());
            realEstatePostDTO.setCreatedBy(userDetails.getId());
            realEstatePostDTO.setEnable(false);
            realEstatePostDTO.setViewCount(0);
            realEstatePostDTO.setContactCount(0);
            realEstatePostDTO.setStatus(EStatus.PENDING);
            RealEstatePost realEstatePost = modelMapper.map(realEstatePostDTO, RealEstatePost.class);
            if (realEstatePostDTO.getLocation() != null && !realEstatePostDTO.getLocation().isBlank()) {
    try {
        // Bóc tách chuỗi chữ dạng "POINT(105.83922958387122 21.006140895881412)"
        String wkt = realEstatePostDTO.getLocation();
        
        // Sử dụng WKTReader (Well-Known Text) của thư viện org.locationtech.jts để dịch chuỗi hình học
        org.locationtech.jts.io.WKTReader wktReader = new org.locationtech.jts.io.WKTReader();
        org.locationtech.jts.geom.Point spatialPoint = (org.locationtech.jts.geom.Point) wktReader.read(wkt);
        
        // Thiết lập hệ số SRID 4326 (Hệ GPS chuẩn toàn cầu toàn diện cho Supabase)
        spatialPoint.setSRID(4326);
        
        // Ép thực thể mang đối tượng hình học này đi lưu trữ
        realEstatePost.setLocation(spatialPoint);
        
        logger.info("🎯 Map tọa độ không gian thành công sang Entity JTS Point!");
    } catch (Exception ex) {
        logger.error("Lỗi parse chuỗi tọa độ WKT sang JTS Point: ", ex);
    }
}
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
            if (!isFree) {
                int pay = Util.calculatePostPrice(realEstatePostDTO.getPriority(), realEstatePostDTO.getPeriod(), realEstatePostDTO.getSell());
                if (pay > 0) {
                    PostPay postPay = new PostPay();
                    postPay.setId(null);
                    postPay.setUser(user);
                    postPay.setRealEstatePost(realEstatePost);
                    postPay.setPrice(pay);
                    postPay.setContent(PayContent.POST_PAY);
                    
                    // Lấy accountBalance ra tính toán an toàn
                    Long currentBalance = (user.getAccountBalance() != null) ? user.getAccountBalance() : 0L;
                    postPay.setAccountBalance(currentBalance - pay);
                    postPay.setCreateAt(Util.getCurrentDateTime());
                    this.postPayService.createPostPay(postPay);

                    user.setAccountBalance(currentBalance - pay);
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
            logger.error("Lỗi khi tạo bài đăng BĐS: ", e);
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
            realEstatePostDTO.setUpdatedAt(Util.getCurrentDateTime());
            realEstatePostDTO.setCreatedBy(userDetails.getId());

            // ==============================================================================
            // 🌟 BƯỚC 1: CẤT GIẤU CHUỖI TỌA ĐỘ (Né lỗi 'Invalid hex digit' của ModelMapper)
            // ==============================================================================
            String wktLocation = realEstatePostDTO.getLocation();
            realEstatePostDTO.setLocation(null); // Gán null để convertToEntity không bị văng lỗi

            // Chuyển đổi DTO sang Entity an toàn
            RealEstatePost realEstatePost = convertToEntity(realEstatePostDTO);

            // ==============================================================================
            // 🌟 BƯỚC 2: TỰ TAY DỊCH TỌA ĐỘ VÀ GẮN LẠI VÀO ENTITY NHƯ MẪU TẠO BÀI
            // ==============================================================================
            if (wktLocation != null && !wktLocation.isBlank()) {
                try {
                    org.locationtech.jts.io.WKTReader wktReader = new org.locationtech.jts.io.WKTReader();
                    org.locationtech.jts.geom.Point spatialPoint = (org.locationtech.jts.geom.Point) wktReader.read(wktLocation);
                    spatialPoint.setSRID(4326); // Chuẩn GPS Supabase
                    realEstatePost.setLocation(spatialPoint);
                    logger.info("🎯 Map tọa độ cập nhật thành công sang Entity JTS Point!");
                } catch (Exception ex) {
                    logger.error("Lỗi parse chuỗi tọa độ WKT sang JTS Point khi cập nhật: ", ex);
                }
            }

            // Gọi service lưu bài viết
            service.update(realEstatePost);

            // Cập nhật các bảng con (Nhà/Đất/Chung cư)
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

            // Xử lý lưu ảnh
            List<PostMediaDTO> postMediaDTOS = request.getImages();
            if (postMediaDTOS != null && !postMediaDTOS.isEmpty()) {
                for (PostMediaDTO postMediaDTO : postMediaDTOS) {
                    postMediaService.save(modelMapper.map(postMediaDTO, PostMedia.class));
                }
            }

            // Cập nhật giá và gửi thông báo nếu giá thay đổi
            if (currPrice != null && request.getRealEstatePost().getPrice() != null 
                && !currPrice.equals(request.getRealEstatePost().getPrice())) {
                service.createRepPrice(request.getRealEstatePost().getPrice(), request.getRealEstatePost().getId(), userDetails.getId());
                notifyService.notifyAgencyREPUpdate(Message.CAP_NHAT_REP, realEstatePostDTO.getDistrict().getCode(), realEstatePostDTO.getId(), true);
                notifyService.notifyInterested(Message.getCAP_NHAT_REP_INTERESTED(realEstatePostDTO.getTitle()), realEstatePostDTO.getId());
            }

            return ResponseEntity.ok(new
                    BaseResponse(realEstatePost.getId(),
                    "Đã cập nhật bài viết thành công.",
                    HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi cập nhật bài đăng: ", e);
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi cập nhật bài đăng " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private RealEstatePost convertToEntity(RealEstatePostDTO realEstatePostDTO) {
        RealEstatePost realEstatePost = new RealEstatePost();

        if (realEstatePostDTO.getLocation() != null && !realEstatePostDTO.getLocation().isEmpty()) {
    try {
        // Bắt buộc phải bỏ vào trong try
        byte[] wkbBytes = WKBReader.hexToBytes(realEstatePostDTO.getLocation());
        WKBReader reader = new WKBReader();
        
        // Dòng số 342 của bác đang nằm ở đây
        Point location = (Point) reader.read(wkbBytes); 
        
        realEstatePost.setLocation(location);
        
    } catch (ParseException e) {
        // Bắt lỗi ở đây để rủi chuỗi Hex bị lỗi thì server không bị sập (trả về lỗi 500)
        logger.error("Lỗi giải mã tọa độ JTS từ Frontend gửi lên: ", e);
        // (Nếu bác không cấu hình logger thì dùng System.out.println cũng được)
    }
}
        
        realEstatePost.setArea(realEstatePostDTO.getArea());
        realEstatePost.setAddressShow(realEstatePostDTO.getAddressShow());
        realEstatePost.setDescription(realEstatePostDTO.getDescription());
        realEstatePost.setContactCount(realEstatePostDTO.getContactCount());
        realEstatePost.setEnable(realEstatePostDTO.getEnable());
        if (realEstatePostDTO.getDistrict() != null) {
            realEstatePost.setDistrict(modelMapper.map(realEstatePostDTO.getDistrict(), District.class));
        }
        realEstatePost.setDirection(realEstatePostDTO.getDirection());
        realEstatePost.setPrice(realEstatePostDTO.getPrice());
        realEstatePost.setCreatedAt(realEstatePostDTO.getCreatedAt());
        realEstatePost.setCreatedBy(realEstatePostDTO.getCreatedBy());
        realEstatePost.setId(realEstatePostDTO.getId());
        if (realEstatePostDTO.getOwnerId() != null) {
            realEstatePost.setOwnerId(modelMapper.map(realEstatePostDTO.getOwnerId(), User.class));
        }
        realEstatePost.setPriority(realEstatePostDTO.getPriority());
        if (realEstatePostDTO.getProvince() != null) {
            realEstatePost.setProvince(modelMapper.map(realEstatePostDTO.getProvince(), Province.class));
        }
        realEstatePost.setSell(realEstatePostDTO.getSell());
        realEstatePost.setStatus(realEstatePostDTO.getStatus());
        realEstatePost.setStreet(realEstatePostDTO.getStreet());
        realEstatePost.setTitle(realEstatePostDTO.getTitle());
        realEstatePost.setType(realEstatePostDTO.getType());
        realEstatePost.setUpdatedAt(realEstatePostDTO.getUpdatedAt());
        realEstatePost.setViewCount(realEstatePostDTO.getViewCount());
        
        return realEstatePost;
    }

    private Plot convertToPlotEntity(PlotDTO plotDTO) {
        if (plotDTO == null) return null;
        Plot plot = new Plot();
        plot.setId(plotDTO.getId());
        plot.setBehindWidth(plotDTO.getBehindWidth());
        plot.setFrontWidth(plotDTO.getFrontWidth());
        return plot;
    }

    private Apartment convertToApartmentEntity(ApartmentDTO apartmentDTO) {
        if (apartmentDTO == null) return null;
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
        if (houseDTO == null) return null;
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
            logger.error("Lỗi đếm số bài của user: ", e);
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
            logger.error("Lỗi lấy danh sách bài đăng của user: ", e);
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
            logger.error("Lỗi lấy danh sách bài đăng: ", e);
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
            logger.error("Lỗi lấy danh sách bài đăng phân trang: ", e);
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
            logger.error("Lỗi ẩn bài đăng: ", e);
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi ẩn bài đăng. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/api/v1/real-estate-post/enable/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> disableOrEnablePostById(@PathVariable("id") UUID id) {
        try {
            // 🌟 CHUẨN HÓA CÚ PHÁP OPTIONAL
            RealEstatePost optPost = service.findById(id);
            if (optPost == null) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng.", HttpStatus.NOT_FOUND));
            }
            RealEstatePost realEstatePost = optPost;

            if (realEstatePost.getEnable()) {
                realEstatePost.setEnable(false);
                service.update(realEstatePost);
                return ResponseEntity.ok(new BaseResponse(0, "Ẩn bài viết thành công", HttpStatus.OK));
            } else {
                realEstatePost.setEnable(true);
                service.update(realEstatePost);
                return ResponseEntity.ok(new BaseResponse(1, "Hiện bài viết thành công", HttpStatus.OK));
            }
        } catch (Exception e) {
            logger.error("Lỗi đổi trạng thái bài đăng: ", e);
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
            
            // Cập nhật trạng thái xuống DB
            service.updatePostStatus(request.getStatus().toString(), request.getPostId());
            
            // 🌟 CHUẨN HÓA CÚ PHÁP OPTIONAL (Sửa lại đoạn gán thẳng bị lỗi)
            RealEstatePost optPost = service.findById(request.getPostId());
            if (optPost == null) {
                return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy bài đăng.", HttpStatus.NOT_FOUND));
            }
            RealEstatePost realEstatePost = optPost;

            // Gửi thông báo
            if (request.getStatus().equals(EStatus.REJECTED)) {
                notifyService.notifyAcceptRejectREP(
                        "Bài viết " + realEstatePost.getTitle() + " đã bị quản trị viên từ chối",
                        realEstatePost.getOwnerId().getId(), realEstatePost.getId(), true);
                        
            } else if (request.getStatus().equals(EStatus.APPROVED)) {
                notifyService.notifyAcceptRejectREP(
                        "Bài viết " + realEstatePost.getTitle() + " đã được quản trị viên chấp nhận",
                        realEstatePost.getOwnerId().getId(), realEstatePost.getId(), false);
                notifyService.notifyAgencyREPUpdate(Message.TAO_REP, realEstatePost.getDistrict().getCode(), realEstatePost.getId(), false);
            } 

            return ResponseEntity.ok(new BaseResponse(
                    request.getStatus().toString(),
                    "Cập nhật trạng thái bài viết thành công.",
                    HttpStatus.OK
            ));
        } catch (Exception e) {
            logger.error("Lỗi cập nhật status bài đăng: ", e);
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi cập nhật trạng thái bài đăng. " + e.getMessage(),
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
            if (!realEstatePost.getStatus().equals(EStatus.APPROVED)) {
                return ResponseEntity.ok(new BaseResponse(null, "Không thể đánh dấu đã hoàn thành cho bài viết vì bài viết không ở trạng thái đã kiểm duyệt.", HttpStatus.NOT_ACCEPTABLE));
            }
            service.updatePostStatus(EStatus.COMPLETED.toString(), realEstatePostId);
            notifyService.thongBaoHoanThanhBaiDang("Bài viết " + realEstatePost.getTitle() + " đã được " + (realEstatePost.getSell() ? "bán" : "cho thuê"), realEstatePostId);
            return ResponseEntity.ok(new BaseResponse(
                    EStatus.COMPLETED.toString(),
                    "Cập nhật trạng thái bài viết thành công.",
                    HttpStatus.OK
            ));
        } catch (Exception e) {
            logger.error("Lỗi đánh dấu đã hoàn thành bài đăng: ", e);
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
            logger.error("Lỗi khi enableRequest: ", e);
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
            logger.error("Lỗi userRequested: ", e);
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
            logger.error("Lỗi agencyRequested: ", e);
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
            Optional<Interested> interestedOptional = service.findByDeviceInfoAndRealEstatePostId(body.getDeviceInfo(), body.getRealEstatePostId());
            if (interestedOptional.isEmpty()) {
                body.setCreateBy(ANONYMOUS_USER_ID);
                body.setCreateAt(Util.getCurrentDateTime());
                body.setId(null);
                body.setUserId(ANONYMOUS_USER_ID);
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
            logger.error("Lỗi anonymousInterested: ", e);
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
                body.setId(null);
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
            logger.error("Lỗi userInterested: ", e);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lưu thông tin quan tâm bài đăng.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/real-estate-post/interested")
    public ResponseEntity<BaseResponse> findByUserIdAndDeviceInfo(@RequestParam( value = "userId", required = false) UUID userId, @RequestParam("deviceInfo") String deviceInfo) {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.findListInterestPostsOfUser(userId, deviceInfo), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            logger.error("Lỗi findByUserIdAndDeviceInfo: ", e);
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
            logger.error("Lỗi đếm số bài viết quan tâm: ", e);
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
            //in ra log id để dễ debug
            logger.info("Lấy thông tin liên hệ của bài viết với id: " + id + " - " + "kiểu dữ liệu của id: " + id.getClass().getName());
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
            logger.error("Lỗi lấy liên hệ bài đăng: ", e);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin liên lạc của bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/real-estate-post/isInterested")
    public ResponseEntity<BaseResponse> isInterested(@RequestParam(value = "userId", required = false) UUID userId,
                                                     @RequestParam("deviceInfo") String deviceInfo,
                                                     @RequestParam("realEstatePostId") UUID realEstatePostId) {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.isInterested(userId, realEstatePostId, deviceInfo), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            logger.error("Lỗi kiểm tra isInterested: ", e);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin người dùng quan tâm của bài viết.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/real-estate-post/detailPageData")
    public ResponseEntity<BaseResponse> detailPageData(@RequestParam(value = "userId", required = false) UUID userId,
                                                       @RequestParam("deviceInfo") String deviceInfo,
                                                       @RequestParam("sell") Boolean sell,
                                                       @RequestParam("type") String type,
                                                       @RequestParam("limit") Integer limit,
                                                       @RequestParam("offset") Integer offset) {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.detailPageData(sell, type, limit, offset, userId, deviceInfo), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            logger.error("Lỗi lấy data chi tiết: ", e);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy danh sách bài viết.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/no-auth/real-estate-post/countTotalBySellAndTypeClient")
    public ResponseEntity<BaseResponse> detailPageData(@RequestParam("sell") Boolean sell,
                                                       @RequestParam("type") String type) {
        try {
            return ResponseEntity.ok(new BaseResponse(
                    service.countTotalBySellAndTypeClient(sell, type), "", HttpStatus.OK
            ));
        } catch (Exception e) {
            logger.error("Lỗi đếm số lượng post theo Sell và Type: ", e);
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
            logger.error("Lỗi đếm quan tâm và bình luận: ", e);
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
            logger.error("Lỗi lấy post quan tâm nhất: ", e);
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
            logger.error("Lỗi lấy post view nhiều nhất: ", e);
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
            logger.error("Lỗi lấy post mới nhất: ", e);
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
            logger.error("Lỗi khi tìm kiếm bài đăng: ", e);
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
            logger.error("Lỗi lấy thống kê chart1: ", e);
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
            logger.error("Lỗi lấy thống kê chart2: ", e);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/price-fluctuation")
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
public ResponseEntity<?> getPriceFluctuationStatistic(
        @RequestParam(required = false) Byte sell,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String provinceCode,
        @RequestParam(required = false) String districtCode,
        @RequestParam(required = false, defaultValue = "0") Integer month,
        @RequestParam(required = false) Integer year) {
    try {
        // Chuyển đổi an toàn từ Byte của Frontend cũ sang Boolean cho Service mới
        Boolean isSell = (sell != null && sell == 1);
        
        ChartOption result = service.getPriceFluctuationStatistic(isSell, type, provinceCode, districtCode, month, year);
        
        // Dùng .body() để trị dứt điểm lỗi báo đỏ ResponseEntity của IDE
        return ResponseEntity.ok().body(new BaseResponse(result, "", HttpStatus.OK));
        
    } catch (Exception e) {
        logger.error("Lỗi lấy biến động giá: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            new BaseResponse(
                null,
                "Đã xảy ra lỗi khi lấy thông tin thống kê lịch sử biến động giá. " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        );
    }
}

    @GetMapping("/api/v1/real-estate-post/statistic/most-change-price")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> getPriceFluctuationStatistic() {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getLstMostChangePrice(), "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi lấy bài đăng đổi giá nhiều nhất: ", e);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê lịch sử biến động giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/price")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> getPriceChartOption(@RequestParam UUID postId) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getPriceOption(postId), "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi lấy chart giá: ", e);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/view")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> getViewChartOption(@RequestParam UUID postId,
                                                           @RequestParam Integer month,
                                                           @RequestParam Integer year) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getViewChartOption(postId, month, year), "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi lấy chart view: ", e);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/comment")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> getCommentChartOption(@RequestParam UUID postId,
                                                              @RequestParam Integer month,
                                                              @RequestParam Integer year) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getCommentChartOption(postId, month, year), "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi lấy chart comment: ", e);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/interested")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> getInterestedChartOption(@RequestParam UUID postId,
                                                                 @RequestParam Integer month,
                                                                 @RequestParam Integer year) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getInterestedChartOption(postId, month, year), "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi lấy chart quan tâm: ", e);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/report")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> getReportChartOption(@RequestParam UUID postId,
                                                             @RequestParam Integer month,
                                                             @RequestParam Integer year) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getReportChartOption(postId, month, year), "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi lấy chart report: ", e);
            return ResponseEntity.ok(new BaseResponse(
                    null,
                    "Đã xảy ra lỗi khi lấy thông tin thống kê giá. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            ));
        }
    }

    @GetMapping("/api/v1/real-estate-post/statistic/click")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_AGENCY')")
    public ResponseEntity<BaseResponse> getClickedViewChartOption(@RequestParam UUID postId,
                                                             @RequestParam Integer month,
                                                             @RequestParam Integer year) {
        try {
            return ResponseEntity.ok(new BaseResponse(service.getClickedViewChartOption(postId, month, year), "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi lấy chart click liên hệ: ", e);
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
            logger.error("Lỗi lấy danh sách bài viết theo User: ", e);
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
            logger.error("Lỗi lấy danh sách user quan tâm bài viết: ", e);
            return ResponseEntity.ok(new BaseResponse(null,
                    "Đã xảy ra lỗi khi lấy danh sách người dùng quan tâm bài viết. " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
