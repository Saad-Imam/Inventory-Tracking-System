// src/test/java/com/bazaar/inventory_system/controller/StockMovementControllerTest.java
package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.TestBase;
import com.bazaar.inventory_system.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StockMovementControllerTest extends TestBase {

    private Long storeId;
    private Long productId;
    private Long managerId;
    private Long vendorId;

    private <T> T extractResponseBody(ResponseEntity<T> response, String entityName) {
        assertNotNull(response, entityName + " response should not be null");
        T body = response.getBody();
        assertNotNull(body, entityName + " response body should not be null");
        return body;
    }

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        // Create a store
        Store store = createTestStore();
        ResponseEntity<Store> storeResponse = restTemplate.postForEntity(
                baseUrl + "/stores",
                new HttpEntity<>(store),
                Store.class);
        storeId = extractResponseBody(storeResponse, "Store").getStoreId();

        // Create a product
        Product product = createTestProduct();
        ResponseEntity<Product> productResponse = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(product),
                Product.class);
        productId = extractResponseBody(productResponse, "Product").getProductId();

        // Create a manager
        Manager manager = createTestManager();
        ResponseEntity<Manager> managerResponse = restTemplate.postForEntity(
                baseUrl + "/managers",
                new HttpEntity<>(manager),
                Manager.class);
        managerId = extractResponseBody(managerResponse, "Manager").getmanagerId();

        // Create a vendor
        Vendor vendor = createTestVendor();
        ResponseEntity<Vendor> vendorResponse = restTemplate.postForEntity(
                baseUrl + "/vendors",
                new HttpEntity<>(vendor),
                Vendor.class);
        vendorId = extractResponseBody(vendorResponse, "Vendor").getVendorId();    }

    @Test
    void testCreateStockMovement() {
        StockMovement movement = createTestStockMovement(storeId, productId, 10, "STOCK-IN");

        ResponseEntity<StockMovement> response = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/stock-movements?managerId=" + managerId + "&vendorId=" + vendorId,
                new HttpEntity<>(movement),
                StockMovement.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().getQuantityChange());
    }

    @Test
    void testGetAllStockMovements() {
        // First create a movement
        StockMovement movement = createTestStockMovement(storeId, productId, 10, "STOCK-IN");
        restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/stock-movements?managerId=" + managerId + "&vendorId=" + vendorId,
                new HttpEntity<>(movement),
                StockMovement.class);

        ResponseEntity<StockMovement[]> response = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/stock-movements",
                StockMovement[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void testGetStockMovementById() {
        // First create a movement
        StockMovement movement = createTestStockMovement(storeId, productId, 10, "STOCK-IN");
        ResponseEntity<StockMovement> createResponse = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/stock-movements?managerId=" + managerId + "&vendorId=" + vendorId,
                new HttpEntity<>(movement),
                StockMovement.class);

        Long movementId = createResponse.getBody().getStockMovementId();

        // Then get it by ID
        ResponseEntity<StockMovement> response = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/stock-movements/" + movementId,
                StockMovement.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movementId, response.getBody().getStockMovementId());
    }

    @Test
    void testFilterStockMovements() {
        // First create a movement
        StockMovement movement = createTestStockMovement(storeId, productId, 10, "STOCK-IN");
        restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/stock-movements?managerId=" + managerId + "&vendorId=" + vendorId,
                new HttpEntity<>(movement),
                StockMovement.class);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(1);

        // Filter by date range
        ResponseEntity<StockMovement[]> dateResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/stock-movements?startDate=" + start + "&endDate=" + end,
                StockMovement[].class);

        assertEquals(HttpStatus.OK, dateResponse.getStatusCode());
        assertTrue(dateResponse.getBody().length > 0);

        // Filter by product and date
        ResponseEntity<StockMovement[]> productDateResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/stock-movements?productId=" + productId + "&startDate=" + start + "&endDate=" + end,
                StockMovement[].class);

        assertEquals(HttpStatus.OK, productDateResponse.getStatusCode());
        assertTrue(productDateResponse.getBody().length > 0);
    }

    @Test
    void testDeleteStockMovement() {
        // First create a movement
        StockMovement movement = createTestStockMovement(storeId, productId, 10, "STOCK-IN");
        ResponseEntity<StockMovement> createResponse = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/stock-movements?managerId=" + managerId + "&vendorId=" + vendorId,
                new HttpEntity<>(movement),
                StockMovement.class);

        Long movementId = createResponse.getBody().getStockMovementId();

        // Delete the movement
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/stores/" + storeId + "/stock-movements/" + movementId,
                HttpMethod.DELETE,
                new HttpEntity<>(createHeaders("admin", "admin")),
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verify it's gone
        ResponseEntity<StockMovement> getResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/stock-movements/" + movementId,
                StockMovement.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }
}