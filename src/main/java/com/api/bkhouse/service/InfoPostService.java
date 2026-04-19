package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.InfoPost;
import com.api.bkhouse.entity.response.IInfoPost;
import com.api.bkhouse.payload.response.chart.ChartOption;
import com.api.bkhouse.repository.InfoPostRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InfoPostService {
    @Autowired
    private InfoPostRepository repository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<InfoPost> findAll() {
        List<InfoPost> infoPosts = repository.findAll(Sort.by(Sort.Direction.DESC, "createAt"));
        return infoPosts;
    }

    @Transactional
    public InfoPost update(InfoPost infoPost) {
        InfoPost infoPost1 = repository.save(infoPost);
        return infoPost1;
    }

    @Transactional
    public InfoPost create(InfoPost infoPost) {
        InfoPost infoPost1 = repository.save(infoPost);
        return infoPost1;
    }

    public InfoPost findById(Long id) {
        Optional<InfoPost> infoPostOptional = repository.findById(id);
        if (infoPostOptional.isEmpty()) {
            return null;
        }
        return infoPostOptional.get();
    }

    public List<InfoPost> findByUserId(String userId) {
        List<InfoPost> infoPosts = repository.findByCreateByOrderByCreateAtDesc(userId);
        return infoPosts;
    }

    public List<InfoPost> findByTypeId(Integer typeId) {
        return repository.findTop5ByInfoTypeIdOrderByCreateAtDesc(typeId);
    }

    public List<InfoPost> loadMore(Integer typeId, Integer limit, Integer page) {
        Pageable pageable = PageRequest.of(page, limit);
        return repository.findByInfoTypeIdOrderByCreateAtDesc(typeId, pageable);
    }

    public Long countByInfoTypeId(Integer infoTypeId) {
        return repository.countByInfoTypeId(infoTypeId);
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Object getHomePagePosts() {
//        List<IInfoPost> iInfoPosts = new ArrayList<>();
//        iInfoPosts.addAll(repository.getTop3OrderByView());
//        iInfoPosts.addAll(repository.getTop2InfoPostNewest());
//        return iInfoPosts;
        return repository.getTop5InfoPostNewest();
    }

    public Object getHomePageDuAnPosts() {
        return repository.bestProjects();
    }

    public ChartOption getChar1Options() {
        String query = "select count(ip.id) as cnt, it.name\n" +
                "from info_type it inner join info_post ip on it.id = ip.info_type_id\n" +
                "group by it.name;";
        List<Map<String, Object>> jdbcResponse = jdbcTemplate.queryForList(query, new MapSqlParameterSource());
        ChartOption chartOption = new ChartOption();
        jdbcResponse
                .stream()
                .forEach(e -> {
                    chartOption.getXaxis().add(e.get("name"));
                    chartOption.getSeries().add(e.get("cnt"));
                });
        return chartOption;
    }

    public ChartOption getChar2Options() {
        String query = "select sum(ip.view) as cnt, it.name\n" +
                "from info_type it inner join info_post ip on it.id = ip.info_type_id\n" +
                "group by it.name;";
        List<Map<String, Object>> jdbcResponse = jdbcTemplate.queryForList(query, new MapSqlParameterSource());
        ChartOption chartOption = new ChartOption();
        jdbcResponse
                .stream()
                .forEach(e -> {
                    chartOption.getXaxis().add(e.get("name"));
                    chartOption.getSeries().add(e.get("cnt"));
                });
        return chartOption;
    }
}
