package com.bazaar.inventory_system.model;

import jakarta.persistence.*;

@Entity
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vendorId;

    private String name;

    // Constructors, Getters, and Setters

    public Vendor(Long vendorId, String name) {
        this.vendorId = vendorId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
