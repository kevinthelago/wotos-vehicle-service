package com.wotos.wotosvehicleservice.ingest;

import com.wotos.wotosvehicleservice.AbstractIntegrationTest;
import com.wotos.wotosvehicleservice.armor.ArmorService;
import com.wotos.wotosvehicleservice.ingest.filter.IngestFilter;
import com.wotos.wotosvehicleservice.ingest.storage.VehicleModelAsset;
import com.wotos.wotosvehicleservice.ingest.storage.VehicleModelAssetRepository;
import com.wotos.wotosvehicleservice.vehicle.Vehicle;
import com.wotos.wotosvehicleservice.vehicle.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end integration test for the dataset-expansion ingest pipeline.
 * Spins up real MySQL (inherited from {@link AbstractIntegrationTest}) and a real
 * MinIO container, then verifies that ingesting a non-Tier-1 tank:
 * <ul>
 *   <li>writes a {@code vehicles} row</li>
 *   <li>writes a {@code vehicle_armor} row (from the classpath JSON)</li>
 *   <li>uploads the model bytes to MinIO and writes a {@code vehicle_model_assets} row</li>
 *   <li>is idempotent on a second run without {@code --force}</li>
 * </ul>
 * The {@code .glb} fixture lives in {@code src/test/resources/tanks/models/5137.glb};
 * the armor fixture is in {@code src/main/resources/tanks/armor/5137.json}.
 */
class DatasetIngestionRunnerIntegrationTest extends AbstractIntegrationTest {

    static final MinIOContainer MINIO =
            new MinIOContainer("minio/minio:RELEASE.2023-09-04T19-57-37Z");

    static {
        MINIO.start();
    }

    @DynamicPropertySource
    static void minioProperties(DynamicPropertyRegistry registry) {
        registry.add("storage.s3.endpoint",   MINIO::getS3URL);
        registry.add("storage.s3.access-key", MINIO::getUserName);
        registry.add("storage.s3.secret-key", MINIO::getPassword);
        registry.add("storage.s3.bucket",     () -> "test-models");
    }

    @Autowired private IngestionService            ingestionService;
    @Autowired private VehicleRepository           vehicleRepository;
    @Autowired private ArmorService                armorService;
    @Autowired private VehicleModelAssetRepository modelAssetRepository;

    @Test
    void ingestsNonTier1TankEndToEnd() {
        // Tank 5137 = T-34-85, tier 6, ussr — armor JSON and GLB fixture are present
        IngestionService.IngestResult result = ingestionService.ingest(IngestFilter.byTier(6), false);

        assertThat(result.succeeded()).isGreaterThanOrEqualTo(1);
        assertThat(result.failed()).isZero();

        Optional<Vehicle> vehicle = vehicleRepository.findById(5137);
        assertThat(vehicle).isPresent();
        assertThat(vehicle.get().getName()).isEqualTo("T-34-85");
        assertThat(vehicle.get().getNation()).isEqualTo("ussr");
        assertThat(vehicle.get().getTier()).isEqualTo(6);
        assertThat(vehicle.get().getCanonicalName()).isEqualTo("t-34-85");

        assertThat(armorService.getArmorProfile(5137))
                .isPresent()
                .hasValueSatisfying(p -> {
                    assertThat(p.vehicleId()).isEqualTo(5137);
                    assertThat(p.generatedFrom()).isEqualTo("tanks.gg");
                    assertThat(p.zones()).isNotEmpty();
                });

        Optional<VehicleModelAsset> asset = modelAssetRepository.findByVehicleId(5137);
        assertThat(asset).isPresent();
        assertThat(asset.get().getBucket()).isEqualTo("test-models");
        assertThat(asset.get().getObjectKey()).isEqualTo("models/t-34-85.glb");
        assertThat(asset.get().getSizeBytes()).isGreaterThan(0);
        assertThat(asset.get().getFormat()).isEqualTo("glb");
    }

    @Test
    void idempotentSkipsAlreadyIngestedTankOnSecondRun() {
        ingestionService.ingest(IngestFilter.byTier(6), false);

        IngestionService.IngestResult second = ingestionService.ingest(IngestFilter.byTier(6), false);

        assertThat(second.attempted()).isGreaterThanOrEqualTo(1);
        assertThat(second.skipped()).isEqualTo(second.attempted());
        assertThat(second.succeeded()).isZero();
    }

    @Test
    void forceReIngestsAlreadyPresentTank() {
        ingestionService.ingest(IngestFilter.byTier(6), false);

        IngestionService.IngestResult forced = ingestionService.ingest(IngestFilter.byTier(6), true);

        assertThat(forced.succeeded()).isGreaterThanOrEqualTo(1);
        assertThat(forced.skipped()).isZero();
    }
}
