package com.wotos.wotosvehicleservice.model;

/**
 * Model-asset API response (LOCKED cross-stream contract). The React garage fetches
 * the {@code .glb} directly from {@code url} — a short-lived (60s) presigned GET URL
 * minted fresh per request.
 *
 * @param url       60-second presigned GET URL for the {@code .glb} blob
 * @param etag      object ETag (integrity/cache key)
 * @param sizeBytes blob size in bytes
 * @param format    asset format, always {@code "glb"}
 */
public record ModelResponse(
        String url,
        String etag,
        Long sizeBytes,
        String format
) {
}
