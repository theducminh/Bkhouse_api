package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.bkhouse.entity.Apartment;
import com.api.bkhouse.repository.ApartmentRepository;
import java.util.UUID;

@Service
public class ApartmentService {
    @Autowired
    private ApartmentRepository repository;

    public Apartment findByRealEstatePostId(UUID id) {
        return repository.findByRealEstatePostId(id).get();
    }

    @Transactional
    public Apartment create(Apartment apartment) {
        return repository.save(apartment);
    }

    @Transactional
    public Apartment update(Apartment apartment) {
        return repository.save(apartment);
    }

    public void deleteByRealEstatePostId(UUID realEstatePostId) {
        repository.deleteByRealEstatePostId(realEstatePostId);
    }
}
