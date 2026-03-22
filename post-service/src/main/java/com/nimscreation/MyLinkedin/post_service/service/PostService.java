package com.nimscreation.MyLinkedin.post_service.service;

import com.nimscreation.MyLinkedin.post_service.dto.PostCreateDto;
import com.nimscreation.MyLinkedin.post_service.dto.PostDto;
import org.springframework.stereotype.Service;

@Service
public interface PostService {

    PostDto createPost(PostCreateRequestDto postDto, Long userId);
}
