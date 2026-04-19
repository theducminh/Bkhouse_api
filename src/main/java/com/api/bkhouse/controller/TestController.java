package com.api.bkhouse.controller;

import org.modelmapper.ModelMapper;
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
    @Autowired
    private TestService testService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private RealEstatePostService realEstatePostService;

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
        if (users.size() == 0) {
            return ResponseEntity.ok(new BaseResponse(null, "Khong tim thay user nao", HttpStatus.NOT_FOUND));
        } else {
            List<UserDTO> userDTOS = users
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new BaseResponse(users, "", HttpStatus.OK));
        }
    }

    @PostMapping("/notify")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> notifyToAll() {
        try {
            notifyService.notifyToAllUsers("Hello world");
            return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponse(null, "", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/current-user")
    @PreAuthorize("hasRole('ROLE_AGENCY') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_ENTERPRISE')")
    public ResponseEntity<BaseResponse> getCurrentUser(@CurrentUser UserDetailsImpl currentUser) {
        try {
//            UserDetails userDetails = (UserDetailsImpl) authentication.getDetails();
//            return ResponseEntity.ok(new BaseResponse(authentication.getDetails(), "", HttpStatus.OK));
            return ResponseEntity.ok(new BaseResponse(currentUser, "", HttpStatus.OK));
        } catch (Exception e) {
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
            e.printStackTrace();
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
