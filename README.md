# Scalable Inventory Tracking System for Bazaar Technologies

## Overview

This project implements a scalable Inventory Tracking System for Bazaar Technologies, designed to evolve from a single-store solution to a multi-store distributed platform. Built with Java Spring Boot, the system tracks product inventory, stock movements (stock-in, sales, removals), and supports audit capabilities while ensuring high performance, reliability, and security. The solution adheres to the three-stage scalability outlined in the case study:

* **Stage 1**: Single-store inventory tracking.
* **Stage 2**: Multi-store support (500+ stores) with MySQL, REST APIs, and basic security.
* **Stage 3**: Horizontal scalability (1000s of stores) with asynchronous processing, caching, and audit logs.

## Entities

The following entities were designed using Object-Oriented Programming (OOP) principles to ensure a strong foundation for scalability:

| Entity        | Fields                        | PK/FK                        | Relationships                                   |
|---------------|-------------------------------|------------------------------|-------------------------------------------------|
| **Product** | productId, name, category, price, description    | productId (PK)               | One-to-Many: Stock, StockMovement               |
| **Store** | storeId, name, location       | storeId (PK)                 | One-to-Many: Stock, StockMovement               |
| **Stock** | storeId, productId, quantity  | Composite PK (storeId, productId) | Many-to-One: Store, Product                     |
| **StockMovement** | movementId, storeId, productId, quantityChange, type, movementType, timestamp, manager, vendor | movementId (PK)              | Many-to-One: Store, Product, Manager, Vendor    |
| **Manager** | managerId, name, passwordHash | managerId (PK)               | One-to-Many: StockMovement                      |
| **Vendor** | vendorId, name                | vendorId (PK)                | One-to-Many: StockMovement                      |

## Operations/Functions (API Endpoints)

The system supports the following operations, accessible via REST APIs:

### 1. Manage Stores

* **Create a Store**: Add a new store (name, location).
* **List All Stores**: View all registered stores.
* **View Store Details**: Get information for a specific store.
* **Update Store**: Modify store name or location.
* **Delete Store**: Remove a store (and its stock data).

### 2. Manage Products

* **Add Product**: Create a new product (name, category, price).
* **List All Products**: View all products in a store.
* **Search Products**: Filter by name, category, or both.
* **Update Product**: Edit product details (e.g., price, description).
* **Delete Product**: Remove a product from the system.

### 3. Handle Stock

* **Add Stock (Stock-In)**: Increase stock quantity for a product (requires manager/vendor approval).
* **Sell Product**: Reduce stock when a product is sold.
* **Remove Stock Manually**: Decrease stock (e.g., damaged items).
* **Check Current Stock**: View real-time stock levels for a product or store.
* **Filter Stock**: Search stock by product name or category within a store.

### 4. Track Stock Movements

* **View All Movements**: See every stock change (stock-in, sales, removals).
* **Filter Movements**: Search by date range, product, or store.
* **Delete Movement**: Remove a stock movement record (e.g., corrections).

### 5. Manage Users

* **Create Manager/Vendor**: Add new managers or vendors.
* **List Managers/Vendors**: View all registered managers or vendors.
* **View User Details**: Get information for a specific manager or vendor.

### 6. Security & Monitoring

* **Basic Authentication**: Log in with username/password to access APIs.
* **Rate Limiting**: Prevent abuse (e.g., 100 requests/minute).
* **Audit Logs**: Track who made stock changes (manager/vendor).

*All operations are functional, and their respective APIs were tested using the Postman application.*

## Development Environment

* Language: Java
* Framework: Spring Boot

    * Reasoning: Comfort with Java, experience with small-scale Java projects, popularity for enterprise application development, and development accessibility and feasibility.

## Project Structure

The application follows a layered architecture:

* model/:  Defines data structures (entities)
* repository/: Handles database operations
* service/: Contains business logic
* controller/: REST APIs

*In code, I have combined the service layer into the controller layer for simplicity*
### Architecture

The system follows a layered architecture to ensure clear separation of concerns, maintainability, and scalability. The key layers are:

1.  **Model Layer**

    * Purpose: Defines the core data structures and relationships.
    * Implementation:
        * Uses JPA (Jakarta Persistence API) entities.
        * Annotated with `@Entity` for object-relational mapping.
        * Includes validation constraints (e.g., `@NotBlank`, `@Size`).
2.  **Repository Layer**

    * Purpose: Handles all database operations.
    * Implementation:
        * Spring Data JPA interfaces like `ProductRepository` and `StockRepository`.
        * Extends `JpaRepository` for standard CRUD operations.
        * Includes custom query methods (e.g., `findByNameContainingIgnoreCase`).
        * Supports complex joins and filtering through `@Query` annotations.
3.  **Service Layer**

    * Purpose: Contains business logic and coordinates transactions.
    * Implementation:
        * Handles complex operations like stock updates and audit logging.
        * Ensures atomic transactions with `@Transactional`.
        * Validates business rules (e.g., preventing negative stock quantities).
4.  **Controller Layer**

    * Purpose: Exposes REST APIs and handles HTTP interactions.
    * Implementation:
        * Uses `@RestController` for endpoint definitions.
        * Implements input validation with `@Valid`.
        * Provides standardized error handling via `@ControllerAdvice`.
        * Supports filtering for large datasets.

### Abstraction and Modularity

* **Single-Store Foundation**
    * The core inventory logic is designed around a single-store model.
    * Key Points:
        * `Stock` uses a composite key (`storeId` + `productId`) to track inventory per store.
        * `StockMovement` records all inventory changes with timestamps and actor references.
    * This design allows seamless scaling to multiple stores by:
        * Maintaining store-specific data through composite keys.
        * Avoiding redundant tables or complex joins.
* **Central Product Catalog**
    * Products are managed independently from inventory.
    * Benefits:
        * Single source of truth for product information.
        * Products can exist in multiple stores without duplication.
        * Simplifies product management and updates.

## Stage-wise Implementation Details

### Stage 1

* Database: H2 in-memory database (for initial development and testing).  The connections built in stage 1 can be reused in the next stage (where I used MySQL RDB) without any changes in the Model, Repository or Controller layers.
* API Format: REST
* Key Implementation Details:
    * `StockId` class for composite primary key in the `Stock` entity.
    * Exception handling classes for customized error management.

### Stage 2

* Database: MySQL Relational Database.
* Security:
    * Basic authentication using Spring Boot Security.
    * Request throttling using the Bucket4j library.
* Enhancements:
    * More complex filtering for stock, allowing searches based on product name, category, or store.

### Stage 3

* Caching:
    * Implemented using the Caffeine library (instead of Redis).
    * Spring annotations (`@Cacheable`, `@CacheEvict`) for easy integration.
* Audit Logging:
    * Functionality added to the `Vendor` and `Manager` entities to track stock additions, sales, and removals.
    * StockMovement objects are created whenever a Stock is added, sold or removed.
    * Enables a global view of order responsibility in a multi-store platform.
* Horizontal Scalability:
     * The idea was to separate the MySQL database into a separate Docker container / or server, and then creating a dockerized version of this application. This allows vertical scaling as multiple containers of this application can connect to the same database. Or, if need be (for example based on location) we can have separate databases connected to different containers.
     * Due to a time crunch, this was not implemented.

## Additional Notes

* **Integration Testing:** An attempt was made to implement a separate Testing Application in stage 3, but it was not completed due to unresolved errors.
* **Frontend:** The application was tested using Postman, and a frontend was not designed or connected.
