package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.bkhouse.entity.UserDeviceToken;
import com.api.bkhouse.repository.UserDeviceTokenRepository;
import com.api.bkhouse.util.Util;

//import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserDeviceTokenService {
    @Autowired
    private UserDeviceTokenRepository repository;

    public UserDeviceToken findByUserIdAndDeviceInfo(UUID userId, String deviceInfo) {
        Optional<UserDeviceToken> userDeviceToken = repository.findByUserIdAndDeviceInfo(userId, deviceInfo);
        if (userDeviceToken.isEmpty()) {
            return null;
        }
        return userDeviceToken.get();
    }

    @Transactional
    public void update(UserDeviceToken userDeviceToken) {
        repository.save(userDeviceToken);
    }

    @Transactional
    public void create(UserDeviceToken userDeviceToken) {
        UserDeviceToken repoSave = findByUserIdAndDeviceInfo(userDeviceToken.getUserId(), userDeviceToken.getDeviceInfo());
        if (repoSave != null) {
            repoSave.setLogout(false);
            repoSave.setUpdateAt(Util.getCurrentDateTime());
        } else {
            repoSave = userDeviceToken;
        }
        repository.save(repoSave);
    }
}
