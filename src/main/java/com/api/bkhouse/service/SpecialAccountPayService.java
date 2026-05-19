package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.SpecialAccountPay;
import com.api.bkhouse.repository.SpecialAccountPayRepository;

import java.util.List;
import java.util.UUID;

@Service
public class SpecialAccountPayService {
    
    private final SpecialAccountPayRepository repository;

    public SpecialAccountPayService(SpecialAccountPayRepository repository) {
        this.repository = repository;
    }

    public List<SpecialAccountPay> getSpecialAccountPaysByUserId(UUID userId) {
        return repository.findByUserIdOrderByCreateAtDesc(userId);
    }

    public List<SpecialAccountPay> findAllSpecialAccountPays() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createAt"));
    }
}
