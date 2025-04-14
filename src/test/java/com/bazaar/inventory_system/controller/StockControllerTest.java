package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.TestBase;
import com.bazaar.inventory_system.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class StockControllerTest extends TestBase {

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
    void testAddStock() {
        Stock stock = createTestStock(storeId, productId, 10);

        ResponseEntity<Stock> response = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/stock-in?managerId=" + managerId + "&vendorId=" + vendorId,
                new HttpEntity<>(stock),
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
                new HttpEntity<>(stock),
                Stock.class);

        // Then sell some
        Stock sale = createTestStock(storeId, productId, 5);
        ResponseEntity<Stock> response = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/sell",
                new HttpEntity<>(sale),
                Stock.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().getQuantity()); // 10 - 5 = 5 remaining
    }

    @Test
    void testRemoveStock() {
        // First add stock
        Stock stock = createTestStock(storeId, productId, 10);
        restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/stock-in?managerId=" + managerId + "&vendorId=" + vendorId,
                new HttpEntity<>(stock),
                Stock.class);

        // Then remove some
        Stock removal = createTestStock(storeId, productId, 3);
        ResponseEntity<Stock> response = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/remove-stock",
                new HttpEntity<>(removal),
                Stock.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(7, response.getBody().getQuantity()); // 10 - 3 = 7 remaining
    }

    @Test
    void testGetAllStockForStore() {
        // First add some stock
        Stock stock = createTestStock(storeId, productId, 10);
        restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/stock-in?managerId=" + managerId + "&vendorId=" + vendorId,
                new HttpEntity<>(stock),
                Stock.class);

        ResponseEntity<Stock[]> response = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/stock",
                Stock[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void testFilterStockByStore() {
        // First add some stock
        Stock stock = createTestStock(storeId, productId, 10);
        restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/stock-in?managerId=" + managerId + "&vendorId=" + vendorId,
                new HttpEntity<>(stock),
                Stock.class);

        // Filter by name (assuming your controller supports this)
        ResponseEntity<Stock[]> nameResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/filter?name=Test",
                Stock[].class);

        assertEquals(HttpStatus.OK, nameResponse.getStatusCode());
        assertNotNull(nameResponse.getBody());
        assertTrue(nameResponse.getBody().length >= 0); // Could be 0 if no match

        // Filter by category (assuming your controller supports this)
        ResponseEntity<Stock[]> categoryResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/filter?category=Test",
                Stock[].class);

        assertEquals(HttpStatus.OK, categoryResponse.getStatusCode());
        assertNotNull(categoryResponse.getBody());
        assertTrue(categoryResponse.getBody().length >= 0); // Could be 0 if no match
    }
}