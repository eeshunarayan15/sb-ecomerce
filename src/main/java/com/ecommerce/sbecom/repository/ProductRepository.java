package com.ecommerce.sbecom.repository;

import com.ecommerce.sbecom.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("SELECT p from Product p where p.category.id= :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") UUID categoryId);


    List<Product> findByProductNameLikeIgnoreCase(String keyword);

}