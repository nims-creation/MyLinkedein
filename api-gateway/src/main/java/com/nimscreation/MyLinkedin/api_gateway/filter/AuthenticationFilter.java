
package com.nimscreation.MyLinkedin.api_gateway.filter;

import com.nimscreation.MyLinkedin.api_gateway.service.JwtService;
import com.nimscreation.MyLinkedin.api_gateway.service.TokenVersionService;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthenticationFilter
        extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtService jwtService;
    private final TokenVersionService tokenVersionService;

    public AuthenticationFilter(
            JwtService jwtService,
            TokenVersionService tokenVersionService) {

        super(Config.class);
        this.jwtService = jwtService;
        this.tokenVersionService = tokenVersionService;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            log.info("Auth request: {}", exchange.getRequest().getURI());

            final String tokenHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            if (tokenHeader == null ||
                    !tokenHeader.startsWith("Bearer ") ||
                    tokenHeader.length() <= 7) {

                return unauthorized(exchange);
            }

            final String token = tokenHeader.substring(7).trim();

            if (token.isEmpty()) {
                return unauthorized(exchange);
            }

            try {
                String userId = jwtService.getUserIdFromToken(token);

                Integer tokenVersion =
                        jwtService.getTokenVersionFromToken(token);

                if (userId == null || tokenVersion == null) {
                    return unauthorized(exchange);
                }

                return tokenVersionService
                        .isCurrentVersion(userId, tokenVersion)
                        .flatMap(isValid -> {

                            if (!isValid) {
                                log.warn(
                                        "JWT token version mismatch for user ID: {}",
                                        userId
                                );

                                return unauthorized(exchange);
                            }

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

                            return chain.filter(mutatedExchange);
                        })
                        .onErrorResume(error -> {
                            log.error(
                                    "Redis token validation failed: {}",
                                    error.getMessage()
                            );

                            return exchange.getResponse()
                                    .setStatusCode(HttpStatus.SERVICE_UNAVAILABLE)
                                    ? exchange.getResponse().setComplete()
                                    : Mono.empty();
                        });

            } catch (JwtException | IllegalArgumentException e) {

                log.warn("JWT validation failed: {}", e.getMessage());

                return unauthorized(exchange);
            }
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }

    public static class Config {
    }
}
