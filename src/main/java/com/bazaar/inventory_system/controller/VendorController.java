package com.bazaar.inventory_system.controller;

import com.bazaar.inventory_system.model.Vendor;
import com.bazaar.inventory_system.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/vendors")
public class VendorController {

    @Autowired
    private VendorRepository vendorRepository;

    @GetMapping
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @GetMapping("/{vendorId}")
    public Optional<Vendor> getVendorById(@PathVariable Long vendorId) {
        return vendorRepository.findById(vendorId);
    }

    @PostMapping
    public Vendor createVendor(@RequestBody Vendor vendor) {
        return vendorRepository.save(vendor);
    }

}
