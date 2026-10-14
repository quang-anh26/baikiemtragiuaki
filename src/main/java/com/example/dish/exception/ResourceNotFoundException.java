package com.example.dish.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String messageKey;
    private final Object arg;

    public ResourceNotFoundException(String messageKey, Object arg) {
        super(messageKey + ": " + arg);
        this.messageKey = messageKey;
        this.arg = arg;
    }
}
