package com.nimscreation.MyLinkedin.user_service.service;

import com.nimscreation.MyLinkedin.user_service.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.security.MessageDigest;
import java.util.HexFormat;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*100))
                .signWith(getSecretKey())
                .compact();
    }

    @PostConstruct
    public void checkJwtConfiguration() throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-256")
                .digest(jwtSecretKey.getBytes(StandardCharsets.UTF_8));

        System.out.println("USER SERVICE JWT FINGERPRINT: "
                + HexFormat.of().formatHex(hash));
    }

}
