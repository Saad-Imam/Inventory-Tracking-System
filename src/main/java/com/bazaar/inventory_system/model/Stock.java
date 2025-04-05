package com.bazaar.inventory_system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@IdClass(StockId.class)
public class Stock {
    @Id
    @Column(name = "store_id")
    private Long storeId;

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id", insertable = false, updatable = false)
    @JsonIgnore
    private Store store;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    @JsonIgnore
    private Product product;

    public Stock() {
    }

    public Stock(Long storeId, Long productId, Integer quantity, Store store, Product product) {
        this.storeId = storeId;
        this.productId = productId;
        this.quantity = quantity;
        this.store = store;
        this.product = product;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}

