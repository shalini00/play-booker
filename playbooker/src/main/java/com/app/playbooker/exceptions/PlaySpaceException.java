package com.app.playbooker.exceptions;

public class PlaySpaceException extends RuntimeException {
    public PlaySpaceException(String message) {
        super(message);
    }
    public PlaySpaceException(String message, Throwable cause) { super(message, cause);}
}
