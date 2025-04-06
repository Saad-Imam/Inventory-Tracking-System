package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.exception.InvalidStoreRequestException;
import com.bazaar.inventory_system.exception.StoreNotFoundException;
import com.bazaar.inventory_system.model.Store;
import com.bazaar.inventory_system.repository.StoreRepository;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stores")
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;

    // GET all stores
    @Cacheable(value = "stores")
    @GetMapping
    public ResponseEntity<List<Store>> getAllStores() {
        List<Store> stores = storeRepository.findAll();
        return ResponseEntity.ok(stores);
    }

    // GET store by ID
    @Cacheable(value = "storeById", key = "#storeId")
    @GetMapping("/{storeId}")
    public ResponseEntity<Store> getStoreById(@PathVariable Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreNotFoundException(storeId));
        return ResponseEntity.ok(store);
    }

    // CREATE store
    @PostMapping
    public ResponseEntity<Store> createStore(@Valid @RequestBody Store store) {
        // Validate store name is unique
        if (storeRepository.existsByName(store.getName())) {
            throw new InvalidStoreRequestException(
                    "Store name '" + store.getName() + "' already exists");
        }

        Store savedStore = storeRepository.save(store);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStore);
    }

    // UPDATE store
    @CacheEvict(value = "storeById", key = "#storeId")
    @PutMapping("/{storeId}")
    public ResponseEntity<Store> updateStore(
            @PathVariable Long storeId,
            @Valid @RequestBody Store storeDetails) {

        // Verify store exists
        Store existingStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreNotFoundException(storeId));

        // Validate name uniqueness (if changed)
        if (!existingStore.getName().equals(storeDetails.getName()) &&
                storeRepository.existsByName(storeDetails.getName())) {
            throw new InvalidStoreRequestException(
                    "Store name '" + storeDetails.getName() + "' already exists");
        }

        // Update fields
        existingStore.setName(storeDetails.getName());
        existingStore.setLocation(storeDetails.getLocation());
        // Add other fields as needed

        Store updatedStore = storeRepository.save(existingStore);
        return ResponseEntity.ok(updatedStore);
    }

    // DELETE store
    @CacheEvict(value = "storeById", key = "#storeId")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long storeId) {
        // Verify store exists
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreNotFoundException(storeId));
        storeRepository.delete(store);
        return ResponseEntity.noContent().build();
    }
}