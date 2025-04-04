package com.bazaar.inventory_system.model;
import jakarta.persistence.*;

@Entity
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    private String name;
    private String location;

    // Constructors, Getters, and Setters
}