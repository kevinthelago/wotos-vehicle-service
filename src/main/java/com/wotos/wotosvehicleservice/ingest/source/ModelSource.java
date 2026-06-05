package com.wotos.wotosvehicleservice.ingest.source;

import com.wotos.wotosvehicleservice.ingest.catalog.TankEntry;

import java.util.Optional;

/**
 * Fetches the raw {@code .glb} bytes for a tank's 3-D model. Empty means no model
 * file is available; the ingest step proceeds without uploading or writing a model row.
 */
public interface ModelSource {
    Optional<byte[]> fetchModel(TankEntry entry);
}
