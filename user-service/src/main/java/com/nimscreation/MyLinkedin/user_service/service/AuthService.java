
package com.nimscreation.MyLinkedin.user_service.service;

import com.nimscreation.MyLinkedin.user_service.dto.LoginRequestDto;
import com.nimscreation.MyLinkedin.user_service.dto.SignupRequestDto;
import com.nimscreation.MyLinkedin.user_service.dto.UserDto;
import com.nimscreation.MyLinkedin.user_service.entity.PasswordResetToken;
import com.nimscreation.MyLinkedin.user_service.entity.User;
import com.nimscreation.MyLinkedin.user_service.events.UserCreatedEvent;
import com.nimscreation.MyLinkedin.user_service.exception.BadRequestException;
import com.nimscreation.MyLinkedin.user_service.repository.PasswordResetTokenRepository;
import com.nimscreation.MyLinkedin.user_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    private final KafkaTemplate<Long, UserCreatedEvent>
            userCreatedEventKafkaTemplate;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;

    // SIGNUP
    public UserDto signUp(SignupRequestDto signupRequestDto) {

        log.info("Signup request for email: {}", signupRequestDto.getEmail());

        boolean exists = userRepository.existsByEmail(
                signupRequestDto.getEmail()
        );

        if (exists) {
            throw new BadRequestException("User already exists");
        }

        User user = modelMapper.map(signupRequestDto, User.class);

        user.setPassword(
                BCrypt.hashpw(
                        signupRequestDto.getPassword(),
                        BCrypt.gensalt()
                )
        );

        user = userRepository.save(user);

        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .userId(user.getId())
                .name(user.getName())
                .build();

        userCreatedEventKafkaTemplate.send(
                "user_created_topic",
                userCreatedEvent
        );

        return modelMapper.map(user, UserDto.class);
    }

    // LOGIN
    public String login(LoginRequestDto loginRequestDto) {

        log.info("Login request for email: {}", loginRequestDto.getEmail());

        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new BadRequestException(
                        "Incorrect email or password"
                ));

        boolean isPasswordMatch = BCrypt.checkpw(
                loginRequestDto.getPassword(),
                user.getPassword()
        );

        if (!isPasswordMatch) {
            throw new BadRequestException("Incorrect email or password");
        }

        return jwtService.generateAccessToken(user);
    }

    // FORGOT PASSWORD
    @Transactional
    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email).orElse(null);

        // Do not reveal whether an account exists
        if (user == null) {
            log.info("Password reset requested");
            return;
        }

        // Remove previous reset tokens
        passwordResetTokenRepository.deleteByUser(user);

        // Generate secure token
        String token = passwordResetTokenService.generateToken();

        String tokenHash = passwordResetTokenService.hashToken(token);

        Instant now = Instant.now();

        PasswordResetToken resetToken = new PasswordResetToken();

        resetToken.setTokenHash(tokenHash);
        resetToken.setUser(user);
        resetToken.setCreatedAt(now);
        resetToken.setExpiresAt(now.plus(15, ChronoUnit.MINUTES));

        passwordResetTokenRepository.save(resetToken);

        String resetLink =
                "http://localhost:3000/reset-password?token=" + token;

        try {
            emailService.sendPasswordResetEmail(
                    user.getEmail(),
                    resetLink
            );

            log.info("Password reset email processed");

        } catch (Exception e) {
            passwordResetTokenRepository.delete(resetToken);
            log.error("Unable to send password reset email", e);
            throw new IllegalStateException("Unable to process password reset request");

        }
    }

    // RESET PASSWORD
    @Transactional
    public void resetPassword(String token, String newPassword) {

        if (token == null || token.isBlank()
                || newPassword == null || newPassword.isBlank()) {
            throw new BadRequestException("Invalid password reset request");
        }

        String passwordPattern =
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s])\\S{8,}$";

        if (!newPassword.matches(passwordPattern)) {
            throw new BadRequestException(
                    "Password must contain at least 8 characters, " +
                            "one uppercase, one lowercase, one digit and one special character"
            );
        }

        String tokenHash = passwordResetTokenService.hashToken(token);

        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHashForUpdate(tokenHash)
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        Instant now = Instant.now();

        if (resetToken.getUsedAt() != null) {
            throw new BadRequestException("Reset token has already been used");
        }

        if (!resetToken.getExpiresAt().isAfter(now)) {
            throw new BadRequestException("Reset token has expired");
        }

        User user = resetToken.getUser();

        // Hash the new password
        user.setPassword(
                BCrypt.hashpw(newPassword, BCrypt.gensalt())
        );

        userRepository.save(user);

        // Mark token as used
        resetToken.setUsedAt(now);

        passwordResetTokenRepository.save(resetToken);

        log.info("Password reset completed for user ID: {}", user.getId());
    }
}
