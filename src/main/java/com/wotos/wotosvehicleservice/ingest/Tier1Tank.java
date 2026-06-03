package com.wotos.wotosvehicleservice.ingest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * One entry of the Tier-1 ingestion work-list ({@code tier1_tanks.json}). {@code id}
 * is the WoT {@code tank_id}; the source URLs document provenance (R1 — so any record
 * Wargaming asks us to remove can be removed by source).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Tier1Tank(
        Integer id,
        String name,
        String canonicalName,
        String nation,
        Integer tier,
        String type,
        String armorSourceUrl,
        String modelSourceUrl
) {
}
