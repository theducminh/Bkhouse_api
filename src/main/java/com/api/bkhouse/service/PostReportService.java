package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.PostReport;
import com.api.bkhouse.repository.PostReportRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class PostReportService {
    @Autowired
    private PostReportRepository repository;

    @Transactional
    public void save(PostReport postReport) {
        repository.save(postReport);
    }
    public Object getAllStatistic() {
        return repository.getListReportData();
    }

    public List<PostReport> findByPostId(UUID postId) {
        return repository.findByPostIdOrderByCreateAtDesc(postId);
    }
}
