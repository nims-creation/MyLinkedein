package com.nimscreation.MyLinkedin.post_service.controller;

import com.nimscreation.MyLinkedin.post_service.dto.PostCreateRequestDto;
import com.nimscreation.MyLinkedin.post_service.dto.PostDto;
import com.nimscreation.MyLinkedin.post_service.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostCreateRequestDto postDto, HttpServletRequest httpServletRequest){
        PostDto createdPost = postService.createPost(postDto, 1L);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }
}
