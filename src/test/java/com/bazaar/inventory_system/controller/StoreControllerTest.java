// src/test/java/com/bazaar/inventory_system/controller/StoreControllerTest.java
package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.TestBase;
import com.bazaar.inventory_system.model.Store;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

class StoreControllerTest extends TestBase {

    @Test
    void testCreateStore() {
        Store store = createTestStore();

        ResponseEntity<Store> response = restTemplate.postForEntity(
                baseUrl + "/stores",
                new HttpEntity<>(store),
                Store.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(store.getName(), response.getBody().getName());
    }

    @Test
    void testGetAllStores() {
        ResponseEntity<Store[]> response = restTemplate.getForEntity(
                baseUrl + "/stores",
                Store[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 0);
    }

    @Test
    void testGetStoreById() {
        // First create a store
        Store store = createTestStore();
        ResponseEntity<Store> createResponse = restTemplate.postForEntity(
                baseUrl + "/stores",
                new HttpEntity<>(store),
                Store.class);
        assertNotNull(createResponse.getBody());
        Long storeId = createResponse.getBody().getStoreId();

        // Then get it by ID
        ResponseEntity<Store> response = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId,
                Store.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(storeId, response.getBody().getStoreId());
    }

    @Test
    void testUpdateStore() {
        // First create a store
        Store store = createTestStore();
        ResponseEntity<Store> createResponse = restTemplate.postForEntity(
                baseUrl + "/stores",
                new HttpEntity<>(store),
                Store.class);
        assertNotNull(createResponse.getBody());
        Long storeId = createResponse.getBody().getStoreId();
        store.setName("Updated Store Name");

        // Update the store
        ResponseEntity<Store> response = restTemplate.exchange(
                baseUrl + "/stores/" + storeId,
                HttpMethod.PUT,
                new HttpEntity<>(store),
                Store.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Store Name", response.getBody().getName());
    }

    @Test
    void testDeleteStore() {
        // First create a store
        Store store = createTestStore();
        ResponseEntity<Store> createResponse = restTemplate.postForEntity(
                baseUrl + "/stores",
                new HttpEntity<>(store),
                Store.class);
        assertNotNull(createResponse.getBody());
        Long storeId = createResponse.getBody().getStoreId();

        // Delete the store
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl + "/stores/" + storeId,
                HttpMethod.DELETE,
                new HttpEntity<>(createHeaders("admin", "admin")),
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verify it's gone
        ResponseEntity<Store> getResponse = restTemplate.getForEntity(
                baseUrl + "/stores/" + storeId,
                Store.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }
}