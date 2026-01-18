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
                    .orElseThrow(() -> new BusinessException("Error while founding the user cart",
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

}
