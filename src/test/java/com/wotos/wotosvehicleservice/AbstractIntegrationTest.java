package com.wotos.wotosvehicleservice;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for full-context integration tests. Boots a real MySQL 8 via
 * Testcontainers so Flyway migrations run against genuine MySQL (not H2).
 *
 * <p><b>Singleton-container pattern.</b> The container is started once in a static
 * initializer and intentionally NOT annotated with {@code @Container}, so
 * Testcontainers does not stop/restart it per test class. Several integration tests
 * (some with {@code @MockBean}, which forces a separate Spring context) share it; a
 * per-class start/stop would hand a cached context a dead port and fail with
 * "Communications link failure". The JVM (Ryuk) reaps the container at exit.
 *
 * <p>{@code @Testcontainers(disabledWithoutDocker = true)} contributes only its
 * skip-when-no-Docker execution condition — evaluated before the test class is
 * initialized, so the static container never starts where Docker is unavailable
 * (e.g. local dev here); CI runs the tests for real.
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
public abstract class AbstractIntegrationTest {

    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");
    static final MinIOContainer MINIO = new MinIOContainer("minio/minio:RELEASE.2024-06-13T22-53-53Z");

    static {
        MYSQL.start();
        MINIO.start();
    }

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);

        registry.add("storage.s3.endpoint", MINIO::getS3URL);
        registry.add("storage.s3.access-key", MINIO::getUserName);
        registry.add("storage.s3.secret-key", MINIO::getPassword);
        registry.add("storage.s3.region", () -> "us-east-1");
        registry.add("storage.s3.bucket", () -> "wotos-models");
    }
}
