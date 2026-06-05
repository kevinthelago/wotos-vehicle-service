package com.wotos.wotosvehicleservice.ingest.source;

import com.wotos.wotosvehicleservice.armor.ArmorProfile;
import com.wotos.wotosvehicleservice.ingest.catalog.TankEntry;

import java.util.Optional;

/**
 * Fetches an {@link ArmorProfile} for a given tank. Empty means no armor data is
 * available for that tank; the ingest step proceeds without writing an armor row.
 */
public interface ArmorSource {
    Optional<ArmorProfile> fetchArmor(TankEntry entry);
}
