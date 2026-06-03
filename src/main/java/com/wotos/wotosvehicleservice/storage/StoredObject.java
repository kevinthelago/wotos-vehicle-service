package com.wotos.wotosvehicleservice.storage;

/**
 * Result of an object-storage upload — the metadata persisted alongside the
 * {@code .glb} blob (see the model-asset row in G4).
 */
public record StoredObject(
        String bucket,
        String key,
        String etag,
        long sizeBytes,
        boolean dracoCompressed,
        long triangleCount
) {
}
