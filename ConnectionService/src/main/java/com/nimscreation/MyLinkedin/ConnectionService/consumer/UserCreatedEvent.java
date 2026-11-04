package com.nimscreation.MyLinkedin.ConnectionService.consumer;

import lombok.Data;

@Data
public class UserCreatedEvent {
    private Long userId;
    private String name;
}
