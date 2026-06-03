package com.wotos.wotosvehicleservice.vehicle;

import com.wotos.wotosvehicleservice.client.wot.WotApiResponse;
import com.wotos.wotosvehicleservice.client.wot.WotTankopediaFeignClient;
import com.wotos.wotosvehicleservice.client.wot.vehicle.WotVehicle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
 * Read-through unit tests for the catalog service, mocking the repository and the WoT
 * Feign client.
 */
@ExtendWith(MockitoExtension.class)
class VehicleCatalogServiceTests {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private WotTankopediaFeignClient wotTankopediaFeignClient;

    @InjectMocks
    private VehicleCatalogService vehicleCatalogService;

    @Test
    void localHitDoesNotCallWot() {
        Vehicle local = new Vehicle(6753, "Maus", "germany", 10, "heavyTank");
        when(vehicleRepository.findById(6753)).thenReturn(Optional.of(local));

        Optional<Vehicle> result = vehicleCatalogService.getVehicle(6753);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Maus");
        verify(wotTankopediaFeignClient, never())
                .getVehicles(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void fallsBackToWotAndMapsWhenLocalMissing() {
        when(vehicleRepository.findById(7169)).thenReturn(Optional.empty());
        WotVehicle wot = mock(WotVehicle.class);
        when(wot.getName()).thenReturn("IS-7");
        when(wot.getNation()).thenReturn("ussr");
        when(wot.getTier()).thenReturn(10);
        when(wot.getType()).thenReturn("heavyTank");
        WotApiResponse<Map<Integer, WotVehicle>> body =
                new WotApiResponse<>("ok", null, null, Map.of(7169, wot));
        when(wotTankopediaFeignClient.getVehicles(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(ResponseEntity.ok(body));

        Optional<Vehicle> result = vehicleCatalogService.getVehicle(7169);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("IS-7");
        assertThat(result.get().getCanonicalName()).isEqualTo("is-7");
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void returnsEmptyWhenWotHasNoVehicle() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.empty());
        WotApiResponse<Map<Integer, WotVehicle>> body =
                new WotApiResponse<>("ok", null, null, Map.of());
        when(wotTankopediaFeignClient.getVehicles(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(ResponseEntity.ok(body));

        assertThat(vehicleCatalogService.getVehicle(1)).isEmpty();
    }
}
