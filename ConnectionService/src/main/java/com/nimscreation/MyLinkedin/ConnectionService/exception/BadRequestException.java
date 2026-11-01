package com.nimscreation.MyLinkedin.ConnectionService.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
