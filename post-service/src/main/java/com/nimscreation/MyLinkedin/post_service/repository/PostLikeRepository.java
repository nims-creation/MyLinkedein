package com.nimscreation.MyLinkedin.post_service.repository;

import com.nimscreation.MyLinkedin.post_service.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByUserIdAndPostId(Long userId, Long postId);
}
