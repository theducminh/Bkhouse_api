package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.bkhouse.entity.PriceFluctuation;
import com.api.bkhouse.repository.PriceFluctuationRepository;

import java.util.List;
import java.util.UUID;

@Service
public class PriceFluctuationService {
    
    private final PriceFluctuationRepository repository;
    public PriceFluctuationService(PriceFluctuationRepository repository) {
        this.repository = repository;
    }

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
        // 1. Lấy toàn bộ các đăng ký theo dõi giá của user này
        List<PriceFluctuation> userConfigs = repository.findByUserId(userId);
        
        // 2. Nếu họ có đăng ký thì mới cập nhật
        if (userConfigs != null && !userConfigs.isEmpty()) {
            for (PriceFluctuation config : userConfigs) {
                // Giả sử trong Entity của bác biến này tên là isEnabled (hoặc enable)
                config.setEnable(enable); 
            }
            // 3. Lưu lại toàn bộ list (Spring Data JPA tối ưu hàm này chạy rất nhanh)
            repository.saveAll(userConfigs);
        }
    }
}
