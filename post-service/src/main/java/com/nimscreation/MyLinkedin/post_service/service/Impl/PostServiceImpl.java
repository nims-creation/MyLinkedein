package com.nimscreation.MyLinkedin.post_service.service.Impl;

import com.nimscreation.MyLinkedin.post_service.dto.PostCreateDto;
import com.nimscreation.MyLinkedin.post_service.dto.PostDto;
import com.nimscreation.MyLinkedin.post_service.entity.Post;
import com.nimscreation.MyLinkedin.post_service.repository.PostRepository;
import com.nimscreation.MyLinkedin.post_service.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Override
    public PostDto createPost(PostCreateRequestDto postDto, Long userId) {
        Post
    }
}
