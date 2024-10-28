package com.example.setcardgame.exception;

import lombok.Getter;

@Getter
public class JSONParsingException extends RuntimeException {
    private final String message;

    public JSONParsingException(String message) {
        this.message = message;
    }
}
