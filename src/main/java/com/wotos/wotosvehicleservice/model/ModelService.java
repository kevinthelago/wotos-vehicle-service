package com.wotos.wotosvehicleservice.model;

import com.wotos.wotosvehicleservice.storage.ObjectStorageService;
import com.wotos.wotosvehicleservice.storage.StoredObject;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * Looks up model-asset metadata and mints a fresh presigned GET URL per request, and
 * upserts asset rows after an upload (used by the ingestion job, G5). One row per
 * vehicle, latest wins.
 */
@Service
public class ModelService {

    private final ModelAssetRepository modelAssetRepository;
    private final ObjectStorageService objectStorageService;

    public ModelService(ModelAssetRepository modelAssetRepository, ObjectStorageService objectStorageService) {
        this.modelAssetRepository = modelAssetRepository;
        this.objectStorageService = objectStorageService;
    }

    /**
     * @return a {@link ModelResponse} with a freshly minted 60s signed URL, or empty
     *         if the vehicle has no model asset yet.
     */
    public Optional<ModelResponse> getModel(Integer vehicleId) {
        return modelAssetRepository.findByVehicleId(vehicleId).map(asset -> {
            String url = objectStorageService.presignedGetUrl(asset.getObjectKey()).toString();
            return new ModelResponse(url, asset.getEtag(), asset.getSizeBytes(), asset.getFormat());
        });
    }

    /** Upserts the model-asset row for a vehicle from an upload result. */
    public ModelAsset saveModelAsset(Integer vehicleId, StoredObject stored) {
        ModelAsset asset = modelAssetRepository.findByVehicleId(vehicleId).orElseGet(ModelAsset::new);
        asset.setVehicleId(vehicleId);
        asset.setBucket(stored.bucket());
        asset.setObjectKey(stored.key());
        asset.setEtag(stored.etag());
        asset.setSizeBytes(stored.sizeBytes());
        asset.setFormat("glb");
        asset.setDracoCompressed(stored.dracoCompressed());
        asset.setUploadedAt(Instant.now());
        return modelAssetRepository.save(asset);
    }
}
