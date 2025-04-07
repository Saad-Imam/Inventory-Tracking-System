// src/test/java/com/bazaar/inventory_system/controller/ProductControllerTest.java
package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.TestBase;
import com.bazaar.inventory_system.model.Product;
import com.bazaar.inventory_system.model.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductControllerTest extends TestBase {

    private Long storeId;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        // Create a store for product tests
        Store store = createTestStore();
        ResponseEntity<Store> response = restTemplate.postForEntity(
                baseUrl + "/stores",
                new HttpEntity<>(store, createHeaders("admin", "admin")),
                Store.class);
        // to combat NullPointerException warning
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getBody(), "Response body should not be null");
        storeId = response.getBody().getStoreId();
    }

    @Test
    void testCreateProduct() {
        Product product = createTestProduct();

        ResponseEntity<Product> response = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(product, createHeaders("admin", "admin")),
                Product.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(product.getName(), response.getBody().getName());
    }

    @Test
    void testGetAllProducts() {
        // First create a product
        Product product = createTestProduct();
        restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(product, createHeaders("admin", "admin")),
                Product.class);

        ResponseEntity<Product[]> response = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                Product[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void testGetProductById() {
        // First create a product
        Product product = createTestProduct();
        ResponseEntity<Product> createResponse = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(product, createHeaders("admin", "admin")),
                Product.class);

        Long productId = createResponse.getBody().getProductId();

        // Then get it by ID
        ResponseEntity<Product> response = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/products/" + productId,
                Product.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productId, response.getBody().getProductId());
    }

    @Test
    void testSearchProducts() {
        // First create a product
        Product product = createTestProduct();
        restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(product, createHeaders("admin", "admin")),
                Product.class);

        // Search by name
        ResponseEntity<Product[]> nameResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/products/search?name=Test",
                Product[].class);

        assertEquals(HttpStatus.OK, nameResponse.getStatusCode());
        assertTrue(nameResponse.getBody().length > 0);

        // Search by category
        ResponseEntity<Product[]> categoryResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/products/search?category=Test",
                Product[].class);

        assertEquals(HttpStatus.OK, categoryResponse.getStatusCode());
        assertTrue(categoryResponse.getBody().length > 0);
    }

    @Test
    void testUpdateProduct() {
        // First create a product
        Product product = createTestProduct();
        ResponseEntity<Product> createResponse = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(product, createHeaders("admin", "admin")),
                Product.class);

        Long productId = createResponse.getBody().getProductId();
        product.setName("Updated Product Name");

        // Update the product
        ResponseEntity<Product> response = restTemplate.exchange(
                baseUrl + "/stores/" + storeId + "/products/" + productId,
                HttpMethod.PUT,
                new HttpEntity<>(product, createHeaders("admin", "admin")),
                Product.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Product Name", response.getBody().getName());
    }

    @Test
    void testDeleteProduct() {
        // First create a product
        Product product = createTestProduct();
        ResponseEntity<Product> createResponse = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(product, createHeaders("admin", "admin")),
                Product.class);

        Long productId = createResponse.getBody().getProductId();

        // Delete the product
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/stores/" + storeId + "/products/" + productId,
                HttpMethod.DELETE,
                new HttpEntity<>(createHeaders("admin", "admin")),
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verify it's gone
        ResponseEntity<Product> getResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/products/" + productId,
                Product.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }
}