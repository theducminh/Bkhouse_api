package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.ForumPost;
import com.api.bkhouse.entity.ForumPostLike;
import com.api.bkhouse.payload.response.ForumPostLog;
import com.api.bkhouse.payload.response.chart.ChartOption;
import com.api.bkhouse.payload.response.chart.Series;
import com.api.bkhouse.repository.ForumPostLikeRepository;
import com.api.bkhouse.repository.ForumPostRepository;
import com.api.bkhouse.repository.PostCommentRepository;
import com.api.bkhouse.repository.PostReportRepository;
import com.api.bkhouse.util.Util;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ForumPostService {
    @Autowired
    private ForumPostRepository repository;

    @Autowired
    private ForumPostLikeRepository forumPostLikeRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private PostReportRepository postReportRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional
    public void save(ForumPost forumPost, UUID userId, boolean isUpdate) {
        if (isUpdate) {
            forumPost.setUpdateBy(userId);
            forumPost.setUpdateAt(Util.getCurrentDateTime());
        } else {
            forumPost.setCreateAt(Util.getCurrentDateTime());
            forumPost.setCreateBy(userId);
        }
        repository.save(forumPost);
    }

    public boolean existsById(UUID id) {
        return repository.existsByIdAndEnable(id, true);
    }

    public ForumPost findById(UUID id) {
        Optional<ForumPost> forumPost = repository.findByIdAndEnable(id, true);
        if (forumPost.isEmpty()) {
            return null;
        }
        return forumPost.get();
    }

    public Page<ForumPost> findByUser(UUID userId, Integer pageSize, Integer page) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createAt").descending());
        return repository.findByCreateByAndEnable(userId, true, pageable);
    }

    public Page<ForumPost> findAllWithPageable(Integer pageSize, Integer page) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createAt").descending());
        return repository.findByEnable(true, pageable);
    }

    @Transactional
    public boolean like(UUID forumPostId, UUID userId) {
        if (forumPostLikeRepository.existsByForumPostIdAndUserId(forumPostId, userId)) {
            forumPostLikeRepository.deleteByForumPostIdAndUserId(forumPostId, userId);
            return false;
        } else {
            ForumPostLike forumPostLike = new ForumPostLike();
            forumPostLike.setForumPostId(forumPostId);
            forumPostLike.setUserId(userId);
            forumPostLike.setCreateBy(userId);
            forumPostLike.setCreateAt(Util.getCurrentDateTime());

            forumPostLikeRepository.save(forumPostLike);
            return true;
        }
    }


    public boolean isLiked(UUID postId, UUID userId) {
        return forumPostLikeRepository.existsByForumPostIdAndUserId(postId, userId);
    }

    public ForumPostLog getLog(UUID postId) {
        ForumPostLog forumPostLog = new ForumPostLog();
        forumPostLog.setNoLikes(forumPostLikeRepository.countByForumPostId(postId));
        forumPostLog.setNoComments(postCommentRepository.countByPostId(postId));
        forumPostLog.setNoReports(postReportRepository.countByPostId(postId));
        return forumPostLog;
    }

    public Object findAllNotByAdmin() {
        return repository.findAllByUser();
    }

    @Transactional
    public void deleteById(UUID postId) {
        repository.deletePostById(postId);
    }

    public ChartOption getChart1Data(Integer month, Integer year) {
        ChartOption chartOption = new ChartOption();
        if (month != 0) {
            int date = Util.getDayOfMonth(month, year);
            for (int i = 1; i <= date; i++) {
                String dateStr = year + "-" + month + "-" + i;
                String query = "select count(*) as cnt\n" +
                        "from forum_post \n" +
                        "where date(create_at) = :ngay";
                Map<String, Object> params = new HashMap<>();
                params.put("ngay", dateStr);
                SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
                Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(query, sqlParameterSource);
                chartOption.getXaxis().add(i);
                chartOption.getSeries().add(jdbcResponse.get("cnt"));
            }
        } else {
            int currMonth = Util.getCurrMonth(year);
            for (int i = 1; i <= currMonth; i++) {
                String query = "select count(*) as cnt\n" +
                        "from forum_post \n" +
                        "where month(create_at) = :month\n" +
                        "and year(create_at) = :year";
                Map<String, Object> params = new HashMap<>();
                params.put("month", i);
                params.put("year", year);
                SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
                Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(query, sqlParameterSource);
                chartOption.getXaxis().add(i);
                chartOption.getSeries().add(jdbcResponse.get("cnt"));
            }
        }
        return chartOption;
    }

    public ChartOption getChart2Data(Integer month, Integer year) {
        ChartOption chartOption = new ChartOption();
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("LIKE", "Thích");
        typeMap.put("COMMENT", "Bình luận");
        typeMap.put("REPORT", "Báo cáo");
        int val;
        if (month != 0) {
            val = Util.getDayOfMonth(month, year);
        } else {
            val = Util.getCurrMonth(year);
        }
        for (int i = 1; i <= val; i++) {
            chartOption.getXaxis().add(i);
        }
        for (Map.Entry<String, String> iterator: typeMap.entrySet()) {
            Series series = new Series();
            series.setName(iterator.getValue());
            if (month != 0) {
                for (int i = 1; i <= val; i++) {
                    String dateStr = year + "-" + month + "-" + i;
                    series.getData().add(chart2CounterFunc2(dateStr, iterator.getKey()));
                }
            } else {
                for (int i = 1; i <= val; i++) {
                    series.getData().add(chart2CounterFunc1(i, year, iterator.getKey()));
                }
            }
            chartOption.getSeries().add(series);
        }
        return chartOption;
    }

    private Long chart2CounterFunc1(Integer month, Integer year, String mapType) {
        String query;
        if (mapType.equals("COMMENT")) {
            query = "select count(*) as cnt\n" +
                    "from post_comment pc inner join forum_post fp on pc.post_id = fp.id\n" +
                    "where year(pc.create_at) = :year\n" +
                    "and month(pc.create_at) = :month";
        } else if (mapType.equals("LIKE")) {
            query = "select count(*) as cnt\n" +
                    "from forum_post_like fpl\n" +
                    "where year(fpl.create_at) = :year\n" +
                    "and month(fpl.create_at) = :month";
        } else {
            query = "select count(*) as cnt\n" +
                    "from post_report pc inner join forum_post fp on pc.post_id = fp.id\n" +
                    "where year(pc.create_at) = :year\n" +
                    "and month(pc.create_at) = :month";
        }
        Map<String, Object> params = new HashMap<>();
        params.put("month", month);
        params.put("year", year);
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
        Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(query, sqlParameterSource);
        Long val = (Long) jdbcResponse.get("cnt");
        return val;
    }

    private Long chart2CounterFunc2(String date, String mapType) {
        String query;
        if (mapType.equals("COMMENT")) {
            query = "select count(*) as cnt\n" +
                    "from post_comment pc inner join forum_post fp on pc.post_id = fp.id\n" +
                    "where date(pc.create_at) = :date";
        } else if (mapType.equals("LIKE")) {
            query = "select count(*) as cnt\n" +
                    "from forum_post_like fpl\n" +
                    "where date(fpl.create_at) = :date";
        } else {
            query = "select count(*) as cnt\n" +
                    "from post_report pc inner join forum_post fp on pc.post_id = fp.id\n" +
                    "where date(pc.create_at) = :date";
        }
        Map<String, Object> params = new HashMap<>();
        params.put("date", date);
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource(params);
        Map<String, Object> jdbcResponse = jdbcTemplate.queryForMap(query, sqlParameterSource);
        Long val = (Long) jdbcResponse.get("cnt");
        return val;
    }
}
