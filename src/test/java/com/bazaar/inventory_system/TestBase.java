package com.bazaar.inventory_system;

import com.bazaar.inventory_system.config.TestConfig;
import com.bazaar.inventory_system.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate; //For making HTTP requests in integration tests
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles; //Use specific profile inside application-test.properties

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //to prevent port conflicts
@ActiveProfiles("test")
@Import(TestConfig.class) // Add this line
public abstract class TestBase {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    protected String baseUrl;

    @BeforeEach //setUp() will run before every other test method, to construct URL for APIs
    protected void setUp() {
        baseUrl = "http://localhost:" + port;
    }
    //creating test objects:
    protected Product createTestProduct() {
        Product product = new Product();
        product.setName("Test Product");
        product.setCategory("Test Category");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(9.99));
        return product;
    }

    protected Store createTestStore() {
        Store store = new Store();
        store.setName("Test Store");
        store.setLocation("Test Location");
        return store;
    }

    protected Manager createTestManager() {
        Manager manager = new Manager();
        manager.setUsername("testmanager");
        manager.setPasswordHash("password");
        return manager;
    }

    protected Vendor createTestVendor() {
        Vendor vendor = new Vendor();
        vendor.setName("Test Vendor");
        return vendor;
    }

    protected Stock createTestStock(Long storeId, Long productId, int quantity) {
        Stock stock = new Stock();
        stock.setStoreId(storeId);
        stock.setProductId(productId);
        stock.setQuantity(quantity);
        return stock;
    }

    protected StockMovement createTestStockMovement(Long storeId, Long productId, int quantityChange, String movementType) {
        StockMovement movement = new StockMovement();
        movement.setStoreId(storeId);
        movement.setProductId(productId);
        movement.setQuantityChange(quantityChange);
        movement.setMovementType(movementType);
        movement.setTimestamp(LocalDateTime.now());
        return movement;
    }
}