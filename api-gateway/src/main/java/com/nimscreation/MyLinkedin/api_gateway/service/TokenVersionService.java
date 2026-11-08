package com.nimscreation.MyLinkedin.api_gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TokenVersionService {

    private final ReactiveStringRedisTemplate redisTemplate;

    public Mono<Boolean> isCurrentVersion(
            String userId,
            Integer tokenVersion) {

        String key = "user:token-version:" + userId;

        return redisTemplate.opsForValue()
                .get(key)
                .map(currentVersion ->
                        currentVersion.equals(String.valueOf(tokenVersion))
                )
                .defaultIfEmpty(false);
    }
}
