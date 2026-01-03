package com.ecommerce.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.backend.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByNameContainingIgnoreCaseAndPriceLessThanEqual(String name, Double price, Pageable pageable);

    boolean existsByName(String name);

    Optional<Product> findByName(String name);
}
