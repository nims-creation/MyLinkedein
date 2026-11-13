
package com.nimscreation.MyLinkedin.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

        if (connectionFactory instanceof LettuceConnectionFactory lettuce) {
            System.out.println("========== GATEWAY REDIS CONFIG ==========");
            System.out.println("Redis Host: " + lettuce.getHostName());
            System.out.println("Redis Port: " + lettuce.getPort());
            System.out.println("==========================================");
        }

        return new ReactiveStringRedisTemplate(connectionFactory);
    }
}
