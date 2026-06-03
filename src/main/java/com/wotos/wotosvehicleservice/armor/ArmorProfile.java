package com.wotos.wotosvehicleservice.armor;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Armor profile for a single vehicle — the LOCKED cross-stream contract consumed by
 * {@code wotos-edge-service} (merged into the {@code /garage} fan-out) and painted on
 * the mesh by the React garage. Zones cover hull (front/sides/rear), turret
 * (front/sides/rear), mantle, cupolas, and any per-tank weak spots.
 *
 * <p>Do not rename {@code zones[].key} values or change field names without a
 * {@code bsc-note} to the frontend-garage stream — the schema is published and
 * relied upon downstream.
 *
 * @param vehicleId     WoT {@code tank_id}
 * @param zones         armor zones; thickness in millimeters, geometryRef names the
 *                      mesh sub-object, optional normalHint is the zone's outward
 *                      surface normal {@code [x, y, z]}
 * @param generatedFrom provenance — {@link #SOURCE_TANKS_GG} or {@link #SOURCE_MANUAL}
 * @param generatedAt   ISO-8601 instant the profile was generated
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArmorProfile(
        Integer vehicleId,
        List<ArmorZone> zones,
        String generatedFrom,
        Instant generatedAt
) {

    public static final String SOURCE_TANKS_GG = "tanks.gg";
    public static final String SOURCE_MANUAL = "manual";

    /**
     * A single armor zone. {@code normalHint} is optional (absent for zones whose
     * orientation the client derives from geometry).
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ArmorZone(
            String key,
            double thicknessMm,
            String geometryRef,
            List<Double> normalHint
    ) {
    }
}
