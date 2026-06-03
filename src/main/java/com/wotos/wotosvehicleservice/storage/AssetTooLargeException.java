package com.wotos.wotosvehicleservice.storage;

/** A model upload exceeded the size budget (R4: 4 MB). */
public class AssetTooLargeException extends StorageException {

    public AssetTooLargeException(String message) {
        super(message);
    }
}
