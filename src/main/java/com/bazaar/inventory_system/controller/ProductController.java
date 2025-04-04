package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.model.Product;
import com.bazaar.inventory_system.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired; //for dependency injection
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

    @GetMapping //GET HTTP requests mapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{productId}")
    public Optional<Product> getProductById(@PathVariable Long productId) {
        //@PathVariable allows accessing variables enclosed with {} in the path
        return productRepository.findById(productId);
    }

    @PostMapping //POST HTTP request mapping
    public Product createProduct(@RequestBody Product product) {
        /*
         @RequestBody Product product annotation indicates that the method expects the request body to contain a JSON
          representation of a Product object. Spring will automatically convert this JSON into a Product instance
         */
        return productRepository.save(product);
    }

    // Add update and delete methods as needed
}
