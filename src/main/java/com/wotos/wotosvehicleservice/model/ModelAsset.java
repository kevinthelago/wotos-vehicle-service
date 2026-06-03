package com.wotos.wotosvehicleservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Metadata for a vehicle's {@code .glb} model blob. The bytes live in object storage;
 * this row holds only the locator (bucket + key) and integrity/size info, so the
 * model endpoint can mint a fresh signed URL. One row per vehicle (latest wins).
 */
@Entity
@Table(name = "vehicle_model_assets")
public class ModelAsset {

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
    private boolean dracoCompressed;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    public ModelAsset() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isDracoCompressed() {
        return dracoCompressed;
    }

    public void setDracoCompressed(boolean dracoCompressed) {
        this.dracoCompressed = dracoCompressed;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
