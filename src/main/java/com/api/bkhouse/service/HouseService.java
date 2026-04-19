package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.bkhouse.entity.House;
import com.api.bkhouse.repository.HouseRepository;
import java.util.UUID;

@Service
public class HouseService {
    @Autowired
    private HouseRepository repository;

    public House findByRealEstatePostId(UUID id) {
        return repository.findByRealEstatePostId(id).get();
    }

    @Transactional
    public House create(House house) {
        return repository.save(house);
    }

    @Transactional
    public House update(House house) {
        return repository.save(house);
    }

    public void deleteByRealEstatePostId(UUID realEstatePostId) {
        repository.deleteByRealEstatePostId(realEstatePostId);
    }
}
