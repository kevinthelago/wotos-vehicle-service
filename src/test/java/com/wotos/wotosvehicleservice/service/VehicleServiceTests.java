package com.wotos.wotosvehicleservice.service;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Business-logic unit tests for the Tankopedia bulk service, mocking the WoT Feign
 * client (no network).
 */
@ExtendWith(MockitoExtension.class)
class VehicleServiceTests {

    @Mock
    private WotTankopediaFeignClient wotTankopediaFeignClient;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void returnsVehiclesFromWotApi() {
        WotVehicle wot = mock(WotVehicle.class);
        WotApiResponse<Map<Integer, WotVehicle>> body =
                new WotApiResponse<>("ok", null, null, Map.of(5137, wot));
        when(wotTankopediaFeignClient.getVehicles(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(ResponseEntity.ok(body));

        Map<Integer, WotVehicle> result = vehicleService.getVehicles(
                null, "en", null, null, null, null, null, null);

        assertThat(result).containsKey(5137);
    }

    @Test
    void returnsEmptyMapWhenWotBodyIsNull() {
        // A null response body trips Objects.requireNonNull -> NPE -> swallowed -> empty map.
        WotApiResponse<Map<Integer, WotVehicle>> nullBody = null;
        when(wotTankopediaFeignClient.getVehicles(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(ResponseEntity.ok(nullBody));

        Map<Integer, WotVehicle> result = vehicleService.getVehicles(
                null, "en", null, null, null, null, null, null);

        assertThat(result).isEmpty();
    }
}
