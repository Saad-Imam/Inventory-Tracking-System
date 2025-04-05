package com.bazaar.inventory_system.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    @NotBlank(message = "Store name is mandatory")
    @Size(max = 100, message = "Name must be ≤100 characters")
    private String name;

    @Size(max = 200, message = "Location must be ≤200 characters")
    private String location;

    // Constructors, Getters, and Setters
    public Store() {
    }

    public Store(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Store{" +
                "storeId=" + storeId +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

}