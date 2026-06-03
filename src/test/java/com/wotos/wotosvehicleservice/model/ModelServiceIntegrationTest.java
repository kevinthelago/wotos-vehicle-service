package com.wotos.wotosvehicleservice.model;

import com.wotos.wotosvehicleservice.AbstractIntegrationTest;
import com.wotos.wotosvehicleservice.storage.ObjectStorageService;
import com.wotos.wotosvehicleservice.storage.StoredObject;
import com.wotos.wotosvehicleservice.storage.TestGlbFactory;
import com.wotos.wotosvehicleservice.vehicle.Vehicle;
import com.wotos.wotosvehicleservice.vehicle.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end model flow against Testcontainers MySQL + MinIO: upload a .glb, persist
 * the asset row, then resolve the model endpoint payload (with a live signed URL).
 */
class ModelServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ModelService modelService;

    @Autowired
    private ObjectStorageService objectStorageService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    void uploadsPersistsAndResolvesSignedModelUrl() {
        vehicleRepository.save(new Vehicle(5137, "T-34-85", "ussr", 6, "mediumTank"));
        StoredObject stored = objectStorageService.uploadGlb("models/t-34-85.glb", TestGlbFactory.triangleGlb(500));
        modelService.saveModelAsset(5137, stored);

        Optional<ModelResponse> response = modelService.getModel(5137);

        assertThat(response).isPresent();
        assertThat(response.get().format()).isEqualTo("glb");
        assertThat(response.get().etag()).isEqualTo(stored.etag());
        assertThat(response.get().sizeBytes()).isEqualTo(stored.sizeBytes());
        assertThat(response.get().url()).contains("models/t-34-85.glb");
        assertThat(response.get().url()).contains("X-Amz-Signature");
    }

    @Test
    void returnsEmptyForUningestedVehicle() {
        assertThat(modelService.getModel(424242)).isEmpty();
    }
}
