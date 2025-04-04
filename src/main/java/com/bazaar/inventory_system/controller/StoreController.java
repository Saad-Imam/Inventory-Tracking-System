package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.model.Store;
import com.bazaar.inventory_system.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stores")
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;

    @GetMapping
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    @GetMapping("/{storeId}")
    public Optional<Store> getStoreById(@PathVariable Long storeId) {
        return storeRepository.findById(storeId);
    }

    @PostMapping
    public Store createStore(@RequestBody Store store) {
        return storeRepository.save(store);
    }

    // Add update and delete methods as needed
}

