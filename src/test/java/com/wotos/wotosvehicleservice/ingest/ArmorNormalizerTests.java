package com.wotos.wotosvehicleservice.ingest;

import com.wotos.wotosvehicleservice.armor.ArmorProfile;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Local unit tests for the community-armor -> ArmorProfile normalization. */
class ArmorNormalizerTests {

    private final Tier1Tank tank = new Tier1Tank(5137, "T-34-85", "t-34-85", "ussr", 6, "mediumTank",
            "https://tanks.gg/tank/t-34-85/model", "https://assets/t-34-85.glb");

    @Test
    void mapsCommunityFieldsToContract() {
        CommunityArmorDocument doc = new CommunityArmorDocument(5137, "tanks.gg", List.of(
                new CommunityArmorDocument.Zone("hull_front", 45.0, "hull", List.of(0.0, 0.0, 1.0)),
                new CommunityArmorDocument.Zone("turret_front", 90.0, "turret", null)));
        Instant now = Instant.parse("2026-06-01T00:00:00Z");

        ArmorProfile profile = ArmorNormalizer.normalize(doc, tank, now);

        assertThat(profile.vehicleId()).isEqualTo(5137);
        assertThat(profile.generatedFrom()).isEqualTo("tanks.gg");
        assertThat(profile.generatedAt()).isEqualTo(now);
        assertThat(profile.zones()).hasSize(2);
        assertThat(profile.zones().get(0).key()).isEqualTo("hull_front");
        assertThat(profile.zones().get(0).thicknessMm()).isEqualTo(45.0);
        assertThat(profile.zones().get(0).geometryRef()).isEqualTo("hull");
        assertThat(profile.zones().get(0).normalHint()).containsExactly(0.0, 0.0, 1.0);
        assertThat(profile.zones().get(1).normalHint()).isNull();
    }

    @Test
    void defaultsSourceWhenMissing() {
        CommunityArmorDocument doc = new CommunityArmorDocument(5137, null, List.of());
        ArmorProfile profile = ArmorNormalizer.normalize(doc, tank, Instant.now());
        assertThat(profile.generatedFrom()).isEqualTo(ArmorProfile.SOURCE_TANKS_GG);
        assertThat(profile.zones()).isEmpty();
    }
}
