package com.wotos.wotosvehicleservice.vehicle;

import com.wotos.wotosvehicleservice.AbstractIntegrationTest;
import com.wotos.wotosvehicleservice.client.wot.WotApiResponse;
import com.wotos.wotosvehicleservice.client.wot.WotTankopediaFeignClient;
import com.wotos.wotosvehicleservice.client.wot.vehicle.WotVehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Persistence + read-through integration test against a real MySQL (Testcontainers).
 * Exercises the Flyway-created {@code vehicles} table and the WoT fallback path.
 */
class VehicleCatalogIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleCatalogService vehicleCatalogService;

    @MockBean
    private WotTankopediaFeignClient wotTankopediaFeignClient;

    @Test
    void persistsAndReadsBackAVehicle() {
        Vehicle t34 = new Vehicle(5137, "T-34-85", "ussr", 6, "mediumTank");
        t34.setShortName("T-34-85");
        t34.setCanonicalName("t-34-85");
        t34.setShellTypes("[\"AP\",\"APCR\",\"HE\"]");

        vehicleCatalogService.save(t34);

        Optional<Vehicle> found = vehicleRepository.findById(5137);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("T-34-85");
        assertThat(found.get().getNation()).isEqualTo("ussr");
        assertThat(found.get().getTier()).isEqualTo(6);
        assertThat(found.get().getShellTypes()).contains("APCR");
    }

    @Test
    void readThroughReturnsLocalRowWithoutCallingWot() {
        Vehicle maus = new Vehicle(6753, "Maus", "germany", 10, "heavyTank");
        vehicleCatalogService.save(maus);

        Optional<Vehicle> found = vehicleCatalogService.getVehicle(6753);

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Maus");
        verify(wotTankopediaFeignClient, never()).getVehicles(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void readThroughFallsBackToWotApiWhenLocalRowMissing() {
        int missingId = 9999;
        WotVehicle wot = mock(WotVehicle.class);
        when(wot.getName()).thenReturn("IS-7");
        when(wot.getNation()).thenReturn("ussr");
        when(wot.getTier()).thenReturn(10);
        when(wot.getType()).thenReturn("heavyTank");
        when(wot.getShortName()).thenReturn("IS-7");

        WotApiResponse<Map<Integer, WotVehicle>> body =
                new WotApiResponse<>("ok", null, null, Map.of(missingId, wot));
        when(wotTankopediaFeignClient.getVehicles(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(ResponseEntity.ok(body));

        Optional<Vehicle> found = vehicleCatalogService.getVehicle(missingId);

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("IS-7");
        assertThat(found.get().getCanonicalName()).isEqualTo("is-7");
        assertThat(vehicleRepository.findById(missingId)).isEmpty(); // fallback does not persist
    }
}
