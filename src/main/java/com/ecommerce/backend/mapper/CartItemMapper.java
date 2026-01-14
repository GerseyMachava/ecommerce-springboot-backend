package com.ecommerce.backend.mapper;

import org.springframework.stereotype.Component;

import com.ecommerce.backend.dto.ResponseDto.CartItemResponseDto;
import com.ecommerce.backend.model.Cart;
import com.ecommerce.backend.model.CartItem;
import com.ecommerce.backend.model.Product;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CartItemMapper {

    public CartItem toEntity(int quantity, Cart cart, Product product) {
        return CartItem.builder()
                .quantity(quantity)
                .cart(cart)
                .product(product)
                .build();
    }

    public CartItemResponseDto toResponseDto(CartItem cartItem, int quantity) {
        CartItemResponseDto responseDto = new CartItemResponseDto(
                cartItem.getId(),
                cartItem.getProduct().getName(),
                cartItem.getProduct().getPrice(),
                quantity);
        return responseDto;

    }

    public CartItem toUpdate(CartItem existingCartItem, Cart cart, Product product, int quantity) {
        existingCartItem.setQuantity(quantity);
        existingCartItem.setCart(cart);
        existingCartItem.setProduct(product);
        return existingCartItem;
    }
}
