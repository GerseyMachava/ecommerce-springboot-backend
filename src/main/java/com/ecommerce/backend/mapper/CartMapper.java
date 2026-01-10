package com.ecommerce.backend.mapper;

import org.springframework.stereotype.Component;

import com.ecommerce.backend.model.Cart;
import com.ecommerce.backend.model.User;

@Component
public class CartMapper {

    public Cart toEntity(User user){
        return Cart.builder()
        .user(user)
        .build();
    }
}
