package com.nimscreation.MyLinkedin.post_service.service;

import com.nimscreation.MyLinkedin.post_service.dto.PostCreateRequestDto;
import com.nimscreation.MyLinkedin.post_service.dto.PostDto;
import com.nimscreation.MyLinkedin.post_service.entity.Post;

public interface PostService {

    PostDto createPost(PostCreateRequestDto postDto, Long userId);

    PostDto getPostById(Long postId);
}
