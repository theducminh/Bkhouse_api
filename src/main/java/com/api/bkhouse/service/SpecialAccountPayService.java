package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.SpecialAccountPay;
import com.api.bkhouse.repository.SpecialAccountPayRepository;

import java.util.List;

@Service
public class SpecialAccountPayService {
    @Autowired
    private SpecialAccountPayRepository repository;

    public List<SpecialAccountPay> getSpecialAccountPaysByUserId(String userId) {
        return repository.findByUserIdOrderByCreateAtDesc(userId);
    }

    public List<SpecialAccountPay> findAllSpecialAccountPays() {
        List<SpecialAccountPay> specialAccountPays =
                repository.findAll(Sort.by(Sort.Direction.DESC, "createAt"));
        return specialAccountPays;
    }
}
