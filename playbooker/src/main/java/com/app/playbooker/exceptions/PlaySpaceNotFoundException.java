package com.app.playbooker.exceptions;

public class PlaySpaceNotFoundException extends PlaySpaceException {

    public PlaySpaceNotFoundException(String id) {
        super("PlaySpace not found with ID: " + id);
    }
}
