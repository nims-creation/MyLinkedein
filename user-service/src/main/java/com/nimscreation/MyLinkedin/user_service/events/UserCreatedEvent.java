package com.nimscreation.MyLinkedin.user_service.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreatedEvent {
    private Long userId;
    private String name;
}
