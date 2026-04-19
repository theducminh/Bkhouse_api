package com.api.bkhouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.bkhouse.entity.User;
import com.api.bkhouse.repository.UserRepository;
import com.api.bkhouse.util.Util;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public User findById(UUID id) {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) return null;
        return user.get();
    }

    @Transactional
    public User updateUserInfo(User user) {
        user.setUpdatedAt(Util.getCurrentDateTime());
        return repository.save(user);
    }

    public List<User> findAll() {
        return repository.findByUsernameNot("anonymous");
    }

    public List<String> listRoles(UUID userId) {
        return repository.findRolesByUserId(userId);
    }
}
