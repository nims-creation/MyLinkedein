package com.nimscreation.MyLinkedin.notification_service.events;

import lombok.Data;

@Data
public class UserCreatedEvent {
    private Long userId;
    private String name;
}

