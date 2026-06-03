package com.wotos.wotosvehicleservice.ingest;

import com.wotos.wotosvehicleservice.AbstractIntegrationTest;
import com.wotos.wotosvehicleservice.armor.ArmorProfile;
import com.wotos.wotosvehicleservice.armor.ArmorService;
import com.wotos.wotosvehicleservice.model.ModelService;
import com.wotos.wotosvehicleservice.storage.TestGlbFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end Tier-1 ingestion against Testcontainers MySQL + MinIO. Stub armor/model
 * sources stand in for the network so the test exercises the orchestration, the
 * armor + model row writes, idempotency, and {@code --force}.
 */
@ActiveProfiles("ingest")
@Import(Tier1IngestionIntegrationTest.StubSources.class)
class Tier1IngestionIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private Tier1IngestionService ingestionService;

    @Autowired
    private ArmorService armorService;

    @Autowired
    private ModelService modelService;

    @Test
    void ingestsAllTier1TanksThenSkipsThenForces() {
        IngestionReport first = ingestionService.ingestTier1(false);
        assertThat(first.ingested()).hasSize(20);
        assertThat(first.skipped()).isEmpty();
        assertThat(first.failed()).isEmpty();

        // T-34-85 (5137) has both an armor row and a resolvable signed model URL.
        assertThat(armorService.getArmorProfile(5137)).isPresent();
        assertThat(modelService.hasModel(5137)).isTrue();
        assertThat(modelService.getModel(5137).orElseThrow().url()).contains("X-Amz-Signature");

        // Idempotent: a second run skips everything already present.
        IngestionReport second = ingestionService.ingestTier1(false);
        assertThat(second.skipped()).hasSize(20);
        assertThat(second.ingested()).isEmpty();

        // --force re-ingests.
        IngestionReport forced = ingestionService.ingestTier1(true);
        assertThat(forced.ingested()).hasSize(20);
    }

    @TestConfiguration
    static class StubSources {

        @Bean
        @Primary
        ArmorSource stubArmorSource() {
            return tank -> new ArmorProfile(
                    tank.id(),
                    List.of(new ArmorProfile.ArmorZone("hull_front", 100.0, "hull", List.of(0.0, 0.0, 1.0))),
                    ArmorProfile.SOURCE_MANUAL,
                    Instant.now());
        }

        @Bean
        @Primary
        ModelSource stubModelSource() {
            return tank -> TestGlbFactory.triangleGlb(100);
        }
    }
}
