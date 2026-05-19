package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.PostReport;
import com.api.bkhouse.repository.PostReportRepository;
import com.api.bkhouse.entity.response.IReportData;


import java.util.List;
import java.util.UUID;

@Service
public class PostReportService {
    
    private final PostReportRepository repository;

    public PostReportService(PostReportRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void save(PostReport postReport) {
        repository.save(postReport);
    }
    public List<IReportData> getAllStatistic() {
        return repository.getListReportData();
    }

    public List<PostReport> findByPostId(UUID postId) {
        return repository.findByPostIdOrderByCreateAtDesc(postId);
    }
}
