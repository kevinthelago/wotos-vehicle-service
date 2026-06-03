package com.wotos.wotosvehicleservice.storage;

/** Base type for object-storage failures (handled by the global advice in G7). */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
