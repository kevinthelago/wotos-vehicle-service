package com.wotos.wotosvehicleservice.storage;

/** The uploaded bytes are not a parseable binary glTF (.glb). */
public class MalformedGlbException extends StorageException {

    public MalformedGlbException(String message) {
        super(message);
    }

    public MalformedGlbException(String message, Throwable cause) {
        super(message, cause);
    }
}
