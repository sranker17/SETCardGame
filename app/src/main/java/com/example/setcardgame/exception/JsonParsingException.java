package com.example.setcardgame.exception;

import lombok.Getter;

@Getter
public class JsonParsingException extends RuntimeException {
    private final String message;

    public JsonParsingException(String message) {
        this.message = message;
    }
}
