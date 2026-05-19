package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.UserDeviceToken;
import com.api.bkhouse.repository.UserDeviceTokenRepository;
import com.api.bkhouse.util.Util;

//import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserDeviceTokenService {
    
    private final UserDeviceTokenRepository repository;

    public UserDeviceTokenService(UserDeviceTokenRepository repository) {
        this.repository = repository;
    }

    public UserDeviceToken findByUserIdAndDeviceInfo(UUID userId, String deviceInfo) {
        return repository.findByUserIdAndDeviceInfo(userId, deviceInfo).orElse(null);
    }

    @Transactional
    public void update(UserDeviceToken userDeviceToken) {
        repository.save(userDeviceToken);
    }

    @Transactional
    public void create(UserDeviceToken userDeviceToken) {
        UserDeviceToken repoSave = findByUserIdAndDeviceInfo(userDeviceToken.getUserId(), userDeviceToken.getDeviceInfo());
        if (repoSave != null) {
            repoSave.setNotifyToken(userDeviceToken.getNotifyToken());
            repoSave.setEnable(true);
            repoSave.setLogout(false);
            repoSave.setUpdateAt(Util.getCurrentDateTime());
        } else {
            repoSave = userDeviceToken;
        }
        repository.save(repoSave);
    }

     
}
