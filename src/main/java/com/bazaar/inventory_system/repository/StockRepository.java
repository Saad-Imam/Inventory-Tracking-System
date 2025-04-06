package com.bazaar.inventory_system.repository;
import com.bazaar.inventory_system.model.Stock;
import com.bazaar.inventory_system.model.StockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, StockId> {
    List<Stock> findByStoreId(Long storeId);
    // Find stock by productId
    List<Stock> findByProductId(Long productId);

    @Query("SELECT s FROM Stock s JOIN Product p ON s.productId = p.productId WHERE p.category = :category")
    List<Stock> findByCategory(@Param("category") String category);

    // Find stock by productId and category (join with Product)
    @Query("SELECT s FROM Stock s JOIN Product p ON s.productId = p.productId WHERE p.category = :category AND s.productId = :productId")
    List<Stock> findByProductIdAndCategory(@Param("productId") Long productId, @Param("category") String category);

    // Find stock by product name (join with Product)
    @Query("SELECT s FROM Stock s JOIN Product p ON s.productId = p.productId WHERE p.name LIKE %:name%")
    List<Stock> findByName(@Param("name") String name);

    // Find stock by productId and name (join with Product)
    @Query("SELECT s FROM Stock s JOIN Product p ON s.productId = p.productId WHERE s.productId = :productId AND p.name LIKE %:name%")
    List<Stock> findByProductIdAndName(@Param("productId") Long productId, @Param("name") String name);

    // Find stock by category and name (join with Product)
    @Query("SELECT s FROM Stock s JOIN Product p ON s.productId = p.productId WHERE p.category = :category AND p.name LIKE %:name%")
    List<Stock> findByCategoryAndName(@Param("category") String category, @Param("name") String name);

    // Find stock by productId, category, and name (join with Product)
    @Query("SELECT s FROM Stock s JOIN Product p ON s.productId = p.productId WHERE p.category = :category AND s.productId = :productId AND p.name LIKE %:name%")
    List<Stock> findByProductIdAndCategoryAndName(@Param("productId") Long productId, @Param("category") String category, @Param("name") String name);
    // Find products in a specific store by name or category
    @Query("SELECT s FROM Stock s JOIN Product p ON s.productId = p.productId WHERE s.storeId = :storeId AND (p.name LIKE %:name% OR p.category = :category)")
    List<Stock> findByStoreIdAndNameOrCategory(@Param("storeId") Long storeId, @Param("name") String name, @Param("category") String category);

}
