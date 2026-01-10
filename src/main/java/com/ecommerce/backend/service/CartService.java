package com.ecommerce.backend.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ecommerce.backend.model.Cart;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.shared.exception.BusinessException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    /*
     * public void existingCart(User user) {
     * if (cartRepository.existsByUser(user) == false) {
     * createUserCart(user);
     * }
     * 
     * }
     * 
     * public Cart createUserCart(User user) {
     * Cart newCart = Cart.builder()
     * .user(user)
     * .build();
     * return cartRepository.save(newCart);
     * }
     * 
     * public Cart findUserCart(User user) {
     * if (cartRepository.existsByUser(user) == false) {
     * return createUserCart(user);
     * }
     * return cartRepository.findByUser(user).orElseThrow(
     * () -> new BusinessException("No cart found for the user " + user.getId(),
     * HttpStatus.NOT_FOUND));
     * }
     */

    public Cart findOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> createUserCart(user));
    }

    public Cart createUserCart(User user) {
        try {
            Cart newCart = Cart.builder()
                    .user(user)
                    .build();
            return cartRepository.save(newCart);
        } catch (DataIntegrityViolationException e) {
            return cartRepository.findByUser(user)
                    .orElseThrow(() -> new BusinessException("Erro ao recuperar carrinho",
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

}
