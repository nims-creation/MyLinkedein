
package com.nimscreation.MyLinkedin.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenVersionService {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "user:token-version:";

    private String getKey(Long userId) {
        return KEY_PREFIX + userId;
    }

    public void initializeIfAbsent(Long userId, Integer version) {
        redisTemplate.opsForValue().setIfAbsent(
                getKey(userId),
                String.valueOf(version)
        );
    }

    public void updateVersion(Long userId, Integer version) {
        redisTemplate.opsForValue().set(
                getKey(userId),
                String.valueOf(version)
        );
    }

    public void deleteVersion(Long userId) {
        redisTemplate.delete(getKey(userId));
    }

    public boolean isCurrentVersion(Long userId, Integer tokenVersion) {

        String currentVersion = redisTemplate.opsForValue()
                .get(getKey(userId));

        if (currentVersion == null) {
            return false;
        }

        try {
            return Integer.valueOf(currentVersion).equals(tokenVersion);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
