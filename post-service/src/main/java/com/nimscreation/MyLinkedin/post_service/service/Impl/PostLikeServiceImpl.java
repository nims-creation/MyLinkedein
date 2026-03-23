package com.nimscreation.MyLinkedin.post_service.service.Impl;

import com.nimscreation.MyLinkedin.post_service.entity.PostLike;
import com.nimscreation.MyLinkedin.post_service.exception.BadRequestException;
import com.nimscreation.MyLinkedin.post_service.exception.ResourceNotFoundException;
import com.nimscreation.MyLinkedin.post_service.repository.PostLikeRepository;
import com.nimscreation.MyLinkedin.post_service.repository.PostRepository;
import com.nimscreation.MyLinkedin.post_service.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;


    @Override
    public void likePost(Long postId, Long userId) {

        log.info("Attempting to like the post with id: {}",postId);

        boolean exists = postRepository.existsById(postId);
        if(!exists) {
            throw new ResourceNotFoundException("Post not found with id: "+postId);
        }

        boolean alreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        if(alreadyLiked){
            throw new BadRequestException("Can't like the same post again.");
        }

        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLikeRepository.save(postLike);

        log.info("Post with id: {} liked successfully ",postId);
    }

    @Override
    @Transactional
    public void unlikePost(Long postId, Long userId) {
        log.info("Attempting to unlike the post with id: {}",postId);

        boolean exists = postRepository.existsById(postId);
        if(!exists) {
            throw new ResourceNotFoundException("Post not found with id: "+postId);
        }

        boolean alreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        if(!alreadyLiked){
            throw new BadRequestException("Can't unlike the post which is not liked.");
        }

        postLikeRepository.deleteByUserIdAndPostId(userId, postId);
        log.info("Post with id: {} unliked successfully ",postId);

    }
}
