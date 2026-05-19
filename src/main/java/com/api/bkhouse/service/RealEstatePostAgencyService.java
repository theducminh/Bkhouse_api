package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.api.bkhouse.constant.enumeric.ERepAgencyStatus;
import com.api.bkhouse.entity.RealEstatePostAgency;
import com.api.bkhouse.repository.RealEstatePostAgencyRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RealEstatePostAgencyService {
    
    private final RealEstatePostAgencyRepository repository;

    public RealEstatePostAgencyService(RealEstatePostAgencyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public RealEstatePostAgency save(RealEstatePostAgency realEstatePostAgency) {
        return repository.save(realEstatePostAgency);
    }

    @Transactional
    public void saveAll(List<RealEstatePostAgency> realEstatePostAgencies) {
        repository.saveAll(realEstatePostAgencies);
    }

    @Transactional
    public Long updateStatus(RealEstatePostAgency realEstatePostAgency) {
        RealEstatePostAgency realEstatePostAgency1 = repository.save(realEstatePostAgency);
        if (realEstatePostAgency1.getStatus().equals(ERepAgencyStatus.DA_XAC_NHAN)) {
            repository.updateRep(realEstatePostAgency1.getRealEstatePostId());
        }
        return realEstatePostAgency1.getId();
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<RealEstatePostAgency> findByAgencyId(UUID agencyId) {
        return repository.findByAgencyId(agencyId);
    }

    public List<RealEstatePostAgency> findByCreateBy(UUID userId) {
        return repository.findByCreateBy(userId);
    }

    public boolean inArea(UUID repId, UUID agencyId) {
        Long count = repository.checkInArea(repId, agencyId);
        return count != null && count > 0;
    }

    public Optional<RealEstatePostAgency> findById(Long id) {
        return repository.findById(id);
    }
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
