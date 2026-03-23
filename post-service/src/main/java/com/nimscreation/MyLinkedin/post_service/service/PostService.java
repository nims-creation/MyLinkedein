package com.nimscreation.MyLinkedin.post_service.service;

import com.nimscreation.MyLinkedin.post_service.dto.PostCreateRequestDto;
import com.nimscreation.MyLinkedin.post_service.dto.PostDto;

import java.util.List;

public interface PostService {

    PostDto createPost(PostCreateRequestDto postDto, Long userId);

    PostDto getPostById(Long postId);

    List<PostDto> getAllPostsOfUser(Long userId);
}
