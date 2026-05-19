package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.bkhouse.entity.Plot;
import com.api.bkhouse.repository.PlotRepository;
import java.util.UUID;

@Service
public class PlotService {
    
    private final PlotRepository repository;

    public PlotService(PlotRepository repository) {
        this.repository = repository;
    }

    public Plot findByRealEstatePostId(UUID id) {
        return repository.findByRealEstatePostId(id).orElse(null);
    }

    @Transactional
    public Plot create(Plot plot) {
        return repository.save(plot);
    }

    @Transactional
    public Plot update(Plot plot) {
        return repository.save(plot);
    }

    @Transactional
    public void deleteByRealEstatePostId(UUID realEstatePostId) {
        repository.deleteByRealEstatePostId(realEstatePostId);
    }
}
