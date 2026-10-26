package com.nimscreation.MyLinkedin.post_service.auth;

import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor extends RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Long userId = AuthContextHolder.getCurrentUserId();
        if(userId != null) {
            requestTemplate.header("X-User-Id", userId.toString());
        }
    }
}
