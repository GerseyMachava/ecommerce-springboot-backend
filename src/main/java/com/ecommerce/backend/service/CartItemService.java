package com.ecommerce.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ecommerce.backend.dto.ResponseDto.CartItemResponseDto;
import com.ecommerce.backend.dto.requestDto.CartItemRequestDto;
import com.ecommerce.backend.mapper.CartItemMapper;
import com.ecommerce.backend.model.Cart;
import com.ecommerce.backend.model.CartItem;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.CartItemRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.security.SecurityService;
import com.ecommerce.backend.shared.exception.BusinessException;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CartItemService {

    private final CartItemRepository repository;
    private final CartItemMapper mapper;
    private final CartService cartService;
    private final ProductService productService;
    private final SecurityService securityService;
    private final UserRepository userRepository;

    public CartItemResponseDto addProductToCart(CartItemRequestDto requestDto) {
        User user = securityService.getAuthenticatedUser()
                .orElseThrow(() -> new BusinessException("User not found ", HttpStatus.NOT_FOUND));
        // Recarrega o User da sessão Hibernate para evitar detached entity exception
        user = userRepository.findById(user.getId())
                .orElseThrow(() -> new BusinessException("User not found ", HttpStatus.NOT_FOUND));
        Cart userCart = cartService.findOrCreateCart(user);
        Product product = productService.getProduct(requestDto.productId());
        Optional<CartItem> existingItemOpt = repository.findByProductAndCart(product, userCart);
        int quantityInCart = existingItemOpt.map(CartItem::getQuantity).orElse(0);
        int newTotalQuantity = quantityInCart + requestDto.quantity();

        if (product.getStockQuantity() < newTotalQuantity) {
            throw new BusinessException("Requested quantity exceeds available stock", HttpStatus.CONFLICT);
        }
        CartItem cartItem;
        if (existingItemOpt.isPresent()) {
            cartItem = existingItemOpt.get();
            cartItem.setQuantity(newTotalQuantity);

        } else {

            cartItem = mapper.toEntity(requestDto.quantity(), userCart, product);
        }

        repository.save(cartItem);
        return mapper.toResponseDto(cartItem, newTotalQuantity);

    }

    public List<CartItemResponseDto> getUserCartItems(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new BusinessException("No user found with the id:  " + userId, HttpStatus.NOT_FOUND));
        List<CartItem> cartItems = repository.findByCartUser(user);
        return cartItems.stream().map(
                cartItem -> mapper.toResponseDto(cartItem, cartItem.getQuantity())).toList();

    }

    public List<CartItemResponseDto> getAuthCartItems() {
        User user = securityService.getAuthenticatedUser()
                .orElseThrow(() -> new BusinessException("User not found ", HttpStatus.NOT_FOUND));
        List<CartItem> cartItems = repository.findByCartUser(user);
        return cartItems.stream().map(
                cartItem -> mapper.toResponseDto(cartItem, cartItem.getQuantity())).toList();
    }

    public void deleteCartItem(Long cartItemId) {
        repository.findById(cartItemId).ifPresentOrElse(
                repository::delete, () -> {
                    throw new BusinessException("No cart item found with the id " + cartItemId, HttpStatus.NOT_FOUND);
                });

    }

    @Transactional
    public void cleanUserCartItems(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new BusinessException("No user found with the id:  " + userId, HttpStatus.NOT_FOUND));
        List<CartItem> cartItems = repository.findByCartUser(user);
        repository.deleteAll(cartItems);
    }

    public void cleanAuthUserCartitems() {
        User user = securityService.getAuthenticatedUser()
                .orElseThrow(() -> new BusinessException("User not found ", HttpStatus.NOT_FOUND));
        List<CartItem> cartItems = repository.findByCartUser(user);
        repository.deleteAll(cartItems);
    }

}
