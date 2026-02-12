package com.ecommerce.backend.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

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
import com.ecommerce.backend.service.CartService;
import com.ecommerce.backend.service.ProductService;
import com.ecommerce.backend.service.CartItemService;
import com.ecommerce.backend.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

        @Mock
        private CartItemRepository cartItemRepository;

        @Mock
        private CartItemMapper cartItemMapper;

        @Mock
        private CartService cartService;

        @Mock
        private ProductService productService;

        @Mock
        private SecurityService securityService;

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private CartItemService cartItemService;

        private User user;
        private Cart cart;
        private Product product;
        private CartItem cartItem;
        private CartItemRequestDto requestDto;
        private CartItemResponseDto responseDto;

        @BeforeEach
        void setUp() {
                user = User.builder()

                                .email("test@email.com")
                                .build();

                user.setId(1L);
                cart = Cart.builder()

                                .user(user)
                                .build();
                cart.setId(1L);
                product = Product.builder()

                                .name("Smartphone")
                                .price(new BigDecimal("2000.00"))
                                .stockQuantity(10)
                                .build();
                product.setId(1L);
                cartItem = CartItem.builder()

                                .product(product)
                                .cart(cart)
                                .quantity(2)
                                .build();
                cartItem.setId(1L);
                requestDto = new CartItemRequestDto(
                                1L,
                                2);

                responseDto = new CartItemResponseDto(
                                1L,
                                "Smartphone",
                                new BigDecimal("2000.00"),
                                2);
        }

        @Nested
        @DisplayName("addProductToCart - Adicionar produto ao carrinho")
        class AddProductToCartTests {

                @Test
                @DisplayName("✅ Deve adicionar novo produto ao carrinho")
                void addProductToCart_WithNewProduct_ShouldAddToCart() {
                        // Arrange
                        when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
                        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
                        when(cartService.findOrCreateCart(user)).thenReturn(cart);
                        when(productService.getProduct(1L)).thenReturn(product);
                        when(cartItemRepository.findByProductAndCart(product, cart)).thenReturn(Optional.empty());
                        when(cartItemMapper.toEntity(2, cart, product)).thenReturn(cartItem);
                        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
                        when(cartItemMapper.toResponseDto(cartItem, 2)).thenReturn(responseDto);

                        // Act
                        CartItemResponseDto result = cartItemService.addProductToCart(requestDto);

                        // Assert
                        assertNotNull(result);
                        assertEquals(1L, result.cartItemId());
                        assertEquals(2, result.quantity());

                        verify(cartItemRepository).save(cartItem);
                        verify(cartItemMapper).toEntity(2, cart, product);
                }

                @Test
                @DisplayName("✅ Deve atualizar quantidade quando produto já está no carrinho")
                void addProductToCart_WithExistingProduct_ShouldUpdateQuantity() {
                        // Arrange
                        int quantidadeExistente = 2;
                        int quantidadeNova = 3;
                        int quantidadeTotal = 5;

                        CartItem existingItem = CartItem.builder()
                                        .product(product)
                                        .cart(cart)
                                        .quantity(quantidadeExistente)
                                        .build();
                        existingItem.setId(1L);
                        CartItemRequestDto updateDto = new CartItemRequestDto(
                                        1L,
                                        quantidadeNova);

                        CartItemResponseDto updatedResponse = new CartItemResponseDto(
                                        1L,
                                        "Smartphone",
                                        new BigDecimal("2000.00"),
                                        quantidadeTotal);

                        when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
                        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
                        when(cartService.findOrCreateCart(user)).thenReturn(cart);
                        when(productService.getProduct(1L)).thenReturn(product);
                        when(cartItemRepository.findByProductAndCart(product, cart))
                                        .thenReturn(Optional.of(existingItem));
                        when(cartItemRepository.save(existingItem)).thenReturn(existingItem);
                        when(cartItemMapper.toResponseDto(existingItem, quantidadeTotal))
                                        .thenReturn(updatedResponse);

                        // Act
                        CartItemResponseDto result = cartItemService.addProductToCart(updateDto);

                        // Assert
                        assertNotNull(result);
                        assertEquals(quantidadeTotal, result.quantity());
                        assertEquals(quantidadeTotal, existingItem.getQuantity());

                        verify(cartItemRepository).save(existingItem);
                        verify(cartItemMapper, never()).toEntity(anyInt(), any(), any());
                }

                @Test
                @DisplayName("💰 Deve rejeitar quando quantidade ultrapassa estoque")
                void addProductToCart_WithQuantityExceedingStock_ShouldThrowException() {
                        // Arrange
                        product.setStockQuantity(5); // Estoque = 5

                        CartItemRequestDto excessDto = new CartItemRequestDto(
                                        1L,
                                        6);

                        when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
                        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
                        when(cartService.findOrCreateCart(user)).thenReturn(cart);
                        when(productService.getProduct(1L)).thenReturn(product);
                        when(cartItemRepository.findByProductAndCart(product, cart))
                                        .thenReturn(Optional.empty());

                        // Act & Assert
                        BusinessException exception = assertThrows(BusinessException.class,
                                        () -> cartItemService.addProductToCart(excessDto));

                        assertEquals("Requested quantity exceeds available stock", exception.getMessage());
                        assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());

                        verify(cartItemRepository, never()).save(any());
                }

                @Test
                @DisplayName("💰 Deve rejeitar quando quantidade TOTAL ultrapassa estoque")
                void addProductToCart_WithTotalQuantityExceedingStock_ShouldThrowException() {
                        // Arrange
                        product.setStockQuantity(5); // Estoque = 5

                        CartItem existingItem = CartItem.builder()
                                        .product(product)
                                        .cart(cart)
                                        .quantity(3) // Já tem 3 no carrinho
                                        .build();
                        existingItem.setId(1L);

                        CartItemRequestDto additionalDto = new CartItemRequestDto(
                                        1L, 3);

                        when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
                        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
                        when(cartService.findOrCreateCart(user)).thenReturn(cart);
                        when(productService.getProduct(1L)).thenReturn(product);
                        when(cartItemRepository.findByProductAndCart(product, cart))
                                        .thenReturn(Optional.of(existingItem));

                        // Act & Assert
                        BusinessException exception = assertThrows(BusinessException.class,
                                        () -> cartItemService.addProductToCart(additionalDto));

                        assertEquals("Requested quantity exceeds available stock", exception.getMessage());

                        verify(cartItemRepository, never()).save(any());
                }

                @Test
                @DisplayName("🔒 Deve rejeitar quando usuário não está autenticado")
                void addProductToCart_WithUnauthenticatedUser_ShouldThrowException() {
                        // Arrange
                        when(securityService.getAuthenticatedUser()).thenReturn(Optional.empty());

                        // Act & Assert
                        BusinessException exception = assertThrows(BusinessException.class,
                                        () -> cartItemService.addProductToCart(requestDto));

                        assertEquals("User not found ", exception.getMessage());
                        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

                        verify(userRepository, never()).findById(any());
                        verify(cartService, never()).findOrCreateCart(any());
                }
        }

        @Nested
        @DisplayName("getCartItems - Buscar itens do carrinho")
        class GetCartItemsTests {

                @Test
                @DisplayName("✅ Deve retornar itens do usuário autenticado")
                void getAuthCartItems_WithAuthenticatedUser_ShouldReturnItems() {
                        // Arrange
                        List<CartItem> cartItems = List.of(cartItem);
                        List<CartItemResponseDto> responseDtos = List.of(responseDto);

                        when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
                        when(cartItemRepository.findByCartUser(user)).thenReturn(cartItems);
                        when(cartItemMapper.toResponseDto(cartItem, 2)).thenReturn(responseDto);

                        // Act
                        List<CartItemResponseDto> result = cartItemService.getAuthCartItems();

                        // Assert
                        assertNotNull(result);
                        assertEquals(1, result.size());

                        verify(cartItemRepository).findByCartUser(user);
                }

                @Test
                @DisplayName("✅ Deve retornar itens por ID de usuário (admin)")
                void getUserCartItems_WithValidUserId_ShouldReturnItems() {
                        // Arrange
                        List<CartItem> cartItems = List.of(cartItem);
                        List<CartItemResponseDto> responseDtos = List.of(responseDto);

                        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
                        when(cartItemRepository.findByCartUser(user)).thenReturn(cartItems);
                        when(cartItemMapper.toResponseDto(cartItem, 2)).thenReturn(responseDto);

                        // Act
                        List<CartItemResponseDto> result = cartItemService.getUserCartItems(1L);

                        // Assert
                        assertNotNull(result);
                        assertEquals(1, result.size());

                        verify(userRepository).findById(1L);
                        verify(cartItemRepository).findByCartUser(user);
                }

                @Test
                @DisplayName("❌ Deve lançar exceção quando usuário não existe")
                void getUserCartItems_WithNonExistingUser_ShouldThrowException() {
                        // Arrange
                        when(userRepository.findById(999L)).thenReturn(Optional.empty());

                        // Act & Assert
                        BusinessException exception = assertThrows(BusinessException.class,
                                        () -> cartItemService.getUserCartItems(999L));

                        assertEquals("No user found with the id:  999", exception.getMessage());
                        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
                }
        }

        @Nested
        @DisplayName("deleteCartItem - Remover item do carrinho")
        class DeleteCartItemTests {

                @Test
                @DisplayName("✅ Deve remover item existente")
                void deleteCartItem_WithExistingId_ShouldDelete() {
                        // Arrange
                        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

                        // Act
                        cartItemService.deleteCartItem(1L);

                        // Assert
                        verify(cartItemRepository).delete(cartItem);
                }

                @Test
                @DisplayName("❌ Deve lançar exceção quando item não existe")
                void deleteCartItem_WithNonExistingId_ShouldThrowException() {
                        // Arrange
                        when(cartItemRepository.findById(999L)).thenReturn(Optional.empty());

                        // Act & Assert
                        BusinessException exception = assertThrows(BusinessException.class,
                                        () -> cartItemService.deleteCartItem(999L));

                        assertEquals("No cart item found with the id 999", exception.getMessage());
                        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

                        verify(cartItemRepository, never()).delete(any());
                }
        }

        @Nested
        @DisplayName("cleanCart - Limpar carrinho")
        class CleanCartTests {

                @Test
                @DisplayName("✅ Deve limpar carrinho do usuário autenticado")
                void cleanAuthUserCartitems_ShouldDeleteAllItems() {
                        // Arrange
                        List<CartItem> cartItems = List.of(cartItem);
                        when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
                        when(cartItemRepository.findByCartUser(user)).thenReturn(cartItems);

                        // Act
                        cartItemService.cleanAuthUserCartItems();

                        // Assert
                        verify(cartItemRepository).deleteAll(cartItems);
                }

                @Test
                @DisplayName("✅ Deve limpar carrinho por ID de usuário (admin)")
                void cleanUserCartItems_WithValidUserId_ShouldDeleteAllItems() {
                        // Arrange
                        List<CartItem> cartItems = List.of(cartItem);
                        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
                        when(cartItemRepository.findByCartUser(user)).thenReturn(cartItems);

                        // Act
                        cartItemService.cleanUserCartItems(1L);

                        // Assert
                        verify(cartItemRepository).deleteAll(cartItems);
                }
        }
}