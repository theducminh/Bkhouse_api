// đường dẫn: src/main/java/com/api/bkland/service/AuthService.java
package com.api.bkhouse.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.bkhouse.constant.enumeric.ERole;
import com.api.bkhouse.entity.*;
import com.api.bkhouse.exception.TokenRefreshException;
import com.api.bkhouse.payload.dto.RoleDTO;
//import com.api.bkhouse.payload.dto.RoleDTO;
import com.api.bkhouse.payload.dto.UserDTO;
import com.api.bkhouse.payload.request.ForgotPassword;
import com.api.bkhouse.payload.request.LoginRequest;
import com.api.bkhouse.payload.request.TokenRefreshRequest;
import com.api.bkhouse.payload.response.BaseResponse;
import com.api.bkhouse.payload.response.JwtResponse;
import com.api.bkhouse.payload.response.TokenRefreshResponse;
import com.api.bkhouse.repository.RoleRepository;
import com.api.bkhouse.repository.UserDeviceTokenRepository;
import com.api.bkhouse.repository.UserRepository;
import com.api.bkhouse.security.jwt.JwtUtils;
import com.api.bkhouse.security.services.UserDetailsImpl;
import com.api.bkhouse.util.Util;
import com.api.bkhouse.repository.SpecialAccountRepository;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class AuthService {
    @Value("${app.google.client-id}")
    private String googleClientId;

    // Giả sử bạn đã @Autowired các Bean này:
    // @Autowired UserRepository userRepository;
    // @Autowired PasswordEncoder encoder;
    // @Autowired JwtUtils jwtUtils;

    public BaseResponse processGoogleLogin(String idTokenString, String deviceInfo) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String firstName = (String) payload.get("given_name");
                String lastName = (String) payload.get("family_name");
                String pictureUrl = (String) payload.get("picture");

                User user = userRepository.findByEmail(email).orElse(null);

                if (user == null) {
                    user = new User();
                    user.setId(UUID.randomUUID());
                    user.setEmail(email);
                    user.setUsername(email.split("@")[0] + "_gg_" + UUID.randomUUID().toString().substring(0, 5));
                    user.setPassword(encoder.encode(UUID.randomUUID().toString()));
                    user.setFirstName(firstName != null ? firstName : "User");
                    user.setLastName(lastName != null ? lastName : "");
                    user.setAvatarUrl(pictureUrl);
                    user.setEnabled(true);
                    user.setAccountBalance(0L);

                    Role defaultRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    user.setRoles(Collections.singleton(defaultRole));

                    user = userRepository.save(user);
                    
                } else if (!user.isEnabled()) {
                    return new BaseResponse(null, "Tài khoản của bạn đã bị khóa", HttpStatus.FORBIDDEN);
                }

                String jwt = jwtUtils.generateTokenFromUsername(user.getUsername(), user.getId());

                RefreshToken refreshTokenObj = refreshTokenService.createRefreshToken(user.getId());
                String refreshTokenStr = refreshTokenObj.getToken();

                List<String> roles = user.getRoles().stream()
                        .map(item -> item.getName().name())
                        .collect(Collectors.toList());

                // ---- ĐÃ SỬA: SỬ DỤNG JwtResponse ĐÚNG THEO FILE CỦA BẠN ----
                JwtResponse jwtResponse = new JwtResponse(
                        jwt, 
                        refreshTokenStr, 
                        user.getId(), 
                        user.getUsername(), 
                        user.getEmail(), 
                        roles
                );

                return new BaseResponse(jwtResponse, "Đăng nhập Google thành công", HttpStatus.OK);

            } else {
                return new BaseResponse(null, "Chữ ký Google Token không hợp lệ", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse(null, "Lỗi Server: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    SpecialAccountRepository specialAccountRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserDeviceTokenRepository userDeviceTokenRepository;
    
    @Transactional
    public BaseResponse authenticateUser(LoginRequest loginRequest) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
            
            if (userOptional.isEmpty()) {
                return new BaseResponse(null, "Tài khoản không tồn tại", HttpStatus.NOT_FOUND);
            }

            User user = userOptional.get();
            if (!user.isEnabled()) {
                return new BaseResponse(null, "Tài khoản đã bị khóa.", HttpStatus.FORBIDDEN);
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            // Xử lý Device Token an toàn cho Supabase
            Optional<UserDeviceToken> deviceTokenOpt = userDeviceTokenRepository
                    .findByUserIdAndDeviceInfo(userDetails.getId(), loginRequest.getDeviceInfo());
            
            if (deviceTokenOpt.isEmpty()) {
                UserDeviceToken deviceToken = new UserDeviceToken();
                // KHÔNG set Id(0), để database tự tăng
                deviceToken.setUserId(userDetails.getId());
                deviceToken.setLogout(false);
                deviceToken.setCreateAt(Util.getCurrentDateTime());
                deviceToken.setCreateBy(userDetails.getId());
                deviceToken.setDeviceInfo(loginRequest.getDeviceInfo());
                deviceToken.setEnable(true); // Thường mặc định cho phép thông báo
                deviceToken.setNotifyToken("");
                userDeviceTokenRepository.save(deviceToken);
            } else {
                UserDeviceToken deviceToken = deviceTokenOpt.get();
                deviceToken.setLogout(false);
                deviceToken.setUpdateBy(userDetails.getId());
                deviceToken.setUpdateAt(Util.getCurrentDateTime());
                userDeviceTokenRepository.save(deviceToken);
            }

            return new BaseResponse(new JwtResponse(
                    jwt,
                    refreshToken.getToken(),
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles
            ), "Đăng nhập thành công", HttpStatus.OK);

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
        // Lỗi này mới thực sự là do gõ sai mật khẩu
        return new BaseResponse(null, "Mật khẩu thực sự không chính xác", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
        // IN LỖI THẬT RA CONSOLE CHO ANH EM MÌNH ĐỌC
        e.printStackTrace(); 
        
        // Trả thẳng lỗi thật ra Swagger
        return new BaseResponse(null, "BUG ĐÂY RỒI: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    }
    
// ...

@Transactional(rollbackFor = Exception.class)
public BaseResponse registerUser(UserDTO signupRequest) {
    // 1. Kiểm tra trùng lặp
    if (userRepository.existsByUsername(signupRequest.getUsername())) {
        return new BaseResponse(null, "Tên đăng nhập đã tồn tại", HttpStatus.BAD_REQUEST);
    }

    if (userRepository.existsByEmail(signupRequest.getEmail())) {
        return new BaseResponse(null, "Email đã được sử dụng", HttpStatus.BAD_REQUEST);
    }

    // 2. Convert từ DTO sang Entity trước
    User user = convertToEntity(signupRequest);
    user.setId(null); // Tạo ID mới cho User, để Hibernate tự quản lý

    // 3. Xử lý các trường nhạy cảm & tự động trực tiếp trên Entity
    // KHÔNG TỰ SET ID NỮA, để @GeneratedValue lo!
    user.setPassword(encoder.encode(signupRequest.getPassword()));
    user.setCreatedAt(Util.getCurrentDateTime()); // Đảm bảo Entity có thuộc tính này
    user.setEnabled(true); 

    // 4. Tuyệt chiêu xử lý Role (Diệt tận gốc TransientObjectException)
    Set<Role> finalRoles = new HashSet<>();
    boolean isEnterprise = false; // Biến cờ để đánh dấu có role enterprise hay không
    
    if (signupRequest.getRoles() == null || signupRequest.getRoles().isEmpty()) {
        // Mặc định là USER nếu không gửi gì lên
        Role defaultRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role User không tìm thấy trong DB."));
        finalRoles.add(defaultRole);
    } else {
        // Duyệt qua danh sách Role gửi lên để lấy Role "THẬT" từ DB
        for (RoleDTO roleDTO : signupRequest.getRoles()) {
            String roleName = roleDTO.getName(); 
            if ("ROLE_ENTERPRISE".equals(roleName)) {
                Role agencyRole = roleRepository.findByName(ERole.ROLE_ENTERPRISE)
                        .orElseThrow(() -> new RuntimeException("Error: Role Enterprise không tìm thấy."));
                finalRoles.add(agencyRole);
                isEnterprise = true; // Gán thoải mái không bị lỗi Java
            } else {
                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role User không tìm thấy."));
                finalRoles.add(userRole);
            }
        }
    }
    
    // Gán Set Role Thật vào User
    user.setRoles(finalRoles);
    User savedUser = userRepository.saveAndFlush(user); // Lưu User trước để có ID

    // 5. Lưu 1 lần duy nhất
    userRepository.saveAndFlush(user);
    if (isEnterprise) {
        SpecialAccount specialAccount = new SpecialAccount();
        specialAccount.setUser(savedUser); // Gán trực tiếp đối tượng User đã có ID
        specialAccount.setMonthlyCharge(0); // Mặc định, có thể cập nhật sau
        specialAccount.setAgency(true);
        specialAccount.setLastPaid(Util.getCurrentDateTime());
        specialAccount.setNotifyBefore(3); // Mặc định thông báo trước 3 ngày
        specialAccountRepository.save(specialAccount);
    }

    return new BaseResponse(user.getId(), "Đăng ký tài khoản thành công", HttpStatus.OK);
}
    // ... Các hàm khác giữ nguyên nhưng đổi package com.api.bkland ...
    
    public BaseResponse refreshToken(TokenRefreshRequest tokenRefreshRequest) {
        String requestRefreshToken = tokenRefreshRequest.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    if (!user.isEnabled()) {
                        return new BaseResponse(null, "Tài khoản đã bị khóa", HttpStatus.FORBIDDEN);
                    }
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername(), user.getId());
                    return new BaseResponse(new TokenRefreshResponse(token, requestRefreshToken), "", HttpStatus.OK);
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token không tồn tại!"));
    }

    // 1. Hàm kiểm tra email tồn tại
public BaseResponse emailExist(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
        return new BaseResponse(null, "", HttpStatus.NO_CONTENT);
    } else {
        // Kiểm tra định dạng username đặc biệt của hệ thống (nếu cần)
        if (user.get().getUsername().equals(user.get().getEmail() + "_user_bkland")) {
            return new BaseResponse(null, "", HttpStatus.OK);
        } else {
            return new BaseResponse(null,
                    "Email đã được sử dụng để đăng ký tài khoản trước đó.",
                    HttpStatus.BAD_REQUEST);
        }
    }
}

// 2. Hàm đổi mật khẩu (Quên mật khẩu)
@Transactional
public boolean changePassword(ForgotPassword forgotPassword) {
    Optional<User> user = userRepository.findByEmail(forgotPassword.getEmail());
    if (user.isEmpty()) {
        return false;
    }
    User userEntity = user.get();
    userEntity.setPassword(encoder.encode(forgotPassword.getNewPassword()));
    userEntity.setUpdatedAt(Util.getCurrentDateTime());
    userRepository.save(userEntity);
    return true;
}

    private User convertToEntity(UserDTO userDTO) {
        return this.modelMapper.map(userDTO, User.class);
    }
}