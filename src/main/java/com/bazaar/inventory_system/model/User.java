package com.bazaar.inventory_system.model;

import jakarta.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String passwordHash;

    // Constructors, Getters, and Setters

}
