package com.wotos.wotosvehicleservice.ingest;

import com.wotos.wotosvehicleservice.armor.ArmorProfile;

import java.time.Instant;
import java.util.List;

/**
 * Normalizes a raw {@link CommunityArmorDocument} into our locked {@link ArmorProfile}
 * contract: community {@code name/thickness/mesh/normal} fields map to
 * {@code key/thicknessMm/geometryRef/normalHint}.
 */
public final class ArmorNormalizer {

    private ArmorNormalizer() {
    }

    public static ArmorProfile normalize(CommunityArmorDocument document, Tier1Tank tank, Instant generatedAt) {
        List<ArmorProfile.ArmorZone> zones = document.zones() == null ? List.of()
                : document.zones().stream()
                .map(z -> new ArmorProfile.ArmorZone(z.name(), z.thickness(), z.mesh(), z.normal()))
                .toList();
        String generatedFrom = (document.source() == null || document.source().isBlank())
                ? ArmorProfile.SOURCE_TANKS_GG
                : document.source();
        return new ArmorProfile(tank.id(), zones, generatedFrom, generatedAt);
    }
}
