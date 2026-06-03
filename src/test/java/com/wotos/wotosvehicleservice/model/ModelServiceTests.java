package com.wotos.wotosvehicleservice.model;

import com.wotos.wotosvehicleservice.storage.ObjectStorageService;
import com.wotos.wotosvehicleservice.storage.StoredObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Service-layer unit tests mocking the repository and the S3 wrapper (no Spring,
 * no Docker) — verifies a fresh signed URL is minted and the response is shaped
 * to the contract.
 */
@ExtendWith(MockitoExtension.class)
class ModelServiceTests {

    @Mock
    private ModelAssetRepository modelAssetRepository;

    @Mock
    private ObjectStorageService objectStorageService;

    @InjectMocks
    private ModelService modelService;

    @Test
    void buildsModelResponseWithFreshSignedUrl() throws Exception {
        ModelAsset asset = new ModelAsset();
        asset.setVehicleId(5137);
        asset.setBucket("wotos-models");
        asset.setObjectKey("models/t-34-85.glb");
        asset.setEtag("etag123");
        asset.setSizeBytes(2048L);
        asset.setFormat("glb");
        when(modelAssetRepository.findByVehicleId(5137)).thenReturn(Optional.of(asset));
        when(objectStorageService.presignedGetUrl("models/t-34-85.glb"))
                .thenReturn(URI.create("http://minio:9000/wotos-models/models/t-34-85.glb?X-Amz-Signature=abc").toURL());

        Optional<ModelResponse> response = modelService.getModel(5137);

        assertThat(response).isPresent();
        assertThat(response.get().url()).contains("X-Amz-Signature");
        assertThat(response.get().etag()).isEqualTo("etag123");
        assertThat(response.get().sizeBytes()).isEqualTo(2048L);
        assertThat(response.get().format()).isEqualTo("glb");
    }

    @Test
    void returnsEmptyWhenNoAsset() {
        when(modelAssetRepository.findByVehicleId(9999)).thenReturn(Optional.empty());

        assertThat(modelService.getModel(9999)).isEmpty();
    }

    @Test
    void saveModelAssetUpsertsFromUploadResult() {
        StoredObject stored = new StoredObject("wotos-models", "models/maus.glb", "etagX", 4096L, true, 50_000L);
        when(modelAssetRepository.findByVehicleId(6753)).thenReturn(Optional.empty());
        when(modelAssetRepository.save(any(ModelAsset.class))).thenAnswer(inv -> inv.getArgument(0));

        ModelAsset saved = modelService.saveModelAsset(6753, stored);

        assertThat(saved.getVehicleId()).isEqualTo(6753);
        assertThat(saved.getObjectKey()).isEqualTo("models/maus.glb");
        assertThat(saved.getEtag()).isEqualTo("etagX");
        assertThat(saved.isDracoCompressed()).isTrue();
        assertThat(saved.getFormat()).isEqualTo("glb");
        assertThat(saved.getUploadedAt()).isNotNull();
    }
}
