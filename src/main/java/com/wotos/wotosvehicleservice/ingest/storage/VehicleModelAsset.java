package com.wotos.wotosvehicleservice.ingest.storage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Metadata row for a vehicle's {@code .glb} model blob stored in object storage.
 * The blob itself lives in the configured S3/MinIO bucket; this row tracks its
 * location, size, and upload time. One row per vehicle (unique on {@code vehicle_id}).
 */
@Entity
@Table(name = "vehicle_model_assets")
public class VehicleModelAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false, unique = true)
    private Integer vehicleId;

    @Column(nullable = false)
    private String bucket;

    @Column(name = "object_key", nullable = false)
    private String objectKey;

    private String etag;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(nullable = false)
    private String format = "glb";

    @Column(name = "draco_compressed", nullable = false)
    private boolean dracoCompressed = false;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    public VehicleModelAsset() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getVehicleId() { return vehicleId; }
    public void setVehicleId(Integer vehicleId) { this.vehicleId = vehicleId; }

    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }

    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }

    public String getEtag() { return etag; }
    public void setEtag(String etag) { this.etag = etag; }

    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public boolean isDracoCompressed() { return dracoCompressed; }
    public void setDracoCompressed(boolean dracoCompressed) { this.dracoCompressed = dracoCompressed; }

    public Instant getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
}
