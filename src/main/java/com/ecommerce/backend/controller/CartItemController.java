package com.ecommerce.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.backend.dto.ResponseDto.CartItemResponseDto;
import com.ecommerce.backend.dto.requestDto.CartItemRequestDto;
import com.ecommerce.backend.service.CartItemService;
import com.ecommerce.backend.shared.apiResponse.ApiResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/cart")
@AllArgsConstructor
public class CartItemController {

    private CartItemService service;

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_CUSTOMER' )")
    @PostMapping()
    public ResponseEntity<ApiResponse<CartItemResponseDto>> addItemtoCart(
            @RequestBody @Valid CartItemRequestDto request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product added to the cart!", service.addProductToCart(request),
                        HttpStatus.CREATED));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_CUSTOMER' )")
    @GetMapping()
    public ResponseEntity<ApiResponse<List<CartItemResponseDto>>> getAuthCartItems() {
        List<CartItemResponseDto> data = service.getAuthCartItems();
        String message = data.isEmpty() ? "The cart is empty" : "Products fetched!";
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(message, data, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getUserCart/{userId}")
    public ResponseEntity<ApiResponse<List<CartItemResponseDto>>> getUserCartItems(
            @PathVariable(name = "userId") Long userId) {
        List<CartItemResponseDto> data = service.getUserCartItems(userId);
        String message = data.isEmpty() ? "The cart is empty" : "Products fetched!";
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(message, data, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/cleanUserCart/{userId}")
    public ResponseEntity<ApiResponse<CartItemResponseDto>> cleanUserCartItems(
            @PathVariable(name = "userId") Long userId) {
        service.cleanUserCartItems(userId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("Cart user cleaned", null, HttpStatus.OK));

    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_CUSTOMER' )")
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<CartItemResponseDto>> deleteCartitem(
            @PathVariable(name = "cartItemId") Long cartItemId) {
        service.deleteCartItem(cartItemId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("Cart Item Deleted", null, HttpStatus.OK));

    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_CUSTOMER' )")
    @DeleteMapping
    public ResponseEntity<ApiResponse<CartItemResponseDto>> cleanCartItem() {
        service.cleanAuthUserCartItems();
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("Cart cleaned", null, HttpStatus.OK));

    }

}
