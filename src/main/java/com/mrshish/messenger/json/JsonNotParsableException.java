package com.mrshish.messenger.json;

public class JsonNotParsableException extends RuntimeException{
    public JsonNotParsableException(String message) {
        super(message);
    }

    public JsonNotParsableException(String message, Throwable cause) {
        super(message, cause);
    }
}
