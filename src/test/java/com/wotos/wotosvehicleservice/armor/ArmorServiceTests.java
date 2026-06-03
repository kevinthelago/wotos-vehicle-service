package com.wotos.wotosvehicleservice.armor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for armor (de)serialization + upsert, mocking the repository with a real
 * ObjectMapper (so the JSON round-trip is exercised).
 */
@ExtendWith(MockitoExtension.class)
class ArmorServiceTests {

    @Mock
    private VehicleArmorRepository vehicleArmorRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private ArmorService service() {
        return new ArmorService(vehicleArmorRepository, objectMapper);
    }

    @Test
    void serializesProfileOnSave() {
        when(vehicleArmorRepository.findByVehicleId(5137)).thenReturn(Optional.empty());
        when(vehicleArmorRepository.save(any(VehicleArmor.class))).thenAnswer(inv -> inv.getArgument(0));

        ArmorProfile profile = new ArmorProfile(5137,
                List.of(new ArmorProfile.ArmorZone("hull_front", 45.0, "hull", null)),
                ArmorProfile.SOURCE_TANKS_GG, Instant.parse("2026-06-01T00:00:00Z"));

        VehicleArmor saved = service().saveArmorProfile(profile);

        assertThat(saved.getVehicleId()).isEqualTo(5137);
        assertThat(saved.getGeneratedFrom()).isEqualTo("tanks.gg");
        assertThat(saved.getArmorProfileJson()).contains("hull_front").contains("45.0");
    }

    @Test
    void deserializesProfileOnRead() throws Exception {
        ArmorProfile profile = new ArmorProfile(6753,
                List.of(new ArmorProfile.ArmorZone("turret_front", 240.0, "turret", List.of(0.0, 1.0, 0.0))),
                ArmorProfile.SOURCE_MANUAL, Instant.parse("2026-06-01T00:00:00Z"));
        VehicleArmor row = new VehicleArmor();
        row.setVehicleId(6753);
        row.setArmorProfileJson(objectMapper.writeValueAsString(profile));
        row.setGeneratedFrom("manual");
        row.setGeneratedAt(profile.generatedAt());
        when(vehicleArmorRepository.findByVehicleId(6753)).thenReturn(Optional.of(row));

        Optional<ArmorProfile> loaded = service().getArmorProfile(6753);

        assertThat(loaded).isPresent();
        assertThat(loaded.get().zones().get(0).key()).isEqualTo("turret_front");
        assertThat(loaded.get().zones().get(0).normalHint()).containsExactly(0.0, 1.0, 0.0);
    }

    @Test
    void readReturnsEmptyWhenNoRow() {
        when(vehicleArmorRepository.findByVehicleId(1)).thenReturn(Optional.empty());
        assertThat(service().getArmorProfile(1)).isEmpty();
    }
}
