package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.AdministrativeUnit;
import com.api.bkhouse.repository.AdministrativeUnitRepository;

import java.util.List;

@Service
public class AdministrativeUnitService {

    @Autowired
    private AdministrativeUnitRepository administrativeUnitRepository;

    public List<AdministrativeUnit> getAll() {
        return administrativeUnitRepository.findAll();
    }
}
