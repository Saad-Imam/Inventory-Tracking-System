// src/test/java/com/bazaar/inventory_system/controller/StockControllerTest.java
package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.TestBase;
import com.bazaar.inventory_system.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

class StockControllerTest extends TestBase {

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
                new HttpEntity<>(store, createHeaders("admin", "admin")),
                Store.class);
        storeId = extractResponseBody(storeResponse, "Store").getStoreId();

        // Create a product
        Product product = createTestProduct();
        ResponseEntity<Product> productResponse = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(product, createHeaders("admin", "admin")),
                Product.class);
        productId = extractResponseBody(productResponse, "Product").getProductId();

        // Create a manager
        Manager manager = createTestManager();
        ResponseEntity<Manager> managerResponse = restTemplate.postForEntity(
                baseUrl + "/managers",
                new HttpEntity<>(manager, createHeaders("admin", "admin")),
                Manager.class);
        managerId = extractResponseBody(managerResponse, "Manager").getmanagerId();

        // Create a vendor
        Vendor vendor = createTestVendor();
        ResponseEntity<Vendor> vendorResponse = restTemplate.postForEntity(
                baseUrl + "/vendors",
                new HttpEntity<>(vendor, createHeaders("admin", "admin")),
                Vendor.class);
        vendorId = extractResponseBody(vendorResponse, "Vendor").getVendorId();
    }

    @Test
    void testAddStock() {
        Stock stock = createTestStock(storeId, productId, 10);

        ResponseEntity<Stock> response = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/stock-in?managerId=" + managerId + "&vendorId=" + vendorId,
                new HttpEntity<>(stock, createHeaders("admin", "admin")),
                Stock.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().getQuantity());
    }

    @Test
    void testSellProduct() {
        // First add stock
        Stock stock = createTestStock(storeId, productId, 10);
        restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/stock-in?managerId=" + managerId + "&vendorId=" + vendorId,
                new HttpEntity<>(stock, createHeaders("admin", "admin")),
                Stock.class);

        // Then sell some
        Stock sale = createTestStock(storeId, productId, 5);
        ResponseEntity<Stock> response = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/sell",
                new HttpEntity<>(sale, createHeaders("admin", "admin")),
                Stock.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody().getQuantity()); // 10 - 5 = 5 remaining
    }

    @Test
    void testRemoveStock() {
        // First add stock
        Stock stock = createTestStock(storeId, productId, 10);
        restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/stock-in?managerId=" + managerId + "&vendorId=" + vendorId,
                new HttpEntity<>(stock, createHeaders("admin", "admin")),
                Stock.class);

        // Then remove some
        Stock removal = createTestStock(storeId, productId, 3);
        ResponseEntity<Stock> response = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/remove-stock",
                new HttpEntity<>(removal, createHeaders("admin", "admin")),
                Stock.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(7, response.getBody().getQuantity()); // 10 - 3 = 7 remaining
    }

    @Test
    void testGetAllStockForStore() {
        // First add some stock
        Stock stock = createTestStock(storeId, productId, 10);
        restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/stock-in?managerId=" + managerId + "&vendorId=" + vendorId,
                new HttpEntity<>(stock, createHeaders("admin", "admin")),
                Stock.class);

        ResponseEntity<Stock[]> response = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/stock",
                Stock[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void testFilterStockByStore() {
        // First add some stock
        Stock stock = createTestStock(storeId, productId, 10);
        restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/stock-in?managerId=" + managerId + "&vendorId=" + vendorId,
                new HttpEntity<>(stock, createHeaders("admin", "admin")),
                Stock.class);

        // Filter by name
        ResponseEntity<Stock[]> nameResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/filter?name=Test",
                Stock[].class);

        assertEquals(HttpStatus.OK, nameResponse.getStatusCode());
        assertTrue(nameResponse.getBody().length > 0);

        // Filter by category
        ResponseEntity<Stock[]> categoryResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/filter?category=Test",
                Stock[].class);

        assertEquals(HttpStatus.OK, categoryResponse.getStatusCode());
        assertTrue(categoryResponse.getBody().length > 0);
    }
}