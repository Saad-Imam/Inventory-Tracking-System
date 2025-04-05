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

    public Product() {
    }

    public Product(Long productId, String name, String category, BigDecimal price, String description) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                '}';
    }
}
