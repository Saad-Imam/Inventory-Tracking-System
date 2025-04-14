package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.TestBase;
import com.bazaar.inventory_system.model.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

class StoreControllerTest extends TestBase {

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        // Clear any existing test data if needed
    }

    @Test
    void testCreateStore() {
        // Given
        Store store = createTestStore();

        // When
        ResponseEntity<Store> response = restTemplate.postForEntity(
                baseUrl + "/stores",
                new HttpEntity<>(store), // No auth headers needed in test profile
                Store.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode(),
                "Expected status code 201 CREATED");

        Store createdStore = response.getBody();
        assertNotNull(createdStore, "Response body should not be null");
        assertEquals(store.getName(), createdStore.getName(),
                "Store name should match");
        assertNotNull(createdStore.getStoreId(),
                "Store ID should be generated");
        assertEquals(store.getLocation(), createdStore.getLocation(),
                "Store location should match");
    }

    @Test
    void testGetAllStores() {
        // Given - Create at least one store first
        Store store = createTestStore();
        restTemplate.postForEntity(
                baseUrl + "/stores",
                new HttpEntity<>(store),
                Store.class);

        // When
        ResponseEntity<Store[]> response = restTemplate.getForEntity(
                baseUrl + "/stores",
                Store[].class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Expected status code 200 OK");

        Store[] stores = response.getBody();
        assertNotNull(stores, "Response body should not be null");
        assertTrue(stores.length > 0,
                "Should return at least one store");

        // Verify the created store is in the list
        boolean found = false;
        for (Store s : stores) {
            if (s.getName().equals(store.getName())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Created store should be in the list");
    }

    @Test
    void testGetStoreById() {
        // Given - Create a store first
        Store store = createTestStore();
        ResponseEntity<Store> createResponse = restTemplate.postForEntity(
                baseUrl + "/stores",
                new HttpEntity<>(store),
                Store.class);

        assertNotNull(createResponse.getBody(),
                "Create response body should not be null");
        Long storeId = createResponse.getBody().getStoreId();

        // When
        ResponseEntity<Store> response = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId,
                Store.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Expected status code 200 OK");

        Store fetchedStore = response.getBody();
        assertNotNull(fetchedStore,
                "Fetched store should not be null");
        assertEquals(storeId, fetchedStore.getStoreId(),
                "Store ID should match");
        assertEquals(store.getName(), fetchedStore.getName(),
                "Store name should match");
    }

    @Test
    void testUpdateStore() {
        // Given - Create a store first
        Store store = createTestStore();
        ResponseEntity<Store> createResponse = restTemplate.postForEntity(
                baseUrl + "/stores",
                new HttpEntity<>(store),
                Store.class);

        assertNotNull(createResponse.getBody(),
                "Create response body should not be null");
        Long storeId = createResponse.getBody().getStoreId();

        // Prepare update
        Store updatedDetails = createTestStore();
        updatedDetails.setName("Updated Store Name");
        updatedDetails.setLocation("Updated Location");

        // When
        ResponseEntity<Store> response = restTemplate.exchange(
                baseUrl + "/stores/" + storeId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedDetails),
                Store.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Expected status code 200 OK");

        Store updatedStore = response.getBody();
        assertNotNull(updatedStore,
                "Updated store should not be null");
        assertEquals(storeId, updatedStore.getStoreId(),
                "Store ID should remain the same");
        assertEquals("Updated Store Name", updatedStore.getName(),
                "Store name should be updated");
        assertEquals("Updated Location", updatedStore.getLocation(),
                "Store location should be updated");
    }

    @Test
    void testDeleteStore() {
        // Given - Create a store first
        Store store = createTestStore();
        ResponseEntity<Store> createResponse = restTemplate.postForEntity(
                baseUrl + "/stores",
                new HttpEntity<>(store),
                Store.class);

        assertNotNull(createResponse.getBody(),
                "Create response body should not be null");
        Long storeId = createResponse.getBody().getStoreId();

        // When - Delete the store
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/stores/" + storeId,
                HttpMethod.DELETE,
                HttpEntity.EMPTY, // No auth headers needed in test profile
                Void.class);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode(),
                "Expected status code 204 NO_CONTENT");

        // Verify it's gone
        ResponseEntity<Store> getResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId,
                Store.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode(),
                "Expected status code 404 NOT_FOUND after deletion");
    }

    @Test
    void testCreateStoreWithDuplicateName() {
        // Given - Create a store first
        Store store = createTestStore();
        restTemplate.postForEntity(
                baseUrl + "/stores",
                new HttpEntity<>(store),
                Store.class);

        // When - Try to create another store with same name
        Store duplicateStore = createTestStore(); // Same name as first store
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/stores",
                new HttpEntity<>(duplicateStore),
                String.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Expected status code 400 BAD_REQUEST for duplicate name");
        assertNotNull(response.getBody(),
                "Error response body should not be null");
        assertTrue(response.getBody().contains("already exists"),
                "Error message should indicate duplicate name");
    }
}