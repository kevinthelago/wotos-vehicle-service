package com.wotos.wotosvehicleservice.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Auto-creates the models bucket on startup when object storage is configured.
 * Skips when neither an endpoint nor credentials are set (so a DB-only local boot
 * does not reach out to AWS), and never fails startup on a storage error — the
 * service can still serve cached/local data while storage is unavailable.
 */
@Component
public class BucketInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BucketInitializer.class);

    private final ObjectStorageService objectStorageService;
    private final StorageProperties properties;

    public BucketInitializer(ObjectStorageService objectStorageService, StorageProperties properties) {
        this.objectStorageService = objectStorageService;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(properties.getEndpoint()) && !StringUtils.hasText(properties.getAccessKey())) {
            log.info("object storage not configured (no endpoint/credentials); skipping bucket init");
            return;
        }
        try {
            objectStorageService.ensureBucketExists();
        } catch (RuntimeException e) {
            log.warn("could not ensure bucket {} on startup: {}", properties.getBucket(), e.getMessage());
        }
    }
}
