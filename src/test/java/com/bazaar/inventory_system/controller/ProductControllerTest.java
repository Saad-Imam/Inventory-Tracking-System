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
                new HttpEntity<>(store),
                Store.class);

        // Verify the store was created successfully
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Store creation failed");
        assertNotNull(response.getBody(), "Store creation response body is null");
        storeId = response.getBody().getStoreId();
        assertNotNull(storeId, "Store ID is null after creation");
    }
    @Test
    void testCreateProduct() {
        // Given
        Product product = createTestProduct();

        // When
        ResponseEntity<Product> response = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(product),
                Product.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode(),
                "Expected status code 201 CREATED");

        Product createdProduct = response.getBody();
        assertNotNull(createdProduct, "Response body should not be null");
        assertEquals(product.getName(), createdProduct.getName(),
                "Product name should match");
        assertNotNull(createdProduct.getProductId(),
                "Product ID should be generated");
    }

    @Test
    void testGetAllProducts() {
        // Given - Create a product first
        Product product = createTestProduct();
        restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(product),
                Product.class);

        // When
        ResponseEntity<Product[]> response = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                Product[].class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Expected status code 200 OK");

        Product[] products = response.getBody();
        assertNotNull(products, "Response body should not be null");
        assertTrue(products.length > 0,
                "Should return at least one product");

        boolean found = false;
        for (Product p : products) {
            if (p.getName().equals(product.getName())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Created product should be in the list");
    }

    @Test
    void testGetProductById() {
        // Given - Create a product first
        Product product = createTestProduct();
        ResponseEntity<Product> createResponse = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(product),
                Product.class);

        assertNotNull(createResponse.getBody(),
                "Create response body should not be null");
        Long productId = createResponse.getBody().getProductId();

        // When
        ResponseEntity<Product> response = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/products/" + productId,
                Product.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Expected status code 200 OK");

        Product fetchedProduct = response.getBody();
        assertNotNull(fetchedProduct,
                "Fetched product should not be null");
        assertEquals(productId, fetchedProduct.getProductId(),
                "Product ID should match");
        assertEquals(product.getName(), fetchedProduct.getName(),
                "Product name should match");
    }

    @Test
    void testSearchProducts() {
        // Given - Create test products
        Product product1 = createTestProduct();
        product1.setName("Test Product 1");
        product1.setCategory("Category A");

        Product product2 = createTestProduct();
        product2.setName("Another Product");
        product2.setCategory("Category B");

        restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(product1),
                Product.class);

        restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(product2),
                Product.class);

        // When - Search by name
        ResponseEntity<Product[]> nameResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/products/search?name=Test",
                Product[].class);

        // Then
        assertEquals(HttpStatus.OK, nameResponse.getStatusCode(),
                "Expected status code 200 OK for name search");

        Product[] nameResults = nameResponse.getBody();
        assertNotNull(nameResults,
                "Name search results should not be null");
        assertEquals(1, nameResults.length,
                "Should find one product matching name");
        assertEquals("Test Product 1", nameResults[0].getName(),
                "Should return correct product");

        // When - Search by category
        ResponseEntity<Product[]> categoryResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/products/search?category=Category+B",
                Product[].class);

        // Then
        assertEquals(HttpStatus.OK, categoryResponse.getStatusCode(),
                "Expected status code 200 OK for category search");

        Product[] categoryResults = categoryResponse.getBody();
        assertNotNull(categoryResults,
                "Category search results should not be null");
        assertEquals(1, categoryResults.length,
                "Should find one product in category");
        assertEquals("Category B", categoryResults[0].getCategory(),
                "Should return correct product");
    }

    @Test
    void testUpdateProduct() {
        // Given - Create a product first
        Product product = createTestProduct();
        ResponseEntity<Product> createResponse = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(product),
                Product.class);

        assertNotNull(createResponse.getBody(),
                "Create response body should not be null");
        Long productId = createResponse.getBody().getProductId();

        // Prepare update
        Product updatedDetails = createTestProduct();
        updatedDetails.setName("Updated Product Name");
        updatedDetails.setDescription("New description");
        updatedDetails.setPrice(BigDecimal.valueOf(19.99));
        updatedDetails.setCategory("New Category");

        // When
        ResponseEntity<Product> response = restTemplate.exchange(
                baseUrl + "/stores/" + storeId + "/products/" + productId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedDetails),
                Product.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Expected status code 200 OK");

        Product updatedProduct = response.getBody();
        assertNotNull(updatedProduct,
                "Updated product should not be null");
        assertEquals(productId, updatedProduct.getProductId(),
                "Product ID should remain the same");
        assertEquals("Updated Product Name", updatedProduct.getName(),
                "Product name should be updated");
        assertEquals("New description", updatedProduct.getDescription(),
                "Description should be updated");
        assertEquals(0, BigDecimal.valueOf(19.99).compareTo(updatedProduct.getPrice()),
                "Price should be updated");
        assertEquals("New Category", updatedProduct.getCategory(),
                "Category should be updated");
    }

    @Test
    void testDeleteProduct() {
        // Given - Create a product first
        Product product = createTestProduct();
        ResponseEntity<Product> createResponse = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(product),
                Product.class);

        assertNotNull(createResponse.getBody(),
                "Create response body should not be null");
        Long productId = createResponse.getBody().getProductId();

        // When - Delete the product
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/stores/" + storeId + "/products/" + productId,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode(),
                "Expected status code 204 NO_CONTENT");

        // Verify it's gone
        ResponseEntity<Product> getResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId + "/products/" + productId,
                Product.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode(),
                "Expected status code 404 NOT_FOUND after deletion");
    }

    @Test
    void testCreateProductWithInvalidData() {
        // Given - Product with invalid data (empty name)
        Product invalidProduct = createTestProduct();
        invalidProduct.setName(""); // Invalid - empty name

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/stores/" + storeId + "/products",
                new HttpEntity<>(invalidProduct),
                String.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Expected status code 400 BAD_REQUEST for invalid data");
        assertNotNull(response.getBody(),
                "Error response body should not be null");
        assertTrue(response.getBody().contains("Product name is mandatory"),
                "Error message should indicate validation failure");
    }
}