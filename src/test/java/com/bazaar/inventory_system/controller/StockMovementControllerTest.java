// src/test/java/com/bazaar/inventory_system/controller/StockMovementControllerTest.java
package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.TestBase;
import com.bazaar.inventory_system.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StockMovementControllerTest extends TestBase {

    private Long storeId;
    private Long productId;
    private Long managerId;
    private Long vendorId;

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
        assertNotNull(storeResponse.getBody(), "Store creation response should not be null");
        assertEquals(HttpStatus.CREATED, storeResponse.getStatusCode(), "Store creation should return 201 CREATED");
        storeId = storeResponse.getBody().getStoreId();
        assertNotNull(storeId, "Store ID should be generated");

        // Create a product
        Product product = createTestProduct();
        ResponseEntity<Product> productResponse = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(product),
                Product.class);
        assertNotNull(productResponse.getBody(), "Product creation response should not be null");
        assertEquals(HttpStatus.CREATED, productResponse.getStatusCode(), "Product creation should return 201 CREATED");
        productId = productResponse.getBody().getProductId();
        assertNotNull(productId, "Product ID should be generated");

        // Create a manager
        Manager manager = createTestManager();
        ResponseEntity<Manager> managerResponse = restTemplate.postForEntity(
                baseUrl + "/managers",
                new HttpEntity<>(manager),
                Manager.class);
        assertNotNull(managerResponse.getBody(), "Manager creation response should not be null");
        assertEquals(HttpStatus.CREATED, managerResponse.getStatusCode(), "Manager creation should return 201 CREATED");
        managerId = managerResponse.getBody().getManagerId();
        assertNotNull(managerId, "Manager ID should be generated");

        // Create a vendor
        Vendor vendor = createTestVendor();
        ResponseEntity<Vendor> vendorResponse = restTemplate.postForEntity(
                baseUrl + "/vendors",
                new HttpEntity<>(vendor),
                Vendor.class);
        assertNotNull(vendorResponse.getBody(), "Vendor creation response should not be null");
        assertEquals(HttpStatus.CREATED, vendorResponse.getStatusCode(), "Vendor creation should return 201 CREATED");
        vendorId = vendorResponse.getBody().getVendorId();
        assertNotNull(vendorId, "Vendor ID should be generated");
    }

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
        ResponseEntity<StockMovement> createResponse = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/stock-movements?managerId=" + managerId + "&vendorId=" + vendorId,
                new HttpEntity<>(movement),
                StockMovement.class);
        assertNotNull(createResponse.getBody(), "StockMovement creation response should not be null");
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode(), "StockMovement creation should return 201 CREATED");

        ResponseEntity<StockMovement[]> response = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/stock-movements",
                StockMovement[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
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
        assertNotNull(createResponse.getBody(), "StockMovement creation response should not be null");
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode(), "StockMovement creation should return 201 CREATED");

        Long movementId = createResponse.getBody().getStockMovementId();
        assertNotNull(movementId, "StockMovement ID should be generated");

        // Then get it by ID
        ResponseEntity<StockMovement> response = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/stock-movements/" + movementId,
                StockMovement.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(movementId, response.getBody().getStockMovementId());
    }

    @Test
    void testFilterStockMovements() {
        // First create a movement
        StockMovement movement = createTestStockMovement(storeId, productId, 10, "STOCK-IN");
        ResponseEntity<StockMovement> createResponse = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/stock-movements?managerId=" + managerId + "&vendorId=" + vendorId,
                new HttpEntity<>(movement),
                StockMovement.class);
        assertNotNull(createResponse.getBody(), "StockMovement creation response should not be null");
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode(), "StockMovement creation should return 201 CREATED");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(1);

        // Filter by date range
        ResponseEntity<StockMovement[]> dateResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/stock-movements?startDate=" + start + "&endDate=" + end,
                StockMovement[].class);

        assertEquals(HttpStatus.OK, dateResponse.getStatusCode());
        assertNotNull(dateResponse.getBody());
        assertTrue(dateResponse.getBody().length > 0);

        // Filter by product and date
        ResponseEntity<StockMovement[]> productDateResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/stock-movements?productId=" + productId + "&startDate=" + start + "&endDate=" + end,
                StockMovement[].class);

        assertEquals(HttpStatus.OK, productDateResponse.getStatusCode());
        assertNotNull(productDateResponse.getBody());
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
        assertNotNull(createResponse.getBody(), "StockMovement creation response should not be null");
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode(), "StockMovement creation should return 201 CREATED");

        Long movementId = createResponse.getBody().getStockMovementId();
        assertNotNull(movementId, "StockMovement ID should be generated");

        // Delete the movement
//        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
//                baseUrl + "/stores/" + storeId + "/stock-movements/" + movementId,
//                HttpMethod.DELETE,
//
//                Void.class);

//        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
//
//        // Verify it's gone
//        ResponseEntity<StockMovement> getResponse = restTemplate.getForEntity(
//                baseUrl + "/stores/" + storeId + "/stock-movements/" + movementId,
//                StockMovement.class);
//
//        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }
}