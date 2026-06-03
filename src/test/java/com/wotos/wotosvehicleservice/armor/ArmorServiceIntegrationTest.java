package com.wotos.wotosvehicleservice.armor;

import com.wotos.wotosvehicleservice.AbstractIntegrationTest;
import com.wotos.wotosvehicleservice.vehicle.Vehicle;
import com.wotos.wotosvehicleservice.vehicle.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Round-trips an armor profile through the {@code vehicle_armor} JSON column against a
 * real MySQL, and verifies the upsert keeps one (latest-wins) row per vehicle.
 */
class ArmorServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ArmorService armorService;

    @Autowired
    private VehicleArmorRepository vehicleArmorRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    void persistsAndReadsBackArmorProfile() {
        vehicleRepository.save(new Vehicle(6753, "Maus", "germany", 10, "heavyTank"));
        ArmorProfile profile = new ArmorProfile(
                6753,
                List.of(
                        new ArmorProfile.ArmorZone("hull_front", 200.0, "hull", List.of(0.0, 0.0, 1.0)),
                        new ArmorProfile.ArmorZone("turret_cheek", 240.0, "turret", null)
                ),
                ArmorProfile.SOURCE_MANUAL,
                Instant.parse("2026-06-01T12:00:00Z"));

        armorService.saveArmorProfile(profile);

        Optional<ArmorProfile> loaded = armorService.getArmorProfile(6753);
        assertThat(loaded).isPresent();
        assertThat(loaded.get().vehicleId()).isEqualTo(6753);
        assertThat(loaded.get().generatedFrom()).isEqualTo("manual");
        assertThat(loaded.get().zones()).hasSize(2);
        assertThat(loaded.get().zones().get(0).key()).isEqualTo("hull_front");
        assertThat(loaded.get().zones().get(0).thicknessMm()).isEqualTo(200.0);
        assertThat(loaded.get().zones().get(0).normalHint()).containsExactly(0.0, 0.0, 1.0);
        assertThat(loaded.get().zones().get(1).normalHint()).isNull();
    }

    @Test
    void upsertKeepsOneRowPerVehicleLatestWins() {
        vehicleRepository.save(new Vehicle(7169, "IS-7", "ussr", 10, "heavyTank"));
        armorService.saveArmorProfile(new ArmorProfile(7169,
                List.of(new ArmorProfile.ArmorZone("hull_front", 150.0, "hull", null)),
                ArmorProfile.SOURCE_TANKS_GG, Instant.now().minus(1, ChronoUnit.DAYS)));

        armorService.saveArmorProfile(new ArmorProfile(7169,
                List.of(new ArmorProfile.ArmorZone("hull_front", 175.0, "hull", null)),
                ArmorProfile.SOURCE_MANUAL, Instant.now()));

        assertThat(vehicleArmorRepository.findAll().stream()
                .filter(a -> a.getVehicleId().equals(7169)).count()).isEqualTo(1);
        assertThat(armorService.getArmorProfile(7169).orElseThrow().zones().get(0).thicknessMm())
                .isEqualTo(175.0);
    }
}
