package com.nimscreation.MyLinkedin.user_service.service.Impl;

import com.nimscreation.MyLinkedin.user_service.dto.LoginRequestDto;
import com.nimscreation.MyLinkedin.user_service.dto.SignupRequestDto;
import com.nimscreation.MyLinkedin.user_service.dto.UserDto;
import com.nimscreation.MyLinkedin.user_service.entity.User;
import com.nimscreation.MyLinkedin.user_service.exception.ResourceNotFoundException;
import com.nimscreation.MyLinkedin.user_service.repository.UserRepository;
import com.nimscreation.MyLinkedin.user_service.service.AuthService;
import com.nimscreation.MyLinkedin.user_service.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private UserRepository userRepository;
    private ModelMapper modelMapper;

    @Override
    public UserDto signup(SignupRequestDto signupRequestDto) {
        User user = modelMapper.map(signupRequestDto, User.class);
        user.setPassword(PasswordUtil.hashPassword(signupRequestDto.getPassword()));

        User saveUser = userRepository.save(user);
        return modelMapper.map(saveUser,UserDto.class);
    }

    @Override
    public String login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(()-> new ResourceNotFoundException("User not found with email:"+loginRequestDto.getEmail()));


    }
}
