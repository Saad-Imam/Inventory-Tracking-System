package com.bazaar.inventory_system.model;
import jakarta.persistence.*;
/* Jakarta Persistence API (JPA) is a standard specification
for managing relational data in Java applications
* */
import java.math.BigDecimal; //for precision

@Entity //JPA entity, meaning instance of class represents row in a database
public class Product {
    @Id //mark as primary key for table
    @GeneratedValue(strategy = GenerationType.IDENTITY) //increment automatically
    private Long productId;

    private String name;
    private String category;
    private BigDecimal price;
    private String description;

    // Constructors, Getters, and Setters
}
