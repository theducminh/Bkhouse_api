package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.PriceFluctuation;
import com.api.bkhouse.repository.PriceFluctuationRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class PriceFluctuationService {
    @Autowired
    private PriceFluctuationRepository repository;

    public List<PriceFluctuation> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    @Transactional
    public void save(PriceFluctuation priceFluctuation) {
        repository.save(priceFluctuation);
    }

    @Transactional
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(userId);
    }

    @Transactional
    public void updateStatus(boolean enable, UUID userId) {
        if (enable) {

        }
    }
}
