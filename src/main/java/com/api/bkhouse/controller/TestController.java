package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.api.bkhouse.config.annotation.CurrentUser;
import com.api.bkhouse.entity.User;
import com.api.bkhouse.payload.dto.UserDTO;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.security.services.UserDetailsImpl;
import com.api.bkhouse.service.NotifyService;
import com.api.bkhouse.service.RealEstatePostService;
import com.api.bkhouse.service.TestService;
import com.api.bkhouse.util.Util;

import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    private final TestService testService;

    private final ModelMapper modelMapper;

  
    private final NotifyService notifyService;

  
    private final RealEstatePostService realEstatePostService;

    public TestController(TestService testService, ModelMapper modelMapper, NotifyService notifyService, RealEstatePostService realEstatePostService) {
        this.testService = testService;
        this.modelMapper = modelMapper;
        this.notifyService = notifyService;
        this.realEstatePostService = realEstatePostService;
    }

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/mod")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public String moderatorAccess() {
        return "Moderator Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

    @GetMapping("/all-users")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
   public ResponseEntity<BaseResponse> getAllUsers() {
        List<User> users = testService.getAllUsers();
        if (users.isEmpty()) { 
            return ResponseEntity.ok(new BaseResponse(null, "Không tìm thấy user nào", HttpStatus.NOT_FOUND));
        } else {
            List<UserDTO> userDTOS = users
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new BaseResponse(userDTOS, "", HttpStatus.OK));
        }
    }

    @PostMapping("/notify")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> notifyToAll() {
        try {
            notifyService.notifyToAllUsers("Hello world");
            return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi test notify: ", e);
            return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/current-user")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> getCurrentUser(@CurrentUser UserDetailsImpl currentUser) {
        try {
            return ResponseEntity.ok(new BaseResponse(currentUser, "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi test current user: ", e);
            return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/rep/statistic")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse> abc(@RequestParam String date) {
        try {
            realEstatePostService.calculatePricePerAreaUnit(date);
            return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.OK));
        } catch (Exception e) {
            logger.error("Lỗi khi test thống kê rep: ", e);
            return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/current-time")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> getCurrentDateTime() {
        return ResponseEntity.ok(new BaseResponse(Util.getCurrentDateTime(), "", HttpStatus.OK));
    }

    private UserDTO convertToDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
