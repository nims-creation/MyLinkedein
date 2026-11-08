
package com.nimscreation.MyLinkedin.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenVersionEventListener {

    private final TokenVersionService tokenVersionService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTokenVersionUpdated(TokenVersionUpdatedEvent event) {

        tokenVersionService.updateVersion(
                event.userId(),
                event.tokenVersion()
        );

        log.info("Redis token version updated for user ID: {}", event.userId());
    }
}
