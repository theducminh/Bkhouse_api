package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.Province;
import com.api.bkhouse.repository.ProvinceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProvinceService {
    @Autowired
    private ProvinceRepository repository;

    public List<Province> getAll() {
        return repository.findAll();
    }

    public Province findByCode(String code) {
        Optional<Province> provinceOptional = repository.findByCode(code);
        if (provinceOptional.isEmpty()) {
            return null;
        }
        return provinceOptional.get();
    }
}
