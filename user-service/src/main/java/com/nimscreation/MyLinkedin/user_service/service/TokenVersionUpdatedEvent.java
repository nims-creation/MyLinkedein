package com.nimscreation.MyLinkedin.user_service.service;

public record TokenVersionUpdatedEvent(
        Long userId,
        Integer tokenVersion
) {
}