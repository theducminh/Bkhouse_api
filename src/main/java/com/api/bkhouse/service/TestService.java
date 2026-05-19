package com.api.bkhouse.service;

import org.springframework.stereotype.Service;

import com.api.bkhouse.entity.User;
import com.api.bkhouse.repository.UserRepository;

import java.util.List;

@Service
public class TestService {
    
    private final UserRepository userRepository;

    // Chuẩn Spring Boot: Constructor Injection thay cho @Autowired
    public TestService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}