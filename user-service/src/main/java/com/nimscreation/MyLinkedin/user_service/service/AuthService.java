package com.nimscreation.MyLinkedin.user_service.service;

import com.nimscreation.MyLinkedin.user_service.dto.SignupRequestDto;
import com.nimscreation.MyLinkedin.user_service.dto.UserDto;

public interface AuthService {
    UserDto signup(SignupRequestDto signupRequestDto);
}
