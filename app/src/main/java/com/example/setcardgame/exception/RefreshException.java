package com.example.setcardgame.exception;

import lombok.Getter;

@Getter
public class RefreshException extends RuntimeException {
    private final String message;

    public RefreshException(String message) {
        this.message = message;
    }
}
