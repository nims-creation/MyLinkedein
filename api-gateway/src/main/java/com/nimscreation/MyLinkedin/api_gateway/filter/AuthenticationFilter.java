package com.nimscreation.MyLinkedin.api_gateway.filter;

import com.nimscreation.MyLinkedin.api_gateway.service.TokenVersionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j

public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final TokenVersionService tokenVersionService;

    public AuthenticationFilter(TokenVersionService tokenVersionService) {
        super(Config.class);
        this.tokenVersionService = tokenVersionService;
    }


    @Value("${jwt.secretKey}")
    private String jwtSecret;



    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Authorization header missing or invalid");
                return unauthorized(exchange);
            }

            String token = authHeader.substring(7);

            Claims claims;

            try {
                SecretKey secretKey = Keys.hmacShaKeyFor(
                        jwtSecret.getBytes(StandardCharsets.UTF_8)
                );

                claims = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

            } catch (Exception exception) {
                log.warn("JWT validation failed: {}", exception.getMessage());
                return unauthorized(exchange);
            }

            String userId = claims.getSubject();

            Integer tokenVersion;

            try {
                Object versionClaim = claims.get("tokenVersion");

                if (versionClaim == null) {
                    log.warn("JWT tokenVersion claim missing");
                    return unauthorized(exchange);
                }

                tokenVersion = Integer.valueOf(versionClaim.toString());

            } catch (NumberFormatException exception) {
                log.warn("Invalid tokenVersion claim");
                return unauthorized(exchange);
            }

            if (userId == null || userId.isBlank()) {
                log.warn("JWT subject/userId missing");
                return unauthorized(exchange);
            }

            log.debug(
                    "JWT validated successfully for userId: {}, tokenVersion: {}",
                    userId,
                    tokenVersion
            );

            return tokenVersionService
                    .isCurrentVersion(userId, tokenVersion)
                    .materialize()
                    .flatMap(signal -> {

                        // Redis error handling only
                        if (signal.isOnError()) {

                            log.error(
                                    "Redis token validation failed",
                                    signal.getThrowable()
                            );

                            exchange.getResponse().setStatusCode(
                                    HttpStatus.SERVICE_UNAVAILABLE
                            );

                            return exchange.getResponse().setComplete();
                        }

                        Boolean isValid = signal.get();

                        if (!Boolean.TRUE.equals(isValid)) {
                            log.warn(
                                    "JWT token version mismatch for userId: {}",
                                    userId
                            );

                            return unauthorized(exchange);
                        }

                        // Forward authenticated user information
                        ServerHttpRequest mutatedRequest = exchange
                                .getRequest()
                                .mutate()
                                .headers(headers -> {
                                    headers.remove("X-User-Id");
                                    headers.set("X-User-Id", userId);
                                })
                                .build();

                        ServerWebExchange mutatedExchange = exchange
                                .mutate()
                                .request(mutatedRequest)
                                .build();

                        log.debug(
                                "Authentication successful. Forwarding request for userId: {}",
                                userId
                        );

                        // Downstream errors are no longer treated as Redis errors
                        return chain.filter(mutatedExchange);
                    });
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }

    public static class Config {
    }
}
