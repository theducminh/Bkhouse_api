package com.api.bkhouse.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

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

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RealEstatePostService {
    @Autowired
    private RealEstatePostRepository repository;

    @Autowired
    private RealEstatePostPriceRepository realEstatePostPriceRepository;

    @Autowired
    private InterestedRepository interestedRepository;

    @Autowired
    private PostMediaRepository postMediaRepository;

    @Autowired
    private PostViewRepository postViewRepository;

    @Autowired
    private ClickedInfoViewRepository clickedInfoViewRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private StatisticPriceFluctuationRepository statisticPriceFluctuationRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private WardRepository wardRepository;

    private Logger logger = LoggerFactory.getLogger(RealEstatePostService.class);

    public RealEstatePost findById(UUID id) {
        Optional<RealEstatePost> realEstatePost = repository.findById(id);
        return realEstatePost.get();
    }

    public List<RealEstatePost> findByOwnerId(UUID ownerId, Integer page, Integer rows) {
        return repository.findByOwnerId(ownerId, rows, page*rows);
    }

    public Integer getNoOfPostsByUserId(UUID ownerId) {
        return repository.getNoOfRecords(ownerId);
    }

    @Transactional
    public RealEstatePost create(RealEstatePost realEstatePost) {
        RealEstatePost saved = repository.save(realEstatePost);
        RealEstatePostPrice realEstatePostPrice = new RealEstatePostPrice();
        realEstatePostPrice.setId(0L);
        realEstatePostPrice.setPrice(saved.getPrice());
        realEstatePostPrice.setRealEstatePost(saved);
        realEstatePostPrice.setCreateBy(saved.getCreateBy());
        realEstatePostPrice.setCreateAt(Util.getCurrentDateTime());
        realEstatePostPriceRepository.save(realEstatePostPrice);
        return saved;
    }

    @Transactional
    public void createRepPrice(Double price, UUID repId, UUID userId) {
        RealEstatePostPrice realEstatePostPrice = new RealEstatePostPrice();
        realEstatePostPrice.setId(0L);
        realEstatePostPrice.setRealEstatePost(findByIdAndEnable(repId));
        realEstatePostPrice.setCreateAt(Util.getCurrentDateTime());
        realEstatePostPrice.setCreateBy(userId);
        realEstatePostPrice.setPrice(price);
        realEstatePostPriceRepository.save(realEstatePostPrice);
    }

    @Transactional
    public RealEstatePost update(RealEstatePost realEstatePost) {
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
        postView.setId(0L);
        postView.setRealEstatePostId(realEstatePostId);
        postView.setCreateBy(UUID.randomUUID());
        postView.setCreateAt(Util.getCurrentDateTime());
        postViewRepository.save(postView);
    }

    @Transactional
    public void updateClickedView(UUID realEstatePostId) {
        repository.updateClickedView(realEstatePostId);
        ClickedInfoView clickedInfoView = new ClickedInfoView();
        clickedInfoView.setId(0L);
        clickedInfoView.setRealEstatePostId(realEstatePostId);
        clickedInfoView.setCreateBy(UUID.randomUUID());
        clickedInfoView.setCreateAt(Util.getCurrentDateTime());
        clickedInfoViewRepository.save(clickedInfoView);
    }

    public RealEstatePost findByIdAndEnable(UUID id) {
        Optional<RealEstatePost> realEstatePost = repository.findByIdAndEnable(id, true);
        if (realEstatePost.isEmpty()) {
            return null;
        }
        return realEstatePost.get();
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
            if (ownerOptional.isEmpty()) {
                return null;
            }
            return ownerOptional.get();
        } else {
            return optional.get();
        }
    }

    public boolean isInterested(UUID userId, UUID realEstatePostId, String deviceInfo) {
        if (deviceInfo != null && deviceInfo.length() > 0 && (userId == null || userId.toString().length() == 0)) {
            return interestedRepository.existsByDeviceInfoAndRealEstatePostId(deviceInfo, realEstatePostId);
        }
        return interestedRepository.existsByUserIdAndRealEstatePostId(userId, realEstatePostId);
    }

    public Object countInterested(String userId, String deviceInfo) {
        String query = "select count(*) as cnt\n" +
                "from interested i inner join real_estate_post rep on i.real_estate_post_id = rep.id\n" +
                "where rep.enable = 1 and i.user_id = :userId ";
        Map<String, Object> params = new HashMap<>();
        if (deviceInfo != null && deviceInfo.length() > 0 && (userId == null || userId.length() == 0)) {
//            return interestedRepository.countByUserIdAndDeviceInfo("anonymous", deviceInfo);
            query += "and i.device_info = :deviceInfo";
            params.put("deviceInfo", deviceInfo);
            params.put("userId", "anonymous");
        } else {
            params.put("userId", userId);
        }
        Map<String, Object> result = jdbcTemplate.queryForMap(query, new MapSqlParameterSource(params));
        return result.get("cnt");
    }

    public List<RepDetailPageResponse> detailPageData(Byte sell, String type, Integer limit, Integer offset, UUID userId, String deviceInfo) {
        List<RepDetailPageResponse> responses = new ArrayList<>();
        List<UUID> repIds = repository.getRepIdDetailPage(sell, type, limit, offset);
        if (repIds.isEmpty()) {
            return responses;
        }
        repIds
                .stream()
                .forEach(e -> {
                    RepDetailPageResponse response = new RepDetailPageResponse();
                    Optional<RealEstatePost> realEstatePostOptional = repository.findById(e);
                    if (!realEstatePostOptional.isEmpty()) {
                        RealEstatePost realEstatePost = realEstatePostOptional.get();
                        response.setArea(realEstatePost.getArea());
                        response.setAddressShow(realEstatePost.getAddressShow());
                        response.setDescription(realEstatePost.getDescription());
                        response.setDirection(realEstatePost.getDirection());
                        response.setTitle(realEstatePost.getTitle());
                        response.setPrice(realEstatePost.getPrice());
                        response.setCreateAt(realEstatePost.getCreateAt());
                        response.setAvatarUrl(realEstatePost.getOwnerId().getAvatarUrl());
                        response.setFullName(realEstatePost.getOwnerId().getFirstName() + " "
                                + realEstatePost.getOwnerId().getMiddleName() + " "
                                + realEstatePost.getOwnerId().getLastName()
                        );
                        response.setPhoneNumber(realEstatePost.getOwnerId().getPhoneNumber());
                        List<PostMedia> postMedias = postMediaRepository.findByPostId(e);
                        if (!postMedias.isEmpty()) {
                            response.setImageUrl(postMedias.get(0).getId());
                        }
                        response.setInterested(isInterested(userId, e, deviceInfo));
                        responses.add(response);
                    }
                });
        return responses;
    }

    public Object findByMostInterested() {
//        List<RepClientResponse> responses = new ArrayList<>();
//        List<IRepClient> repClients = repository.getLstMostInterested();
//        repClients
//                .stream()
//                .forEach(e -> {
//                    Optional<String> imageUrlOptional = postMediaRepository.getOneImageOfPost(e.getId());
//                    RepClientResponse response = new RepClientResponse(e.getId(),
//                            e.getTitle(),
//                            e.getPrice(),
//                            e.getArea(),
//                            e.getSell(),
//                            e.getAddressShow(),
//                            e.getCreateAt(),
//                            imageUrlOptional.isEmpty() ? "" : imageUrlOptional.get());
//                    responses.add(response);
//                });
//        return responses;
        return repository.getLstMostInterested();
    }

    public Object findByMostView() {
//        List<RepClientResponse> responses = new ArrayList<>();
//        List<IRepClient> repClients = repository.getLstMostView();
//        repClients
//                .stream()
//                .forEach(e -> {
//                    Optional<String> imageUrlOptional = postMediaRepository.getOneImageOfPost(e.getId());
//                    RepClientResponse response = new RepClientResponse(e.getId(),
//                            e.getTitle(),
//                            e.getPrice(),
//                            e.getArea(),
//                            e.getSell(),
//                            e.getAddressShow(),
//                            e.getCreateAt(),
//                            imageUrlOptional.isEmpty() ? "" : imageUrlOptional.get());
//                    responses.add(response);
//                });
//        return responses;
        return repository.getLstMostView();
    }

    public Object findByNewest() {
        return repository.getLstNewest();
    }

    public Integer countTotalBySellAndTypeClient(Byte sell, String type) {
        return repository.countTotalBySellAndTypeClient(sell, type);
    }

    public CountInterestAndCommentResponse countNoOfInterestAndComment(UUID postId) {
        CountInterestAndCommentResponse response = new CountInterestAndCommentResponse();
        response.setNoOfComment(postCommentRepository.countByPostId(postId));
        response.setNoOfInterest(interestedRepository.countByRealEstatePostId(postId));
        return response;
    }

    public Object search(SearchRequest request) {
        String query = "select rep.id, rep.title, rep.address_show as addressShow, " +
                "rep.price, rep.area, rep.is_sell as sell, rep.create_at as createAt, rep.description, " +
                "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName, " +
                "u.phone_number as phoneNumber, u.avatar_url as avatarUrl, ";
        if (request.getDeviceInfo() != null && request.getDeviceInfo().length() > 0) {
            query += "(select count(*) > 0 from interested where user_id = :userId and device_info = :deviceInfo and real_estate_post_id = rep.id ) as interested, ";
        } else {
            query += "(select count(*) > 0 from interested where user_id = :userId and real_estate_post_id = rep.id ) as interested, ";
        }
        query += "(select id from post_media where post_id = rep.id limit 1) as imageUrl\n" +
                "from real_estate_post rep inner join user u on u.id = rep.owner_id ";
        if (request.getType() != null) {
            if (request.getType().equals(EType.APARTMENT.toString())) {
                query += "inner join apartment spc on spc.real_estate_post_id = rep.id ";
            } else if (request.getType().equals(EType.HOUSE.toString())) {
                query += "inner join house spc on spc.real_estate_post_id = rep.id ";
            }
        }
        query += "where u.enable = 1 " +
                "and rep.enable = 1 " +
                "and rep.status = 'DA_KIEM_DUYET' " +
                "and datediff(now(), rep.create_at) <= rep.period ";
        if (request.getSell() != null) {
            if (request.getSell()) {
                query += "and rep.is_sell = 1 ";
            } else {
                query += "and rep.is_sell = 0 ";
            }
        }
        if (request.getType() != null) {
            query += "and rep.type = '" + request.getType().replaceAll("\\s+", "") + "' ";
            if (!request.getType().equals(EType.PLOT.toString()) && request.getNoOfBedrooms() != null && request.getNoOfBedrooms().length > 0) {
                int index = 0;
                for (Integer noOfBedroom: request.getNoOfBedrooms()) {
                    if (index == 0) {
                        if (noOfBedroom > 5) {
                            query += "and ( spc.no_bedroom >= " + noOfBedroom + " ";
                        } else {
                            query += "and ( spc.no_bedroom = " + noOfBedroom + " ";
                        }
                    } else {
                        if (noOfBedroom > 5) {
                            query += "or spc.no_bedroom >= " + noOfBedroom + " ";
                        } else {
                            query += "or spc.no_bedroom = " + noOfBedroom + " ";
                        }
                    }
                    index ++;
                }
                query += " ) ";
            }
        }
        if (request.getProvinceCode() != null) {
            query += "and rep.province_code = '" + request.getProvinceCode().replaceAll("\\s+", "") + "' ";
        }
        if (request.getDistrictCode() != null && request.getDistrictCode().length > 0) {
            int index = 0;
            for (String districtCode: request.getDistrictCode()) {
                if (index == 0) {
                    query += "and ( rep.district_code = '" + districtCode.replaceAll("\\s+", "") + "' ";
                } else {
                    query += "or rep.district_code = '" + districtCode.replaceAll("\\s+", "") + "' ";
                }
                index ++;
            }
            query += " ) ";
        }
        if (request.getWardCode() != null && request.getWardCode().length > 0) {
            int index = 0;
            for (String wardCode: request.getWardCode()) {
                if (index == 0) {
                    query += "and ( rep.ward_code = '" + wardCode.replaceAll("\\s+", "") + "' ";
                } else {
                    query += "or rep.ward_code = '" + wardCode.replaceAll("\\s+", "") + "' ";
                }
                index ++;
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

        List<Map<String, Object>> response = jdbcTemplate.queryForList(query, sqlParameterSource);
        return response;
    }

    @Transactional
    public void calculatePricePerAreaUnit(String ngay) {
//        List<String> provinceCodes = statisticPriceFluctuationRepository.getAllProvinceCodes();
        String HaNoiCode = "01";
        List<District> districtCodes = districtRepository.findByProvinceCode(HaNoiCode);
        districtCodes
                .stream()
                .parallel()
                .forEach(e -> {
                    List<Ward> wardCodes = wardRepository.findByDistrictCode(e.getCode());
                    wardCodes
                            .stream()
                            .parallel()
                                    .forEach(ee -> {
                                        StatisticPriceFluctuation sellHouse = getStatisticPriceFluctuation(true, EType.HOUSE, HaNoiCode, e.getCode(), ee.getCode(), ngay);
                                        StatisticPriceFluctuation sellApartment = getStatisticPriceFluctuation(true, EType.APARTMENT, HaNoiCode, e.getCode(), ee.getCode(), ngay);
                                        StatisticPriceFluctuation sellPlot = getStatisticPriceFluctuation(true, EType.PLOT, HaNoiCode, e.getCode(), ee.getCode(), ngay);
                                        StatisticPriceFluctuation hireHouse = getStatisticPriceFluctuation(false, EType.HOUSE, HaNoiCode, e.getCode(), ee.getCode(), ngay);
                                        StatisticPriceFluctuation hireApartment = getStatisticPriceFluctuation(false, EType.APARTMENT, HaNoiCode, e.getCode(), ee.getCode(), ngay);
                                        if (sellHouse != null) {
                                            statisticPriceFluctuationRepository.save(sellHouse);
                                        }
                                        if (sellApartment != null) {
                                            statisticPriceFluctuationRepository.save(sellApartment);
                                        }
                                        if (sellPlot != null) {
                                            statisticPriceFluctuationRepository.save(sellPlot);
                                        }
                                        if (hireHouse != null) {
                                            statisticPriceFluctuationRepository.save(hireHouse);
                                        }
                                        if (hireApartment != null) {
                                            statisticPriceFluctuationRepository.save(hireApartment);
                                        }
                                    });
                });
    }

    private StatisticPriceFluctuation getStatisticPriceFluctuation(boolean sell, EType type, String provinceCode, String districtCode, String wardCode, String ngay) {
        String query = "select avg(price/area) as result from real_estate_post \n" +
                "where enable = 1 \n" +
                "and (status = 'DA_KIEM_DUYET' or status = 'DA_HOAN_THANH') \n" +
                "and datediff(now(), create_at) <= period\n" +
                "and is_sell = :sell\n" +
                "and type = :type\n" +
                "and district_code = :districtCode\n" +
                "and province_code = :provinceCode\n" +
                "and ward_code = :wardCode\n" +
                "and (date(create_at) = :ngay " +
                "or date(update_at) = :ngay)";
        Map<String, Object> params = new HashMap<>();
        params.put("sell", sell ? 1 : 0);
        params.put("type", type.toString());
        params.put("districtCode", districtCode);
        params.put("provinceCode", provinceCode);
        params.put("wardCode", wardCode);
        params.put("ngay", ngay);
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
        Map<String, Object> response = jdbcTemplate.queryForMap(query, sqlParameterSource);
        if (response.get("result") == null) {
            return null;
        } else {
            StatisticPriceFluctuation statisticPriceFluctuation = new StatisticPriceFluctuation();
            statisticPriceFluctuation.setId(0L);
            statisticPriceFluctuation.setSell(sell);
            statisticPriceFluctuation.setType(type);
            try {
                statisticPriceFluctuation.setCreateAt(new SimpleDateFormat("yyyy-MM-dd").parse(ngay));
            } catch (ParseException e) {
                e.printStackTrace();
                statisticPriceFluctuation.setCreateAt(new Date());
            }
            statisticPriceFluctuation.setDistrictCode(districtCode);
            statisticPriceFluctuation.setProvinceCode(provinceCode);
            statisticPriceFluctuation.setWardCode(wardCode);
            statisticPriceFluctuation.setPrice((Double) response.get("result"));
            return statisticPriceFluctuation;
        }
//        statisticPriceFluctuation.setPrice(repository.calculatePricePerAreaUnit(sell ? 1 : 0, type.toString(), districtCode, provinceCode, wardCode));
    }

    public ChartOption baiVietChart1(Byte sell, String type, String provinceCode, Integer year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int currMonth;
        if (calendar.get(Calendar.YEAR) == year) {
            currMonth = calendar.get(Calendar.MONTH);
            currMonth++;
        } else {
            currMonth = 12;
        }
        ChartOption chartOption = new ChartOption();
        List<District> districts = districtRepository.findByProvinceCode(provinceCode);
        int finalCurrMonth = currMonth;
        districts
                .stream()
                .parallel()
                .forEach(e -> {
                    Series series = new Series();
                    series.setName(e.getName());
                    List<Long> data = new ArrayList<>();
                    for (int i = 1; i <= finalCurrMonth; i++) {
                        String query = "select count(*) as cnt from real_estate_post \n" +
                                "where is_sell = :sell\n" +
                                "and type = :type\n" +
                                "and district_code = :districtCode\n" +
                                "and province_code = :provinceCode\n" +
                                "and month(create_at) = :month\n" +
                                "and year(create_at) = :year";
                        Map<String, Object> params = new HashMap<>();
                        params.put("sell", sell);
                        params.put("type", type);
                        params.put("districtCode", e.getCode());
                        params.put("provinceCode", provinceCode);
                        params.put("month", i);
                        params.put("year", year);
                        SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
                        Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(query, sqlParameterSource);
                        Long val = (Long) jdbcResponse.get("cnt");
                        data.add(val);
                    }
                    series.setData(new ArrayList<Object>(data));
                    chartOption.getSeries().add(series);
                });
        List<Integer> xaxis = new ArrayList<>();
        for (int i = 1; i <= finalCurrMonth; i++) {
            xaxis.add(i);
        }
        chartOption.setXaxis(new ArrayList<>(xaxis));
        return chartOption;
    }

    public ChartOption baiVietChart2(Byte sell, String type, String provinceCode, Integer year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int currMonth;
        if (calendar.get(Calendar.YEAR) == year) {
            currMonth = calendar.get(Calendar.MONTH);
            currMonth++;
        } else {
            currMonth = 12;
        }
        ChartOption chartOption = new ChartOption();
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("VIEW", "Số lượt xem");
        typeMap.put("INTEREST", "Quan tâm");
        typeMap.put("COMMENT", "Bình luận");
        typeMap.put("REPORT", "Báo cáo");
        int finalCurrMonth = currMonth;
        for (Map.Entry<String, String> iterator: typeMap.entrySet()) {
            Series series = new Series();
            List<Long> data = new ArrayList<>();
            for (int i = 1; i <= finalCurrMonth; i++) {
                Long val = chart2Counter(sell, type, provinceCode, i, year, iterator.getKey());
                data.add(val);
            }
            series.setName(iterator.getValue());
            series.setData(new ArrayList<Object>(data));
            chartOption.getSeries().add(series);
        }
        List<Integer> xaxis = new ArrayList<>();
        for (int i = 1; i <= finalCurrMonth; i++) {
            xaxis.add(i);
        }
        chartOption.setXaxis(new ArrayList<>(xaxis));
        return chartOption;
    }

    private Long chart2Counter(Byte sell, String type, String provinceCode, Integer month, Integer year, String mapType) {
        String query;
        if (mapType.equals("VIEW")) {
            query = "select count(*) as cnt\n" +
                    "from post_view pv inner join real_estate_post rep on pv.real_estate_post_id = rep.id\n" +
                    "and rep.is_sell = :sell\n" +
                    "and rep.type = :type\n" +
                    "and rep.province_code = :provinceCode\n" +
                    "and year(pv.create_at) = :year\n" +
                    "and month(pv.create_at) = :month";
        } else if (mapType.equals("COMMENT")) {
            query = "select count(*) as cnt\n" +
                    "from post_comment pv inner join real_estate_post rep on pv.post_id = rep.id\n" +
                    "and rep.is_sell = :sell\n" +
                    "and rep.type = :type\n" +
                    "and rep.province_code = :provinceCode\n" +
                    "and year(pv.create_at) = :year\n" +
                    "and month(pv.create_at) = :month";
        } else if (mapType.equals("INTEREST")) {
            query = "select count(*) as cnt\n" +
                    "from interested pv inner join real_estate_post rep on pv.real_estate_post_id = rep.id\n" +
                    "and rep.is_sell = :sell\n" +
                    "and rep.type = :type\n" +
                    "and rep.province_code = :provinceCode\n" +
                    "and year(pv.create_at) = :year\n" +
                    "and month(pv.create_at) = :month";
        } else {
            query = "select count(*) as cnt\n" +
                    "from post_report pv inner join real_estate_post rep on pv.post_id = rep.id\n" +
                    "and rep.is_sell = :sell\n" +
                    "and rep.type = :type\n" +
                    "and rep.province_code = :provinceCode\n" +
                    "and year(pv.create_at) = :year\n" +
                    "and month(pv.create_at) = :month";
        }
        Map<String, Object> params = new HashMap<>();
        params.put("sell", sell);
        params.put("type", type);
        params.put("provinceCode", provinceCode);
        params.put("month", month);
        params.put("year", year);
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
        Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(query, sqlParameterSource);
        Long val = (Long) jdbcResponse.get("cnt");
        return val;
    }

    public ChartOption getPriceFluctuationStatistic(Byte sell, String type, String provinceCode, String districtCode, String wardCode, Integer month, Integer year) {
        ChartOption chartOption = new ChartOption();
        String query = "select avg(price) as result " +
                "from statistic_price_fluctuation\n" +
                "where sell = :sell \n" +
                "and type = :type \n" +
                "and province_code = :provinceCode \n";
        int val;
        Map<String, Object> params = new HashMap<>();

        params.put("sell", sell);
        params.put("type", type);
        params.put("provinceCode", provinceCode);

        if (month != 0) {
            val = Util.getDayOfMonth(month, year);
        } else {
            val = Util.getCurrMonth(year);
        }
        for (int i = 1; i <= val; i++) {
            chartOption.getXaxis().add(i);
        }

        if (districtCode != null && districtCode.length() > 0) {
            query += "and district_code = :districtCode ";
            params.put("districtCode", districtCode);
            if (wardCode != null && wardCode.length() > 0) {
                query += "and ward_code = :wardCode ";
                params.put("wardCode", wardCode);
                String finalQuery = query;
                if (month != 0) {
                    Series series = new Series();
                    series.setName(wardCode);
                    for (int i = 1; i <= val; i++) {
                        String date = year + "-" + month + "-" + i;
                        String sql = finalQuery + "and date(create_at) = :date ";
                        params.put("date", date);
                        Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(sql, new MapSqlParameterSource(params));
                        series.getData().add(jdbcResponse.get("result"));
                    }
                    chartOption.getSeries().add(series);
                } else {
                    Series series = new Series();
                    series.setName(wardCode);
                    for (int i = 1; i <= val; i++) {
                        String sql = finalQuery + "and year(create_at) = :year " +
                                "and month(create_at) = :month ";
                        params.put("month", i);
                        params.put("year", year);
                        Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(sql, new MapSqlParameterSource(params));
                        series.getData().add(jdbcResponse.get("result"));
                    }
                    chartOption.getSeries().add(series);
                }
            } else {
                List<Ward> wards = wardRepository.findByDistrictCode(districtCode);
                String finalQuery = query;
                if (month != 0) {
                    wards
                            .stream()
                            .parallel()
                            .forEach(e -> {
                                Series series = new Series();
                                series.setName(e.getName());
                                for (int i = 1; i <= val; i++) {
                                    String date = year + "-" + month + "-" + i;
                                    String sql = finalQuery + "and date(create_at) = :date " +
                                            "and ward_code = :wardCode ";
                                    params.put("date", date);
                                    params.put("wardCode", e.getCode());
                                    Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(sql, new MapSqlParameterSource(params));
                                    series.getData().add(jdbcResponse.get("result"));
                                }
                                chartOption.getSeries().add(series);
                            });
                } else {
                    wards
                            .stream()
                            .parallel()
                            .forEach(e -> {
                                Series series = new Series();
                                series.setName(e.getName());
                                for (int i = 1; i <= val; i++) {
                                    String sql = finalQuery + "and year(create_at) = :year " +
                                            "and month(create_at) = :month " +
                                            "and ward_code = :wardCode ";
                                    params.put("month", i);
                                    params.put("year", year);
                                    params.put("wardCode", e.getCode());
                                    Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(sql, new MapSqlParameterSource(params));
                                    series.getData().add(jdbcResponse.get("result"));
                                }
                                chartOption.getSeries().add(series);
                            });
                }
            }
        } else {
            if (wardCode != null && wardCode.length() > 0) {
                return null;
            } else {
                List<District> districts = districtRepository.findByProvinceCode(provinceCode);
                String finalQuery = query;
                if (month != 0) {
                    districts
                            .stream()
                            .parallel()
                            .forEach(e -> {
                                Series series = new Series();
                                series.setName(e.getName());
                                for (int i = 1; i <= val; i++) {
                                    String date = year + "-" + month + "-" + i;
                                    String sql = finalQuery + "and date(create_at) = :date " +
                                            "and district_code = :districtCode ";
                                    params.put("date", date);
                                    params.put("districtCode", e.getCode());
                                    Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(sql, new MapSqlParameterSource(params));
                                    series.getData().add(jdbcResponse.get("result"));
                                }
                                chartOption.getSeries().add(series);
                            });
                } else {
                    districts
                            .stream()
                            .parallel()
                            .forEach(e -> {
                                Series series = new Series();
                                series.setName(e.getName());
                                for (int i = 1; i <= val; i++) {
                                    String sql = finalQuery + "and year(create_at) = :year " +
                                            "and month(create_at) = :month " +
                                            "and district_code = :districtCode ";
                                    params.put("month", i);
                                    params.put("year", year);
                                    params.put("districtCode", e.getCode());
                                    Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(sql, new MapSqlParameterSource(params));
                                    series.getData().add(jdbcResponse.get("result"));
                                }
                                chartOption.getSeries().add(series);
                            });
                }
            }
        }
        return chartOption;
    }

    public List<IRepAdmin> getLstMostChangePrice() {
        return repository.getLstMostChangePrice();
    }

    public ChartOption getPriceOption(String postId) {
        ChartOption chartOption = new ChartOption();
        String query = "select price, DATE_FORMAT(create_at, '%Y-%m-%d %T') as createAt\n" +
                "from real_estate_post_price \n" +
                "where real_estate_post_id = :postId";
        Map<String, Object> params = new HashMap<>();
        params.put("postId", postId);
        List<Map<String, Object>> jdbcResponse = jdbcTemplate.queryForList(query, new MapSqlParameterSource(params));
        jdbcResponse
                .stream()
                .forEach(e -> {
                    chartOption.getXaxis().add(e.get("createAt"));
                    chartOption.getSeries().add(e.get("price"));
                });
        return chartOption;
    }

    public ChartOption getViewChartOption(String postId, Integer month, Integer year) {
        String query = "select count(id) as cnt\n" +
                "from post_view \n" +
                "where real_estate_post_id = :postId\n";
        return getChartOptionCommon(query, postId, month, year);
    }

    public ChartOption getCommentChartOption(String postId, Integer month, Integer year) {
        String query = "select count(id) as cnt\n" +
                "from post_comment \n" +
                "where post_id = :postId\n";
        return getChartOptionCommon(query, postId, month, year);
    }

    public ChartOption getInterestedChartOption(String postId, Integer month, Integer year) {
        String query = "select count(id) as cnt\n" +
                "from interested \n" +
                "where real_estate_post_id = :postId\n";
        return getChartOptionCommon(query, postId, month, year);
    }

    public ChartOption getReportChartOption(String postId, Integer month, Integer year) {
        String query = "select count(id) as cnt\n" +
                "from post_report \n" +
                "where post_id = :postId\n";
        return getChartOptionCommon(query, postId, month, year);
    }

    public ChartOption getClickedViewChartOption(String postId, Integer month, Integer year) {
        String query = "select count(id) as cnt\n" +
                "from clicked_info_view \n" +
                "where real_estate_post_id = :postId\n";
        return getChartOptionCommon(query, postId, month, year);
    }

    public ChartOption getChartOptionCommon(String query, String postId, Integer month, Integer year) {
        ChartOption chartOption= new ChartOption();
        int val;
        Map<String, Object> params = new HashMap<>();
        params.put("postId", postId);
        if (month == 0) {
            query += " and month(create_at) = :month and year(create_at) = :year ";
            val = Util.getCurrMonth(year);
            for (int i = 1; i <= val; i++) {
                params.put("month", i);
                params.put("year", year);
                Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(query, new MapSqlParameterSource(params));
                chartOption.getXaxis().add(i);
                chartOption.getSeries().add(jdbcResponse.get("cnt"));
            }
        } else {
            query += " and date(create_at) = :date ";
            val = Util.getDayOfMonth(month, year);
            for (int i = 1; i <= val; i++) {
                String date = year + "-" + month + "-" + i;
                params.put("date", date);
                Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(query, new MapSqlParameterSource(params));
                chartOption.getXaxis().add(i);
                chartOption.getSeries().add(jdbcResponse.get("cnt"));
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
