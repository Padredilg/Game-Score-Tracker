package com.cen4010.gamescoretracker.utils.exceptions;

public class EntityExistsException extends RuntimeException {
    public EntityExistsException(String message) {
        super(message);
    }
}
