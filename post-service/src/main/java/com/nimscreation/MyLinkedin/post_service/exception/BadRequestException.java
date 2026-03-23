package com.nimscreation.MyLinkedin.post_service.exception;

public class BadRequestException extends RuntimeException{

    public BadRequestException(String message){
        super(message);
    }
}
