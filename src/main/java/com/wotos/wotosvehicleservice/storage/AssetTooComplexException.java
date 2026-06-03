package com.wotos.wotosvehicleservice.storage;

/** A model upload exceeded the triangle budget (R4: 100k triangles). */
public class AssetTooComplexException extends StorageException {

    public AssetTooComplexException(String message) {
        super(message);
    }
}
