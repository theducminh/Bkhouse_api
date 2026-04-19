package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.bkhouse.entity.Charge;
import com.api.bkhouse.repository.ChargeRepository;

import java.util.List;

@Service
public class ChargeService {
    @Autowired
    private ChargeRepository repository;

    @Transactional
    public Charge insert(Charge charge) {
        return repository.save(charge);
    }

    @Transactional
    public Charge update(Charge charge) {
        return repository.save(charge);
    }

    public List<Charge> findAll() {
        return repository.findAll();
    }

    public List<Charge> findByUserId(String userId) {
        return repository.findByUserIdOrderByCreateAtDesc(userId);
    }
}
