package com.nimscreation.MyLinkedin.user_service.service.Impl;

import com.nimscreation.MyLinkedin.user_service.dto.SignupRequestDto;
import com.nimscreation.MyLinkedin.user_service.dto.UserDto;
import com.nimscreation.MyLinkedin.user_service.repository.UserRepository;
import com.nimscreation.MyLinkedin.user_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private UserRepository userRepository;

    @Override
    public UserDto signup(SignupRequestDto signupRequestDto) {
        return null;
    }
}
