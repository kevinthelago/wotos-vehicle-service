package com.wotos.wotosvehicleservice.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Object-storage configuration bound from {@code storage.s3.*}. Backed by MinIO in
 * local/CI (path-style, custom endpoint) and real AWS S3 in prod (empty endpoint).
 */
@ConfigurationProperties(prefix = "storage.s3")
public class StorageProperties {

    /** S3 endpoint override; empty = real AWS S3, {@code http://minio:9000} locally. */
    private String endpoint = "";
    private String region = "us-east-1";
    private String bucket = "wotos-models";
    private String accessKey = "";
    private String secretKey = "";
    /** TTL applied to presigned GET URLs (the model endpoint contract is 60s). */
    private long signedUrlTtlSeconds = 60;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getSignedUrlTtlSeconds() {
        return signedUrlTtlSeconds;
    }

    public void setSignedUrlTtlSeconds(long signedUrlTtlSeconds) {
        this.signedUrlTtlSeconds = signedUrlTtlSeconds;
    }
}
