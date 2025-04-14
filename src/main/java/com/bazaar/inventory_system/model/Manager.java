package com.bazaar.inventory_system.model;

import jakarta.persistence.*;

@Entity
public class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long managerId;

    private String name;
    private String passwordHash;

    // Constructors, Getters, and Setters

    public Manager() {
    }

    public Manager(Long managerId, String name, String passwordHash) {
        this.managerId = managerId;
        this.name = name;
        this.passwordHash = passwordHash;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getUsername() {
        return name;
    }

    public void setUsername(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
