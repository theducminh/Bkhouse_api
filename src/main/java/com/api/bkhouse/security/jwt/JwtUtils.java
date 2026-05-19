package com.api.bkhouse.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.api.bkhouse.security.services.UserDetailsImpl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${ducnm.app.jwtSecret}")
    private String jwtSecret;

    @Value("${ducnm.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // Helper để tạo Key từ chuỗi Secret trong application.properties
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("authorities: {}", userPrincipal.getAuthorities());
        return generateTokenFromUsername(userPrincipal.getUsername(), userPrincipal.getId());
    }

    public String generateTokenFromUsername(String username, UUID id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("username", username);

        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey()) // Cách dùng mới cho 0.12.x
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Thay cho setSigningKey
                .build()
                .parseSignedClaims(token)    // Thay cho parseClaimsJws
                .getPayload()                // Thay cho getBody
                .get("username", String.class);
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (JwtException e) {
            // Chuẩn 0.12.x gom các lỗi vào JwtException, hoặc bắt lẻ từng cái như cũ
            logger.error("JWT validation error: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}