package com.example.setcardgame.exception;

import lombok.Getter;

@Getter
public class EncryptException extends RuntimeException {
    private final String message;

    public EncryptException(String message) {
        this.message = message;
    }
}
