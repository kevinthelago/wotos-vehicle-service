package com.wotos.wotosvehicleservice.ingest.catalog;

/**
 * A single entry in the tank catalog — the minimum fields needed to drive ingestion
 * (identity, filter dimensions, and enough data to populate the {@code vehicles} row
 * without a round-trip to the WoT API).
 */
public record TankEntry(int tankId, String name, String nation, int tier, String type) {}
