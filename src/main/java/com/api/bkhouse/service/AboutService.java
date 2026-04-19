package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.bkhouse.entity.About;
import com.api.bkhouse.repository.AboutRepository;

@Service
public class AboutService {
    @Autowired
    private AboutRepository repository;

    public About get() {
        return repository.findById(1).get();
    }

    @Transactional
    public About update(About about) {
        return repository.save(about);
    }
}
