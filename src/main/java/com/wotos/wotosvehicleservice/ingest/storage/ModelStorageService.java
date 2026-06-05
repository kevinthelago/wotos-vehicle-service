package com.wotos.wotosvehicleservice.ingest.storage;

import com.wotos.wotosvehicleservice.config.StorageProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.time.Instant;

/**
 * Uploads {@code .glb} model files to object storage (MinIO / S3) and upserts the
 * corresponding {@link VehicleModelAsset} row. Created only when a {@link MinioClient}
 * bean is present (i.e. {@code storage.s3.access-key} is configured).
 */
@Component
@ConditionalOnBean(MinioClient.class)
public class ModelStorageService {

    private static final Logger log = LoggerFactory.getLogger(ModelStorageService.class);

    private final MinioClient minioClient;
    private final StorageProperties props;
    private final VehicleModelAssetRepository assetRepository;

    public ModelStorageService(MinioClient minioClient, StorageProperties props,
                               VehicleModelAssetRepository assetRepository) {
        this.minioClient = minioClient;
        this.props = props;
        this.assetRepository = assetRepository;
    }

    /** Creates the configured bucket if it does not already exist. */
    @PostConstruct
    void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(props.bucket()).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(props.bucket()).build());
                log.info("Created storage bucket: {}", props.bucket());
            }
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to ensure storage bucket '" + props.bucket() + "' exists", e);
        }
    }

    /**
     * Uploads {@code bytes} to {@code models/{canonicalName}.glb} in the configured
     * bucket, then upserts the {@link VehicleModelAsset} metadata row (one row per
     * vehicle, latest wins).
     */
    public VehicleModelAsset storeModel(int vehicleId, String canonicalName, byte[] bytes) {
        String objectKey = "models/" + canonicalName + ".glb";
        try {
            ObjectWriteResponse response = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(props.bucket())
                            .object(objectKey)
                            .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                            .contentType("model/gltf-binary")
                            .build());

            VehicleModelAsset asset = assetRepository.findByVehicleId(vehicleId)
                    .orElseGet(VehicleModelAsset::new);
            asset.setVehicleId(vehicleId);
            asset.setBucket(props.bucket());
            asset.setObjectKey(objectKey);
            asset.setEtag(response.etag());
            asset.setSizeBytes((long) bytes.length);
            asset.setFormat("glb");
            asset.setDracoCompressed(false);
            asset.setUploadedAt(Instant.now());
            return assetRepository.save(asset);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to upload model for vehicle " + vehicleId + ": " + e.getMessage(), e);
        }
    }
}
