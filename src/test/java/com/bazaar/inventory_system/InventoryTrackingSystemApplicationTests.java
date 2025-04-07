package com.bazaar.inventory_system;

import com.bazaar.inventory_system.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class InventoryTrackingSystemApplicationTests {

	@Test
	void contextLoads() {
		// This test will verify that the application context loads successfully
	}
}