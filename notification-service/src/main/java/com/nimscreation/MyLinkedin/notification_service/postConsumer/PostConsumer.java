package com.nimscreation.MyLinkedin.notification_service.postConsumer;

import com.nimscreation.MyLinkedin.notification_service.entity.Notification;
import com.nimscreation.MyLinkedin.notification_service.services.NotificationService;
import com.nimscreation.MyLinkedin.post_service.events.PostCreatedEvent;
import com.nimscreation.MyLinkedin.post_service.events.PostLikedEvent;
import com.nimscreation.MyLinkedin.notification_service.events.UserCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "post_created_topic")
    public void handlePostCreated(PostCreatedEvent postCreated) {

        log.info("handlePostCreated: {}", postCreated);

        String message = String.format(
                "Your connection with id: %d has created this post: %s",
                postCreated.getOwnerUserId(),
                postCreated.getContent()
        );

        Notification notification = Notification.builder()
                .message(message)
                .userId(postCreated.getUserId())
                .build();

        notificationService.addNotification(notification);
    }

    @KafkaListener(topics = "post_liked_topic")
    public void handlePostLiked(PostLikedEvent postLiked) {

        log.info("handlePostLiked: {}", postLiked);

        String message = String.format(
                "User with id: %d has liked your post with id: %d",
                postLiked.getLikeByUserId(),
                postLiked.getPostId()
        );

        Notification notification = Notification.builder()
                .message(message)
                .userId(postLiked.getOwnerUserId())
                .build();

        notificationService.addNotification(notification);
    }


    @KafkaListener(topics = "user_created_topic")
    public void handleUserCreated(UserCreatedEvent userCreated) {

        log.info("handleUserCreated: {}", userCreated);

        String message = String.format(
                "Welcome to MyLinkedin, %s! Your account has been created successfully.",
                userCreated.getName()
        );

        Notification notification = Notification.builder()
                .message(message)
                .userId(userCreated.getUserId())
                .build();

        notificationService.addNotification(notification);
    }

}