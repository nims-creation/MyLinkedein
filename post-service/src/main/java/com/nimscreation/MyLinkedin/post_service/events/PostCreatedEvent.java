package com.nimscreation.MyLinkedin.post_service.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostCreatedEvent {
    private Long postId;
    private Long userId;
    private String content;
}
