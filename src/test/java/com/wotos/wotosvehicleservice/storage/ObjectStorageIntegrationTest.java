package com.wotos.wotosvehicleservice.storage;

import com.wotos.wotosvehicleservice.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Uploads/downloads a fixture .glb against a Testcontainers MinIO, and exercises the
 * R4 upload guards. The bucket is auto-created on startup by {@link BucketInitializer}.
 */
class ObjectStorageIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectStorageService objectStorageService;

    @Test
    void uploadsAndDownloadsGlb() {
        byte[] glb = TestGlbFactory.triangleGlb(1000);

        StoredObject stored = objectStorageService.uploadGlb("models/test/upload.glb", glb);

        assertThat(stored.bucket()).isEqualTo("wotos-models");
        assertThat(stored.key()).isEqualTo("models/test/upload.glb");
        assertThat(stored.etag()).isNotBlank();
        assertThat(stored.sizeBytes()).isEqualTo(glb.length);
        assertThat(stored.dracoCompressed()).isFalse();
        assertThat(stored.triangleCount()).isEqualTo(1000);

        assertThat(objectStorageService.exists("models/test/upload.glb")).isTrue();
        assertThat(objectStorageService.download("models/test/upload.glb")).isEqualTo(glb);
    }

    @Test
    void mintsPresignedGetUrl() {
        objectStorageService.uploadGlb("models/test/presign.glb", TestGlbFactory.triangleGlb(10));

        URL url = objectStorageService.presignedGetUrl("models/test/presign.glb");

        assertThat(url.toString()).contains("models/test/presign.glb");
        assertThat(url.toString()).contains("X-Amz-Signature");
        assertThat(url.toString()).contains("X-Amz-Expires");
    }

    @Test
    void refusesOversizedAsset() {
        byte[] tooBig = new byte[(int) (ObjectStorageService.MAX_BYTES + 1)];
        assertThatThrownBy(() -> objectStorageService.uploadGlb("models/test/big.glb", tooBig))
                .isInstanceOf(AssetTooLargeException.class);
    }

    @Test
    void refusesTooManyTriangles() {
        byte[] dense = TestGlbFactory.triangleGlb(150_000);
        assertThatThrownBy(() -> objectStorageService.uploadGlb("models/test/dense.glb", dense))
                .isInstanceOf(AssetTooComplexException.class);
    }
}
