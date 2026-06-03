package com.wotos.wotosvehicleservice.web;

/** A requested resource (vehicle, armor profile, model asset) does not exist → 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
