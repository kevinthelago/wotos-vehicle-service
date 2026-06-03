package com.wotos.wotosvehicleservice.ingest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * The raw community/Tanks.gg armor shape fetched per tank, before normalization to
 * our {@link com.wotos.wotosvehicleservice.armor.ArmorProfile}. Field names mirror a
 * typical community export: a list of named zones with thickness, a mesh reference,
 * and an optional outward normal.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CommunityArmorDocument(
        Integer tankId,
        String source,
        List<Zone> zones
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Zone(
            String name,
            double thickness,
            String mesh,
            List<Double> normal
    ) {
    }
}
