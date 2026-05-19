package com.api.bkhouse.service;

import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.api.bkhouse.constant.enumeric.EType;
import com.api.bkhouse.entity.*;
import com.api.bkhouse.entity.response.*;
import com.api.bkhouse.payload.request.SearchRequest;
import com.api.bkhouse.payload.response.CountInterestAndCommentResponse;
import com.api.bkhouse.payload.response.PaymentStatisticResponse;
import com.api.bkhouse.payload.response.RepDetailPageResponse;
import com.api.bkhouse.payload.response.chart.ChartOption;
import com.api.bkhouse.payload.response.chart.Series;
import com.api.bkhouse.repository.*;
import com.api.bkhouse.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RealEstatePostService {
    private final RealEstatePostRepository repository;
    private final RealEstatePostPriceRepository realEstatePostPriceRepository;
    private final InterestedRepository interestedRepository;
    private final PostMediaRepository postMediaRepository;
    private final PostViewRepository postViewRepository;
    private final ClickedInfoViewRepository clickedInfoViewRepository;
    private final PostCommentRepository postCommentRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final AreaPriceBenchmarkRepository areaPriceBenchmarkRepository;
    private final DistrictRepository districtRepository;
    

    public RealEstatePostService(RealEstatePostRepository repository,
                                RealEstatePostPriceRepository realEstatePostPriceRepository,
                                InterestedRepository interestedRepository,
                                PostMediaRepository postMediaRepository,
                                PostViewRepository postViewRepository,
                                ClickedInfoViewRepository clickedInfoViewRepository,
                                PostCommentRepository postCommentRepository,
                                NamedParameterJdbcTemplate jdbcTemplate,
                                AreaPriceBenchmarkRepository areaPriceBenchmarkRepository,
                                DistrictRepository districtRepository
                                ) {
        this.repository = repository;
        this.realEstatePostPriceRepository = realEstatePostPriceRepository;
        this.interestedRepository = interestedRepository;
        this.postMediaRepository = postMediaRepository;
        this.postViewRepository = postViewRepository;
        this.clickedInfoViewRepository = clickedInfoViewRepository;
        this.postCommentRepository = postCommentRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.areaPriceBenchmarkRepository = areaPriceBenchmarkRepository;
        this.districtRepository = districtRepository;
    }

    private Logger logger = LoggerFactory.getLogger(RealEstatePostService.class);

    public RealEstatePost findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public List<RealEstatePost> findByOwnerId(UUID ownerId, Integer page, Integer rows) {
        return repository.findByOwnerId(ownerId, rows, page*rows);
    }

    public Integer getNoOfPostsByUserId(UUID ownerId) {
        return repository.getNoOfRecords(ownerId);
    }

    @Transactional
    public RealEstatePost create(RealEstatePost realEstatePost) {
       try {
            Map<String, Object> meta = realEstatePost.getMetadata();
            if (meta == null) {
                meta = new HashMap<>(); // Nếu chưa có thì khởi tạo Map mới
            }

            // Do Entity không lưu Period, ta set mặc định bài viết có hạn 30 ngày
            // (Nếu muốn chính xác, bác có thể truyền period từ DTO xuống hàm này)
            int periodDays = 30; 
            LocalDateTime expiredAt = LocalDateTime.now().plusDays(periodDays);
            
            // Format ngày tháng chuẩn ISO-8601 để Postgres hiểu
            String expiredAtStr = expiredAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            meta.put("expired_at", expiredAtStr);
            
            // Set ngược lại vào Entity
            realEstatePost.setMetadata(meta);
        } catch (Exception e) {
            logger.error("Lỗi khi tạo metadata lúc tạo bài: ", e);
        }

        RealEstatePost saved = repository.save(realEstatePost);
        RealEstatePostPrice realEstatePostPrice = new RealEstatePostPrice();
        realEstatePostPrice.setId(null);
        realEstatePostPrice.setPrice(saved.getPrice());
        realEstatePostPrice.setRealEstatePost(saved);
        realEstatePostPrice.setCreateBy(saved.getCreatedBy());
        realEstatePostPrice.setCreateAt(Util.getCurrentDateTime());
        realEstatePostPriceRepository.save(realEstatePostPrice);
        return saved;
    }

    @Transactional
    public void createRepPrice(Double price, UUID repId, UUID userId) {
        RealEstatePostPrice realEstatePostPrice = new RealEstatePostPrice();
        realEstatePostPrice.setId(null);
        realEstatePostPrice.setRealEstatePost(findByIdAndEnable(repId));
        realEstatePostPrice.setCreateAt(Util.getCurrentDateTime());
        realEstatePostPrice.setCreateBy(userId);
        realEstatePostPrice.setPrice(price);
        realEstatePostPriceRepository.save(realEstatePostPrice);
    }

    @Transactional
    public RealEstatePost update(RealEstatePost realEstatePost) {
        try {
            Map<String, Object> meta = realEstatePost.getMetadata();
            if (meta == null) {
                meta = new HashMap<>();
            }

            // Đánh dấu thời gian bài viết vừa bị update
            meta.put("last_updated_by_user", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            realEstatePost.setMetadata(meta);
        } catch (Exception e) {
            logger.error("Lỗi cập nhật metadata khi update bài: ", e);
        }
        return repository.save(realEstatePost);
    }

    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Transactional
    public void disablePostExpire() {
        try {
            repository.disablePostExpire();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Transactional
    public void disablePostById(UUID id) {
        repository.disablePostById(id);
    }

    public List<IRepAdmin> findAll() {
        return repository.findAllByAdmin();
    }

    public List<IRepAdmin> findAll(Integer first, Integer rows) {
        return repository.findAllByAdminPageable(rows, first);
    }

    @Transactional
    public void updatePostStatus(String status, UUID id) {
        repository.updateStatus(status, id);
    }

    @Transactional
    public void updateView(UUID realEstatePostId) {
        repository.updateView(realEstatePostId);
        PostView postView= new PostView();
        postView.setId(null);
        postView.setRealEstatePostId(realEstatePostId);
        postViewRepository.save(postView);
    }

    @Transactional
    public void updateClickedView(UUID realEstatePostId) {
        repository.updateClickedView(realEstatePostId);
        ClickedInfoView clickedInfoView = new ClickedInfoView();
        clickedInfoView.setId(null);
        clickedInfoView.setRealEstatePostId(realEstatePostId);
        clickedInfoView.setCreateBy(UUID.randomUUID());
        clickedInfoView.setCreateAt(Util.getCurrentDateTime());
        clickedInfoViewRepository.save(clickedInfoView);
    }

    public RealEstatePost findByIdAndEnable(UUID id) {
        return repository.findByIdAndEnable(id, true).orElse(null);
    }

    public boolean existsByIdAndEnable(UUID id) {
        return repository.existsByIdAndEnable(id, true);
    }

    public List<IRepEnableRequest> enableRequestRep(UUID userId) {
        return repository.enableRequest(userId);
    }

    public List<IRepRequested> repRequested(UUID userId) {
        return repository.repRequested(userId);
    }

    public List<IRepRequested> requestedOfAgency(UUID agencyId) {
        return repository.requestedOfAgency(agencyId);
    }

    @Transactional
    public Interested saveInterested(Interested interested) {
        return interestedRepository.save(interested);
    }

    public List<IPostInterested> findListInterestPostsOfUser(UUID userId, String deviceInfo) {
        if (deviceInfo != null && deviceInfo.length() > 0 && (userId == null || userId.toString().length() == 0)) {
            return interestedRepository.findRepDetailByDeviceInfo(deviceInfo);
        }
        return interestedRepository.findRepDetailByUserId(userId);
    }

    @Transactional
    public void deleteInterested(Long id) {
        interestedRepository.deleteById(id);
    }

    public Optional<Interested> findByDeviceInfoAndRealEstatePostId(String deviceInfo, UUID realEstatePostId) {
        return interestedRepository.findByDeviceInfoAndRealEstatePostId(deviceInfo, realEstatePostId);
    }

    public Optional<Interested> findByUserIdAndRealEstatePostId(UUID userId, UUID realEstatePostId) {
        return interestedRepository.findByUserIdAndRealEstatePostId(userId, realEstatePostId);
    }

    public Object findContact(UUID id) {
        Optional<IEnableUserChat> optional = repository.findContact(id);
        if (optional.isEmpty()) {
            Optional<IEnableUserChat> ownerOptional = repository.findOwnerContact(id);
            return ownerOptional.orElse(null);
        } else {
            return optional.get();
        }
    }

    public boolean isInterested(UUID userId, UUID realEstatePostId, String deviceInfo) {
        if (deviceInfo != null && !deviceInfo.isBlank() && (userId == null || userId.toString().isBlank())) {
            return interestedRepository.existsByDeviceInfoAndRealEstatePostId(deviceInfo, realEstatePostId);
        }

        // 🌟 TRƯỜNG HỢP 2: Khách vãng lai hoàn toàn không có thông tin gì (An toàn cho hệ thống)
        if (userId == null) {
        return false; 
    }

    // 🌟 TRƯỜNG HỢP 3: Thành viên đã đăng nhập thành công (userId chuẩn UUID)
    return interestedRepository.existsByUserIdAndRealEstatePostId(userId, realEstatePostId);
    }

    public Object countInterested(String userId, String deviceInfo) {
        String query = "select count(*) as cnt\n" +
                "from interested i inner join real_estate_posts rep on i.real_estate_post_id = rep.id\n" +
                "where rep.is_enabled = true and i.user_id = :userId ";
        Map<String, Object> params = new HashMap<>();
        if (deviceInfo != null && deviceInfo.length() > 0 && (userId == null || userId.length() == 0)) {
            query += "and i.device_info = :deviceInfo";
            params.put("deviceInfo", deviceInfo);
            params.put("userId", "11111111-1111-1111-111111111111");
        } else {
            params.put("userId", userId);
        }
        Map<String, Object> result = jdbcTemplate.queryForMap(query, new MapSqlParameterSource(params));
        return ((Number) result.get("cnt")).longValue();
    }

    public List<RepDetailPageResponse> detailPageData(Boolean sell, String type, Integer limit, Integer offset, UUID userId, String deviceInfo) {
        List<RepDetailPageResponse> responses = new ArrayList<>();
        List<UUID> repIds = repository.getRepIdDetailPage(sell, type, limit, offset);
        if (repIds.isEmpty()) {
            logger.info("Không tìm thấy bài viết nào với điều kiện sell = {}, type = {}", sell, type);  
            return responses;
        }

        List<RealEstatePost> posts = repository.findAllById(repIds);
        Map<UUID, RealEstatePost> postMap = posts.stream()
                .collect(Collectors.toMap(RealEstatePost::getId, p -> p));

        List<PostMedia> allMedias = postMediaRepository.findByPostIdIn(repIds);
        Map<UUID, List<PostMedia>> mediaMap = allMedias.stream()
                .collect(Collectors.groupingBy(PostMedia::getPostId));

        for (UUID id : repIds) {
            RealEstatePost realEstatePost = postMap.get(id);
            if (realEstatePost != null) {
                RepDetailPageResponse response = new RepDetailPageResponse();
                response.setId(id.toString());
                response.setArea(realEstatePost.getArea());
                response.setAddressShow(realEstatePost.getAddressShow());
                response.setDescription(realEstatePost.getDescription());
                response.setDirection(realEstatePost.getDirection());
                response.setTitle(realEstatePost.getTitle());
                response.setPrice(realEstatePost.getPrice());
                response.setCreateAt(realEstatePost.getCreatedAt());
                response.setEnable(realEstatePost.getEnable());

                if (realEstatePost.getOwnerId() != null) {
                    String middleName = realEstatePost.getOwnerId().getMiddleName() != null 
                                        ? realEstatePost.getOwnerId().getMiddleName() + " " : "";
                    response.setFullName(realEstatePost.getOwnerId().getFirstName() + " "
                            + middleName
                            + realEstatePost.getOwnerId().getLastName()
                    );
                    response.setAvatarUrl(realEstatePost.getOwnerId().getAvatarUrl());
                    response.setPhoneNumber(realEstatePost.getOwnerId().getPhoneNumber());
                }

                // Lấy ảnh ra từ Map đã gom
                List<PostMedia> postMedias = mediaMap.getOrDefault(id, new ArrayList<>());
                if (!postMedias.isEmpty()) {
                    response.setImageUrl(postMedias.get(0).getId().toString());
                }

                // Call này bác có thể tối ưu gom mẻ sau nếu rảnh, hiện tại 1 query này là đủ nhanh
                response.setInterested(isInterested(userId, id, deviceInfo)); 
                
                responses.add(response);
            }
        }
        return responses;
    }

    public Object findByMostInterested() {
        return repository.getLstMostInterested();
    }

    public Object findByMostView() {
        return repository.getLstMostView();
    }

    public Object findByNewest() {
        return repository.getLstNewest();
    }

    public Integer countTotalBySellAndTypeClient(Boolean sell, String type) {
        return repository.countTotalBySellAndTypeClient(sell, type);
    }

    public CountInterestAndCommentResponse countNoOfInterestAndComment(UUID postId) {
        CountInterestAndCommentResponse response = new CountInterestAndCommentResponse();
        response.setNoOfComment(postCommentRepository.countByPostId(postId));
        response.setNoOfInterest(interestedRepository.countByRealEstatePostId(postId));
        return response;
    }

    public Object search(SearchRequest request) {
        String query = "select cast(rep.id as varchar) as id, rep.title, rep.address_show as addressShow, " +
                "rep.price, rep.area, rep.is_sell as sell, rep.created_at as createAt, rep.description, " +
                "concatws(' ', u.first_name, u.middle_name, u.last_name) as fullName, " +
                "u.phone_number as phoneNumber, u.avatar_url as avatarUrl, ";
        if (request.getDeviceInfo() != null && request.getDeviceInfo().length() > 0) {
            query += "(select count(*) > 0 from interested where user_id = :userId and device_info = :deviceInfo and real_estate_post_id = rep.id ) as interested, ";
        } else {
            query += "(select count(*) > 0 from interested where user_id = :userId and real_estate_post_id = rep.id ) as interested, ";
        }
        query += "(SELECT id FROM post_media WHERE post_id = rep.id LIMIT 1) as imageUrl\n" +
                "FROM real_estate_posts rep INNER JOIN users u ON u.id = rep.owner_id ";

        if (request.getType() != null) {
            if (request.getType().equals(EType.APARTMENT.toString())) {
                query += "inner join apartment spc on spc.real_estate_post_id = rep.id ";
            } else if (request.getType().equals(EType.HOUSE.toString())) {
                query += "inner join house spc on spc.real_estate_post_id = rep.id ";
            }
        }
        query += "where u.is_enabled = true " +
                "and rep.is_enabled = true " +
                "and rep.status = 'APPROVED' " +
                "AND (DATE(NOW()) - DATE(rep.created_at)) <= rep.period ";
        if (request.getSell() != null) {
            if (request.getSell()) {
                query += "and rep.is_sell = true";
            } else {
                query += "and rep.is_sell = false"; 
            }
        }
        if (request.getType() != null) {
            query += "AND rep.type = '" + request.getType().replaceAll("\\s+", "") + "' ";
            if (!request.getType().equals(EType.PLOT.toString()) && request.getNoOfBedrooms() != null && request.getNoOfBedrooms().length > 0) {
                int index = 0;
                for (Integer noOfBedroom : request.getNoOfBedrooms()) {
                    if (index == 0) {
                        query += noOfBedroom > 5 ? "AND ( spc.no_bedroom >= " + noOfBedroom + " " : "AND ( spc.no_bedroom = " + noOfBedroom + " ";
                    } else {
                        query += noOfBedroom > 5 ? "OR spc.no_bedroom >= " + noOfBedroom + " " : "OR spc.no_bedroom = " + noOfBedroom + " ";
                    }
                    index++;
                }
                query += " ) ";
            }
        }

        if (request.getProvinceCode() != null) {
            query += "and rep.province_code = '" + request.getProvinceCode().replaceAll("\\s+", "") + "' ";
        }
        if (request.getDistrictCode() != null && request.getDistrictCode().length > 0) {
            int index = 0;
            for (String districtCode : request.getDistrictCode()) {
                if (index == 0) {
                    query += "AND ( rep.district_code = '" + districtCode.replaceAll("\\s+", "") + "' ";
                } else {
                    query += "OR rep.district_code = '" + districtCode.replaceAll("\\s+", "") + "' ";
                }
                index++;
            }
            query += " ) ";
        }
        
        if (request.getStartPrice() != null) {
            if (request.getEndPrice() != null) {
                query += "and rep.price >= " + request.getStartPrice() + " and rep.price <= " + request.getEndPrice() + " ";
            } else {
                query += "and rep.price >= " + request.getStartPrice() + " ";
            }
        } else {
            if (request.getEndPrice() != null) {
                query += "and rep.price <= " + request.getEndPrice() + " ";
            }
        }
        if (request.getStartArea() != null) {
            if (request.getEndArea() != null) {
                query += "and rep.area >= " + request.getStartArea() + " and rep.area <= " + request.getEndArea() + " ";
            } else {
                query += "and rep.area >= " + request.getStartArea() + " ";
            }
        } else {
            if (request.getEndArea() != null) {
                query += "and rep.area <= " + request.getEndArea() + " ";
            }
        }
        if (request.getDirection() != null && request.getDirection().length > 0) {
            int index = 0;
            for (String direction: request.getDirection()) {
                if (index == 0) {
                    query += "and ( rep.direction = '" + direction.replaceAll("\\s+", "") + "' ";
                } else {
                    query += "or rep.direction = '" + direction.replaceAll("\\s+", "") + "' ";
                }
                index ++;
            }
            query += " ) ";
        }
        if (request.getKeyword() != null && request.getKeyword().replaceAll("\\s", "").length() > 0) {
            String[] words = request.getKeyword().split("\\s+");
            int noOfWords = 0;
            for (String item: words) {
                String word = item.toUpperCase();
                if (noOfWords == 0) {
                    query += "and (UPPER(rep.title) like '%" + word + "%' or UPPER(rep.description) like '%" + word + "%' ";
                } else {
                    query += "or UPPER(rep.title) like '%" + word + "%' or UPPER(rep.description) like '%" + word + "%' ";
                }
                noOfWords ++;
            }
            query += ") ";
        }
        query += "order by rep.priority desc limit :limit offset :offset";

        // parameters
        Map<String, Object> params = new HashMap<>();
        params.put("userId", request.getUserId());
        if (request.getDeviceInfo() != null && request.getDeviceInfo().length() > 0) {
            params.put("deviceInfo", request.getDeviceInfo());
        }
        params.put("limit", request.getLimit());
        params.put("offset", request.getOffset());
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);

       return jdbcTemplate.queryForList(query, sqlParameterSource);
    }

    @Transactional
    public void calculatePricePerAreaUnit(String ngay) {
        String HaNoiCode = "01";
        List<District> districtCodes = districtRepository.findByProvinceCode(HaNoiCode);
        
        districtCodes.stream().parallel().forEach(e -> {
                
                // 🚨 Tạm thời chỉ chạy benchmark cho BÁN (sell = true) vì Entity chưa có cột phân biệt Thuê/Bán
                AreaPriceBenchmark sellHouse = getAreaPriceBenchmark(true, EType.HOUSE, HaNoiCode, e.getCode(), ngay);
                AreaPriceBenchmark sellApartment = getAreaPriceBenchmark(true, EType.APARTMENT, HaNoiCode, e.getCode(), ngay);
                AreaPriceBenchmark sellPlot = getAreaPriceBenchmark(true, EType.PLOT, HaNoiCode, e.getCode(), ngay);
                
                if (sellHouse != null) areaPriceBenchmarkRepository.save(sellHouse);
                if (sellApartment != null) areaPriceBenchmarkRepository.save(sellApartment);
                if (sellPlot != null) areaPriceBenchmarkRepository.save(sellPlot);
            });
    }

    private AreaPriceBenchmark getAreaPriceBenchmark(boolean sell, EType type, String provinceCode, String districtCode, String ngay) {
        // 🚨 Đã bổ sung tính toán MIN, MAX và COUNT để khớp với Entity
        String query = "SELECT " +
                "AVG(price / NULLIF(area, 0)) as avg_price, " +
                "MIN(price / NULLIF(area, 0)) as min_price, " +
                "MAX(price / NULLIF(area, 0)) as max_price, " +
                "COUNT(id) as sample_count " +
                "FROM real_estate_posts \n" +
                "WHERE is_enabled = true \n" +
                "AND status IN ('APPROVED', 'DA_HOAN_THANH') \n" +
                "AND (DATE(NOW()) - DATE(created_at)) <= period\n" +
                "AND real-estate-post/detailPageData? = :sell\n" +
                "AND type = :type\n" +
                "AND district_code = :districtCode\n" +
                "AND province_code = :provinceCode\n" +
                "AND (DATE(created_at) = CAST(:ngay AS DATE) OR DATE(updated_at) = CAST(:ngay AS DATE))";
        
        Map<String, Object> params = new HashMap<>();
        params.put("sell", sell);
        params.put("type", type.toString());
        params.put("districtCode", districtCode);
        params.put("provinceCode", provinceCode);
        params.put("ngay", ngay);
        
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
        Map<String, Object> response = jdbcTemplate.queryForMap(query, sqlParameterSource);
        
        // Nếu không có bài viết nào (sample_count = 0 thì avg_price sẽ bị null)
        if (response.get("avg_price") == null) {
            return null;
        } else {
            AreaPriceBenchmark areaPriceBenchmark = new AreaPriceBenchmark();
            areaPriceBenchmark.setId(null);
            
            // Map mã hành chính và loại
            areaPriceBenchmark.setProvinceCode(provinceCode);
            areaPriceBenchmark.setDistrictCode(districtCode);
            areaPriceBenchmark.setPropertyType(type); // 🚨 Chuẩn tên propertyType
            
            // Map các chỉ số (Ép qua Number chống ClassCastException)
            areaPriceBenchmark.setAvgPricePerM2(((Number) response.get("avg_price")).doubleValue());
            areaPriceBenchmark.setMinPrice(((Number) response.get("min_price")).doubleValue());
            areaPriceBenchmark.setMaxPrice(((Number) response.get("max_price")).doubleValue());
            areaPriceBenchmark.setSampleCount(((Number) response.get("sample_count")).intValue());
            
            // 🚨 Xử lý ngày tháng: Chuyển chuỗi YYYY-MM-DD sang Instant
            try {
                java.time.LocalDate localDate = java.time.LocalDate.parse(ngay);
                java.time.Instant instant = localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant();
                areaPriceBenchmark.setUpdatedAt(instant);
            } catch (Exception e) {
                e.printStackTrace();
                areaPriceBenchmark.setUpdatedAt(java.time.Instant.now());
            }
            
            return areaPriceBenchmark;
        }
    }

    public ChartOption baiVietChart1(Byte sell, String type, String provinceCode, Integer year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int currMonth = (calendar.get(Calendar.YEAR) == year) ? calendar.get(Calendar.MONTH) + 1 : 12;
        
        ChartOption chartOption = new ChartOption();
        List<District> districts = districtRepository.findByProvinceCode(provinceCode);
        
        districts.stream().parallel().forEach(e -> {
            Series series = new Series();
            series.setName(e.getName());
            List<Long> data = new ArrayList<>();
            for (int i = 1; i <= currMonth; i++) {
                // 🚨 Sửa hàm extract month/year
                String query = "SELECT COUNT(*) as cnt FROM real_estate_posts \n" +
                        "WHERE is_sell = :sell\n" +
                        "AND type = :type\n" +
                        "AND district_code = :districtCode\n" +
                        "AND province_code = :provinceCode\n" +
                        "AND EXTRACT(MONTH FROM created_at) = :month\n" +
                        "AND EXTRACT(YEAR FROM created_at) = :year";
                Map<String, Object> params = new HashMap<>();
                params.put("sell", sell != null && sell == 1);
                params.put("type", type);
                params.put("districtCode", e.getCode());
                params.put("provinceCode", provinceCode);
                params.put("month", i);
                params.put("year", year);
                
                SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
                Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(query, sqlParameterSource);
                data.add(((Number) jdbcResponse.get("cnt")).longValue());
            }
            series.setData(new ArrayList<>(data));
            chartOption.getSeries().add(series);
        });
        
        List<Object> xaxis = new ArrayList<>();
        for (int i = 1; i <= currMonth; i++) xaxis.add(i);
        chartOption.setXaxis(xaxis);
        return chartOption;
    }

    public ChartOption baiVietChart2(Byte sell, String type, String provinceCode, Integer year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int currMonth = (calendar.get(Calendar.YEAR) == year) ? calendar.get(Calendar.MONTH) + 1 : 12;
        
        ChartOption chartOption = new ChartOption();
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("VIEW", "Số lượt xem");
        typeMap.put("INTEREST", "Quan tâm");
        typeMap.put("COMMENT", "Bình luận");
        typeMap.put("REPORT", "Báo cáo");
        
        for (Map.Entry<String, String> iterator : typeMap.entrySet()) {
            Series series = new Series();
            List<Long> data = new ArrayList<>();
            for (int i = 1; i <= currMonth; i++) {
                data.add(chart2Counter(sell, type, provinceCode, i, year, iterator.getKey()));
            }
            series.setName(iterator.getValue());
            series.setData(new ArrayList<>(data));
            chartOption.getSeries().add(series);
        }
        
        List<Object> xaxis = new ArrayList<>();
        for (int i = 1; i <= currMonth; i++) xaxis.add(i);
        chartOption.setXaxis(xaxis);
        return chartOption;
    }

    private Long chart2Counter(Byte sell, String type, String provinceCode, Integer month, Integer year, String mapType) {
        String query;
        // 🚨 Sửa các hàm YEAR() và MONTH() thành EXTRACT
        if (mapType.equals("VIEW")) {
            query = "SELECT COUNT(*) as cnt FROM post_view pv INNER JOIN real_estate_posts rep ON pv.real_estate_post_id = rep.id " +
                    "WHERE rep.is_sell = :sell AND rep.type = :type AND rep.province_code = :provinceCode " +
                    "AND EXTRACT(YEAR FROM pv.create_at) = :year AND EXTRACT(MONTH FROM pv.create_at) = :month";
        } else if (mapType.equals("COMMENT")) {
            query = "SELECT COUNT(*) as cnt FROM post_comment pv INNER JOIN real_estate_posts rep ON pv.post_id = rep.id " +
                    "WHERE rep.is_sell = :sell AND rep.type = :type AND rep.province_code = :provinceCode " +
                    "AND EXTRACT(YEAR FROM pv.create_at) = :year AND EXTRACT(MONTH FROM pv.create_at) = :month";
        } else if (mapType.equals("INTEREST")) {
            query = "SELECT COUNT(*) as cnt FROM interested pv INNER JOIN real_estate_posts rep ON pv.real_estate_post_id = rep.id " +
                    "WHERE rep.is_sell = :sell AND rep.type = :type AND rep.province_code = :provinceCode " +
                    "AND EXTRACT(YEAR FROM pv.create_at) = :year AND EXTRACT(MONTH FROM pv.create_at) = :month";
        } else {
            query = "SELECT COUNT(*) as cnt FROM post_report pv INNER JOIN real_estate_posts rep ON pv.post_id = rep.id " +
                    "WHERE rep.is_sell = :sell AND rep.type = :type AND rep.province_code = :provinceCode " +
                    "AND EXTRACT(YEAR FROM pv.create_at) = :year AND EXTRACT(MONTH FROM pv.create_at) = :month";
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("sell", sell != null && sell == 1);
        params.put("type", type);
        params.put("provinceCode", provinceCode);
        params.put("month", month);
        params.put("year", year);
        
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
        Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(query, sqlParameterSource);
        return ((Number) jdbcResponse.get("cnt")).longValue();
    }

    public ChartOption getPriceFluctuationStatistic(Boolean isSell, String type, String provinceCode, String districtCode, Integer month, Integer year) {
    ChartOption chartOption = new ChartOption();
    
    // 1. Chuẩn bị mốc trục X (X-axis)
    int daysInMonth = (month != null && month > 0) ? Util.getDayOfMonth(month, year) : Util.getCurrMonth(year);
    for (int i = 1; i <= daysInMonth; i++) {
        chartOption.getXaxis().add(i);
    }

    // 2. Viết câu SQL GROUP BY gộp để xử lý mọi thứ dưới Database
    // Trích xuất ngày hoặc tháng tùy theo param month
    String timeExtractSql = (month != null && month > 0) 
            ? "EXTRACT(DAY FROM create_at) AS time_val " 
            : "EXTRACT(MONTH FROM create_at) AS time_val ";

    StringBuilder query = new StringBuilder(
        "SELECT district_code, " + timeExtractSql + ", AVG(price) as avg_price " +
        "FROM statistic_price_fluctuation " +
        "WHERE sell = :sell AND type = :type AND province_code = :provinceCode "
    );

    Map<String, Object> params = new HashMap<>();
    params.put("sell", isSell != null && isSell); // Chuyển Byte cũ sang Boolean cho chuẩn
    params.put("type", type);
    params.put("provinceCode", provinceCode);

    // Lọc theo Năm và Tháng (nếu có)
    query.append("AND EXTRACT(YEAR FROM create_at) = :year ");
    params.put("year", year);
    
    if (month != null && month > 0) {
        query.append("AND EXTRACT(MONTH FROM create_at) = :month ");
        params.put("month", month);
    }

    // 3. Xử lý danh sách Quận/Huyện cần truy vấn (ĐÃ BỎ WARD CODE)
    List<District> targetDistricts = new ArrayList<>();
    
    if (districtCode != null && !districtCode.trim().isEmpty()) {
        // Nếu truyền cụ thể 1 Quận, chỉ query quận đó
        query.append("AND district_code = :districtCode ");
        params.put("districtCode", districtCode);
        
        districtRepository.findById(districtCode).ifPresent(targetDistricts::add);
    } else {
        // Nếu không truyền Quận, lấy toàn bộ Quận của Tỉnh đó
        targetDistricts = districtRepository.findByProvinceCode(provinceCode);
    }

    // Nhóm kết quả theo Mã quận và Thời gian
    query.append("GROUP BY district_code, time_val ORDER BY time_val ASC");

    // 4. Bắn ĐÚNG 1 CÂU LỆNH XUỐNG DATABASE
    List<Map<String, Object>> dbResults = jdbcTemplate.queryForList(query.toString(), params);

    // 5. Build kết quả Series từ dữ liệu thô
    for (District district : targetDistricts) {
        Series series = new Series();
        series.setName(district.getName());
        
        // Khởi tạo toàn bộ mảng dữ liệu với giá trị 0 (hoặc null) trước
        List<Object> dataPoints = new ArrayList<>(Collections.nCopies(daysInMonth, 0.0));
        
        // Đắp dữ liệu từ DB vào đúng vị trí ngày/tháng
        for (Map<String, Object> row : dbResults) {
            String dbDistrictCode = (String) row.get("district_code");
            if (district.getCode().equals(dbDistrictCode)) {
                // time_val trả về mốc ngày (hoặc tháng), mảng bắt đầu từ index 0 nên phải trừ đi 1
                int timeVal = ((Number) row.get("time_val")).intValue();
                double avgPrice = ((Number) row.get("avg_price")).doubleValue();
                
                if (timeVal >= 1 && timeVal <= daysInMonth) {
                    dataPoints.set(timeVal - 1, avgPrice);
                }
            }
        }
        
        series.setData(dataPoints);
        chartOption.getSeries().add(series);
    }

    return chartOption;
}

    public List<IRepAdmin> getLstMostChangePrice() {
        return repository.getLstMostChangePrice();
    }

    public ChartOption getPriceOption(UUID postId) {
        ChartOption chartOption = new ChartOption();
        String query = "SELECT price, TO_CHAR(create_at, 'YYYY-MM-DD HH24:MI:SS') as createAt " +
                "FROM real_estate_post_price WHERE real_estate_post_id = :postId";
        Map<String, Object> params = new HashMap<>();
        params.put("postId", postId); // Ép UUID
        List<Map<String, Object>> jdbcResponse = jdbcTemplate.queryForList(query, new MapSqlParameterSource(params));
        jdbcResponse.forEach(e -> {
            chartOption.getXaxis().add(e.get("createat")); // Postgres thường lowercase alias
            chartOption.getSeries().add(e.get("price"));
        });
        return chartOption;
    }

    public ChartOption getViewChartOption(UUID postId, Integer month, Integer year) {
        return getChartOptionCommon("SELECT COUNT(id) as cnt FROM post_view WHERE real_estate_post_id = :postId", postId, month, year);
    }

    public ChartOption getCommentChartOption(UUID postId, Integer month, Integer year) {
        return getChartOptionCommon("SELECT COUNT(id) as cnt FROM post_comment WHERE post_id = :postId", postId, month, year);
    }

    public ChartOption getInterestedChartOption(UUID postId, Integer month, Integer year) {
        return getChartOptionCommon("SELECT COUNT(id) as cnt FROM interested WHERE real_estate_post_id = :postId", postId, month, year);
    }

    public ChartOption getReportChartOption(UUID postId, Integer month, Integer year) {
        return getChartOptionCommon("SELECT COUNT(id) as cnt FROM post_report WHERE post_id = :postId", postId, month, year);
    }

    public ChartOption getClickedViewChartOption(UUID postId, Integer month, Integer year) {
        return getChartOptionCommon("SELECT COUNT(id) as cnt FROM clicked_info_view WHERE real_estate_post_id = :postId", postId, month, year);
    }

    public ChartOption getChartOptionCommon(String query, UUID postId, Integer month, Integer year) {
        ChartOption chartOption = new ChartOption();
        int val;
        Map<String, Object> params = new HashMap<>();
        params.put("postId", postId); // Ép kiểu UUID an toàn

        if (month == 0) {
            query += " AND EXTRACT(MONTH FROM create_at) = :month AND EXTRACT(YEAR FROM create_at) = :year ";
            val = Util.getCurrMonth(year);
            for (int i = 1; i <= val; i++) {
                params.put("month", i);
                params.put("year", year);
                Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(query, new MapSqlParameterSource(params));
                chartOption.getXaxis().add(i);
                chartOption.getSeries().add(((Number) jdbcResponse.get("cnt")).longValue());
            }
        } else {
            query += " AND DATE(create_at) = CAST(:date AS DATE) ";
            val = Util.getDayOfMonth(month, year);
            for (int i = 1; i <= val; i++) {
                params.put("date", year + "-" + month + "-" + i);
                Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(query, new MapSqlParameterSource(params));
                chartOption.getXaxis().add(i);
                chartOption.getSeries().add(((Number) jdbcResponse.get("cnt")).longValue());
            }
        }
        return chartOption;
    }

    public List<IRepClientAdministration> getAllRealEstatePostOfUser(UUID userId) {
        return repository.getAllRealEstatePost(userId);
    }

    public List<IInterestedUser> getAllInterestedUsersOfPost(UUID postId) {
        return repository.getListInterestedUsers(postId);
    }
}
