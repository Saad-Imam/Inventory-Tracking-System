package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.exception.InvalidProductRequestException;
import com.bazaar.inventory_system.exception.ProductNotFoundException;
import com.bazaar.inventory_system.exception.StoreNotFoundException;
import com.bazaar.inventory_system.model.Product;
import com.bazaar.inventory_system.repository.ProductRepository;
import com.bazaar.inventory_system.repository.StoreRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired; //for dependency injection
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; //annotations to handle web requests from Spring MVC web framework
import java.util.List;
import java.util.Optional;/*
Optional is used to represent a value that may or may not be present, which is useful when retrieving a single product
by its ID as it might not exist in the database.
*/

@RestController
@RequestMapping("/stores/{storeId}/products") //maps all HTTP requests with this base path to this controller
public class ProductController {

    @Autowired //inject an instance of this class automatically
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Cacheable(value = "products")
    @GetMapping //GET HTTP requests mapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }
    @Cacheable(value = "productById", key = "#productId")
    @GetMapping("/{productId}")
    //@PathVariable allows accessing variables enclosed with {} in the path
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return ResponseEntity.ok(product);
    }
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @PathVariable Long storeId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {

        // Verify the store exists
        if (!storeRepository.existsById(storeId)) {
            throw new StoreNotFoundException(storeId);
        }

        // Case 1: Both name and category provided
        if (name != null && category != null) {
            List<Product> products = productRepository.findByNameContainingIgnoreCaseAndCategory(name, category);
            return ResponseEntity.ok(products);
        }
        // Case 2: Only name provided
        else if (name != null) {
            List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
            return ResponseEntity.ok(products);
        }
        // Case 3: Only category provided
        else if (category != null) {
            List<Product> products = productRepository.findByCategory(category);
            return ResponseEntity.ok(products);
        }
        // Case 4: No filters provided
        else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping //POST HTTP request mapping
     /*
         @RequestBody annotation indicates that the method expects the request body to contain a JSON
          representation of a Product object. Spring will automatically convert this JSON into a Product instance
         */
    public ResponseEntity<Product> createProduct(
            @PathVariable Long storeId,
            @Valid @RequestBody Product product) {
        // Verify the store exists
        if (!storeRepository.existsById(storeId)) {
            throw new StoreNotFoundException(storeId);
        }
        Product savedProduct = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @CacheEvict(value = "productById", key = "#productId")
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody Product productDetails) {

        // Verify product exists
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setCategory(productDetails.getCategory());

        Product updatedProduct = productRepository.save(existingProduct);
        return ResponseEntity.ok(updatedProduct);
    }
    // DELETE product
    @CacheEvict(value = "productById", key = "#productId")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId) {

        // Verify product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        productRepository.delete(product);
        return ResponseEntity.noContent().build();
    }
}
