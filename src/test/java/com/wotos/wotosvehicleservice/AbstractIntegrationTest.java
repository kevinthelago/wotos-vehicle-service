package com.wotos.wotosvehicleservice;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for full-context integration tests. Boots a real MySQL 8 via
 * Testcontainers and wires it to Spring's datasource through {@link ServiceConnection},
 * so Flyway migrations run against a genuine MySQL instance (not H2). The container
 * is static and shared across every test that extends this class, so it starts once.
 *
 * <p>The MinIO object-storage container is added here in G6 so storage/ingestion
 * integration tests inherit it too.
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
public abstract class AbstractIntegrationTest {

    @Container
    @ServiceConnection
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0");
}
