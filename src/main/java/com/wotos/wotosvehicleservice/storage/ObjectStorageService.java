package com.wotos.wotosvehicleservice.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;

/**
 * Uploads/downloads {@code .glb} model blobs and mints short-lived presigned GET
 * URLs. Uploads are validated against the R4 budgets — refused above 4 MB or 100k
 * triangles — and a missing Draco compression only warns (ingestion may upload then
 * optimize). The content-MD5 is sent so S3/MinIO verifies integrity; the returned
 * ETag is captured for the model-asset row.
 */
@Service
public class ObjectStorageService {

    private static final Logger log = LoggerFactory.getLogger(ObjectStorageService.class);

    static final long MAX_BYTES = 4L * 1024 * 1024;   // 4 MB (R4)
    static final long MAX_TRIANGLES = 100_000L;        // 100k tris (R4)
    private static final String GLB_CONTENT_TYPE = "model/gltf-binary";

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final StorageProperties properties;
    private final ObjectMapper objectMapper;

    public ObjectStorageService(S3Client s3Client, S3Presigner s3Presigner,
                                StorageProperties properties, ObjectMapper objectMapper) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    /**
     * Validates and uploads a {@code .glb} blob.
     *
     * @throws AssetTooLargeException   if larger than {@link #MAX_BYTES}
     * @throws AssetTooComplexException if it has more than {@link #MAX_TRIANGLES} triangles
     * @throws MalformedGlbException    if the bytes are not a parseable .glb
     */
    public StoredObject uploadGlb(String key, byte[] data) {
        if (data.length > MAX_BYTES) {
            throw new AssetTooLargeException(
                    "model " + key + " is " + data.length + " bytes (> " + MAX_BYTES + ")");
        }
        GlbInspector.GlbInfo info = GlbInspector.inspect(data, objectMapper);
        if (info.triangleCount() > MAX_TRIANGLES) {
            throw new AssetTooComplexException(
                    "model " + key + " has " + info.triangleCount() + " triangles (> " + MAX_TRIANGLES + ")");
        }
        if (!info.dracoCompressed()) {
            log.warn("model {} is not Draco-compressed; uploading anyway (optimize later)", key);
        }

        PutObjectResponse response = s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(properties.getBucket())
                        .key(key)
                        .contentMD5(base64Md5(data))
                        .contentType(GLB_CONTENT_TYPE)
                        .build(),
                RequestBody.fromBytes(data));

        String etag = stripQuotes(response.eTag());
        return new StoredObject(properties.getBucket(), key, etag, data.length,
                info.dracoCompressed(), info.triangleCount());
    }

    public byte[] download(String key) {
        return s3Client.getObjectAsBytes(GetObjectRequest.builder()
                .bucket(properties.getBucket()).key(key).build()).asByteArray();
    }

    public boolean exists(String key) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(properties.getBucket()).key(key).build());
            return true;
        } catch (NoSuchKeyException | NoSuchBucketException e) {
            return false;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw new StorageException("failed to stat object " + key, e);
        }
    }

    /** Presigned GET URL valid for the configured TTL (the model endpoint contract is 60s). */
    public URL presignedGetUrl(String key) {
        return presignedGetUrl(key, Duration.ofSeconds(properties.getSignedUrlTtlSeconds()));
    }

    public URL presignedGetUrl(String key, Duration ttl) {
        GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(ttl)
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(properties.getBucket()).key(key).build())
                .build();
        return s3Presigner.presignGetObject(request).url();
    }

    /** Creates the configured bucket if it does not already exist. */
    public void ensureBucketExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(properties.getBucket()).build());
        } catch (NoSuchBucketException e) {
            log.info("creating object-storage bucket {}", properties.getBucket());
            s3Client.createBucket(CreateBucketRequest.builder().bucket(properties.getBucket()).build());
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                log.info("creating object-storage bucket {}", properties.getBucket());
                s3Client.createBucket(CreateBucketRequest.builder().bucket(properties.getBucket()).build());
            } else {
                throw new StorageException("failed to ensure bucket " + properties.getBucket(), e);
            }
        }
    }

    private static String base64Md5(byte[] data) {
        try {
            return Base64.getEncoder().encodeToString(MessageDigest.getInstance("MD5").digest(data));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 not available", e);
        }
    }

    private static String stripQuotes(String etag) {
        if (etag == null) {
            return null;
        }
        return etag.replace("\"", "");
    }
}
