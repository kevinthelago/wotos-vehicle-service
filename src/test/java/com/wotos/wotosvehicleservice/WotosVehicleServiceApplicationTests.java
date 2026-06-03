package com.wotos.wotosvehicleservice;

import org.junit.jupiter.api.Test;

/**
 * Verifies the full application context loads — now including the JPA layer and
 * Flyway migrations against a Testcontainers MySQL (see {@link AbstractIntegrationTest}).
 */
public class WotosVehicleServiceApplicationTests extends AbstractIntegrationTest {

	@Test
	public void contextLoads() {
	}

}
