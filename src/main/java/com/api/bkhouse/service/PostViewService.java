package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.PostView;
import com.api.bkhouse.payload.dto.PostViewDTO;
import com.api.bkhouse.repository.PostViewRepository;
import com.api.bkhouse.repository.RealEstatePostRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class PostViewService {

    
    private final PostViewRepository repository;
    private final RealEstatePostRepository realEstatePostRepository;
    public PostViewService(PostViewRepository repository, RealEstatePostRepository realEstatePostRepository) {
        this.repository = repository;
        this.realEstatePostRepository = realEstatePostRepository;
    }

    @Transactional
    public void recordView(PostViewDTO dto, String ipAddress) {
        // Cài đặt thời gian block spam view là 30 phút
        Instant thirtyMinsAgo = Instant.now().minus(30, ChronoUnit.MINUTES);

        // Check xem có phải spam view không
        boolean isSpam = repository.existsRecentView(
                dto.getRealEstatePostId(), 
                ipAddress, 
                dto.getDeviceId(), 
                thirtyMinsAgo
        );

        // Nếu KHÔNG PHẢI spam thì mới cộng view vào DB
        if (!isSpam) {
            PostView view = new PostView();
            view.setRealEstatePostId(dto.getRealEstatePostId());
            view.setUserId(dto.getUserId());
            view.setDeviceId(dto.getDeviceId());
            view.setIpAddress(ipAddress);
            view.setCreatedAt(Instant.now());
            
            repository.save(view);

            realEstatePostRepository.updateView(dto.getRealEstatePostId());
        }
    }

    public long getTotalViews(UUID postId) {
        return repository.countByRealEstatePostId(postId);
    }
}