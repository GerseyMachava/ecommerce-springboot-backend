package com.ecommerce.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.backend.model.ProductCategory;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    boolean existsByCategoryIdAndProductId(Long categoryId, Long productId);
    List<ProductCategory>findAllProductsByCategoryId(Long categoryId);
}
