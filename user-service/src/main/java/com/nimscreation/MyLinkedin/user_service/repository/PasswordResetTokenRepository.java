package com.nimscreation.MyLinkedin.user_service.repository;

import com.nimscreation.MyLinkedin.user_service.entity.PasswordResetToken;
import com.nimscreation.MyLinkedin.user_service.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    void deleteByUser(User user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM PasswordResetToken t WHERE t.tokenHash = :tokenHash")
    Optional<PasswordResetToken> findByTokenHashForUpdate(
            @Param("tokenHash") String tokenHash
    );
}
