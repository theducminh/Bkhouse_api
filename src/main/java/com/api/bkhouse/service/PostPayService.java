package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.bkhouse.entity.PostPay;
import com.api.bkhouse.repository.PostPayRepository;

import java.util.List;
import java.util.UUID;

@Service
public class PostPayService {
    @Autowired
    private PostPayRepository repository;

    @Transactional
    public PostPay createPostPay(PostPay postPay) {
        return repository.save(postPay);
    }

    public List<PostPay> findByUserId(UUID userId) {
        return repository.findByUserIdOrderByCreateAtDesc(userId);
    }

    public List<PostPay> findAllPostPays() {
        List<PostPay> postPays = repository.findAll(Sort.by(Sort.Direction.ASC, "createAt"));
        return postPays;
    }
}
