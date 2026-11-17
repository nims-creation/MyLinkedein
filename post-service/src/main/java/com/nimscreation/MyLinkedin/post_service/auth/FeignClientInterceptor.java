
package com.nimscreation.MyLinkedin.post_service.auth;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Slf4j
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {

        // Forward Authorization header to Connection Service
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            String authorization = request.getHeader("Authorization");

            if (authorization != null && !authorization.isBlank()) {
                requestTemplate.header("Authorization", authorization);
            }


        }

        // Forward current user ID
        Long userId = AuthContextHolder.getCurrentUserId();

        if (userId != null) {
            requestTemplate.header("X-User-Id", userId.toString());
        }

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            String authorization = request.getHeader("Authorization");

            log.info("Feign Interceptor: Request attributes found");
            log.info("Feign Interceptor: Authorization header present: {}",
                    authorization != null && !authorization.isBlank());

            if (authorization != null && !authorization.isBlank()) {
                requestTemplate.header("Authorization", authorization);
            }
        } else {
            log.warn("Feign Interceptor: Request attributes are NULL");
        }
    }
}
