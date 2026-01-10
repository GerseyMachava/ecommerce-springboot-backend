package com.ecommerce.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.backend.model.Cart;
import com.ecommerce.backend.model.User;

public interface CartRepository extends JpaRepository<Cart,Long> {

    boolean existsByUser(User user);

    Optional<Cart> findByUser(User user);

}
