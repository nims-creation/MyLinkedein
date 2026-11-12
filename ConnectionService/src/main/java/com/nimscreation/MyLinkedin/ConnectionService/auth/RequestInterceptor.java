
package com.nimscreation.MyLinkedin.ConnectionService.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.PostConstruct;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Objects;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class RequestInterceptor implements HandlerInterceptor {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    private final StringRedisTemplate redisTemplate;

    public RequestInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                jwtSecretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ") ||
                authHeader.length() <= 7) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Missing or invalid token"
            );
            return false;
        }

        try {
            String token = authHeader.substring(7).trim();

            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = Long.valueOf(claims.getSubject());

            Integer tokenVersion =
                    claims.get("tokenVersion", Integer.class);

            if (tokenVersion == null) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Token version missing"
                );
                return false;
            }

            String redisKey = "user:token-version:" + userId;

            String currentVersion = redisTemplate.opsForValue()
                    .get(redisKey);

            if (currentVersion == null) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Token version not found"
                );
                return false;
            }

            if (!Objects.equals(
                    currentVersion,
                    String.valueOf(tokenVersion))) {

                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Token has been revoked"
                );
                return false;
            }

            AuthContextHolder.setCurrentUserId(userId);

            return true;

        } catch (Exception e) {

            log.error("JWT/Redis validation failed: {}", e.getMessage());

            if (e instanceof org.springframework.data.redis.RedisConnectionFailureException
                    || e.getCause() instanceof org.springframework.data.redis.RedisConnectionFailureException) {

                response.sendError(
                        HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                        "Authentication service temporarily unavailable"
                );
                return false;
            }

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired JWT token"
            );

            return false;
        }
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {

        AuthContextHolder.clear();
    }

    @PostConstruct
    public void checkJwtConfiguration() throws Exception {

        byte[] hash = MessageDigest.getInstance("SHA-256")
                .digest(jwtSecretKey.getBytes(StandardCharsets.UTF_8));

        log.info(
                "CONNECTION SERVICE JWT FINGERPRINT: {}",
                HexFormat.of().formatHex(hash)
        );
    }
}
