package com.ecommerce.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ecommerce.backend.model.Cart;
import com.ecommerce.backend.model.CartItem;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.User;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    boolean existsByProductAndCart(Product product, Cart cart);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.user = :user")
    List<CartItem> findByCartUser(User user);

    Optional<CartItem> findByProductAndCart(Product product, Cart cart);
}
