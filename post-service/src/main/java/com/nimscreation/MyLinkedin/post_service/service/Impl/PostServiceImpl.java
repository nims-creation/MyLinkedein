package com.nimscreation.MyLinkedin.post_service.service.Impl;

import com.nimscreation.MyLinkedin.post_service.dto.PostCreateRequestDto;
import com.nimscreation.MyLinkedin.post_service.dto.PostDto;
import com.nimscreation.MyLinkedin.post_service.entity.Post;
import com.nimscreation.MyLinkedin.post_service.exception.ResourceNotFoundException;
import com.nimscreation.MyLinkedin.post_service.repository.PostRepository;
import com.nimscreation.MyLinkedin.post_service.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Override
    public PostDto createPost(PostCreateRequestDto postDto, Long userId) {
        Post post = modelMapper.map(postDto, Post.class);
        post.setUserId(userId);

        Post savedPost = postRepository.save(post);
        return modelMapper.map(savedPost, PostDto.class);
    }

    @Override
    public PostDto getPostById(Long postId) {
        log.debug("Retrieving post with Id: {}",postId);
        Post post = postRepository.findById(postId).orElseThrow(()->
                new ResourceNotFoundException("Post not found with id: "+postId));
        return modelMapper.map(post, PostDto.class);
    }
}
