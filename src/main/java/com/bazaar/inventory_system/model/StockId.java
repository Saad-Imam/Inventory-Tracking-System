//Needed for the composite primary key:
/*
When using JPA (Java Persistence API) and you have an entity with a composite primary key
JPA requires you to define this key in a specific way. One common approach is to create
 a separate embeddable class (like StockId) that holds the fields of the composite key and
 then reference this class in your entity.
 */
/*
The Serializable interface is a marker interface (it doesn't have any methods) that indicates
that an object of this class can be converted into a stream of bytes and later reconstructed
(deserialized) back into an object. This is often necessary for JPA, especially when dealing
with caching or when the entity's state needs to be persisted across different parts of the
application's lifecycle. Composite primary key classes in JPA entities are typically required to implement Serializable
 */
package com.bazaar.inventory_system.model;
import java.io.Serializable;

public class StockId implements Serializable {
    private Long storeId;
    private Long productId;

    // Constructors, Getters, and Setters (must include hashCode and equals)

    public StockId() {}


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
}
