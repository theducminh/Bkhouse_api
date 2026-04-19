package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.PostMedia;
import com.api.bkhouse.repository.PostMediaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class PostMediaService {
    @Autowired
    private PostMediaRepository repository;

    public List<PostMedia> findByPostId(UUID postId) {
        return repository.findByPostId(postId);
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
    public void deleteById(String id) {
        repository.deleteById(id);
    }

}
