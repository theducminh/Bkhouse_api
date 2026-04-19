package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.Ward;
import com.api.bkhouse.repository.WardRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WardService {
    @Autowired
    private WardRepository wardRepository;

    public List<Ward> getAll() {
        return wardRepository.findAll();
    }

    public Ward findByCode(String code) {
        Optional<Ward> repoReturn = wardRepository.findByCode(code);
        if (repoReturn.isEmpty()) return null;
        return repoReturn.get();
    }

    public List<Ward> getAllWardsOfDistrict(String districtCode) {
        return wardRepository.findByDistrictCode(districtCode);
    }
}
