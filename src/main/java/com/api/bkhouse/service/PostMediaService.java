package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.PostMedia;
import com.api.bkhouse.repository.PostMediaRepository;

import java.util.List;
import java.util.UUID;

@Service
public class PostMediaService {
    
    private final PostMediaRepository repository;
    public PostMediaService(PostMediaRepository repository) {
        this.repository = repository;
    }

    public List<PostMedia> findByPostId(UUID postId) {
        return repository.findByPostId(postId);
    }

    public PostMedia findById(UUID id) {
        return repository.findByPostId(id).stream().findFirst().orElse(null);
    }

    @Transactional
    public void save(PostMedia postMedia) {
        repository.save(postMedia);
    }

    @Transactional
    public void deleteByPostId(UUID postId) {
        repository.deleteByPostId(postId);
    }

    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

}
