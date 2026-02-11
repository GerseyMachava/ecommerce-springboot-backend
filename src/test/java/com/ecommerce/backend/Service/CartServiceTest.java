package com.ecommerce.backend.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import com.ecommerce.backend.model.Cart;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.model.enums.UserRole;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.service.CartService;
import com.ecommerce.backend.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Cart cart;

    @BeforeEach
    void setUp() {
        // Setup User
        user = User.builder()

                .email("test@example.com")
                .role(UserRole.CUSTOMER)
                .build();
        user.setId(1L);
        // Setup Cart
        cart = Cart.builder()

                .user(user)
                .build();
        cart.setId(1L);
    }

    @Nested
    @DisplayName("Testes para findOrCreateCart")
    class FindOrCreateCartTests {

        @Test
        @DisplayName("Deve retornar carrinho existente quando usuário já tem carrinho")
        void findOrCreateCart_WhenCartExists_ShouldReturnExistingCart() {
            // Arrange
            when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

            // Act
            Cart result = cartService.findOrCreateCart(user);

            // Assert
            assertNotNull(result);
            assertEquals(cart.getId(), result.getId());
            assertEquals(user.getId(), result.getUser().getId());

            verify(cartRepository).findByUser(user);
            verify(cartRepository, never()).save(any(Cart.class));
        }

        @Test
        @DisplayName("Deve criar novo carrinho quando usuário não tem carrinho")
        void findOrCreateCart_WhenCartDoesNotExist_ShouldCreateNewCart() {
            // Arrange
            when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            // Act
            Cart result = cartService.findOrCreateCart(user);

            // Assert
            assertNotNull(result);
            assertEquals(cart.getId(), result.getId());
            assertEquals(user.getId(), result.getUser().getId());

            verify(cartRepository).findByUser(user);
            verify(cartRepository).save(any(Cart.class));
        }

        @Test
        @DisplayName("Deve retornar carrinho quando criação lança DataIntegrityViolationException e carrinho existe")
        void findOrCreateCart_WhenCreateThrowsDataIntegrityViolationAndCartExists_ShouldReturnExistingCart() {
            // Arrange
            when(cartRepository.findByUser(user))
                    .thenReturn(Optional.empty()) // Primeira chamada: não encontrou
                    .thenReturn(Optional.of(cart)); // Segunda chamada: encontrou

            when(cartRepository.save(any(Cart.class)))
                    .thenThrow(DataIntegrityViolationException.class);

            // Act
            Cart result = cartService.findOrCreateCart(user);

            // Assert
            assertNotNull(result);
            assertEquals(cart.getId(), result.getId());

            verify(cartRepository, times(2)).findByUser(user);
            verify(cartRepository).save(any(Cart.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando criação falha e carrinho não é encontrado")
        void findOrCreateCart_WhenCreateThrowsDataIntegrityViolationAndCartNotFound_ShouldThrowBusinessException() {
            // Arrange
            when(cartRepository.findByUser(user))
                    .thenReturn(Optional.empty()) // Primeira chamada
                    .thenReturn(Optional.empty()); // Segunda chamada também não encontra

            when(cartRepository.save(any(Cart.class)))
                    .thenThrow(DataIntegrityViolationException.class);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> cartService.findOrCreateCart(user));

            assertEquals("Error while founding the user cart", exception.getMessage());
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());

            verify(cartRepository, times(2)).findByUser(user);
            verify(cartRepository).save(any(Cart.class));
        }
    }

    @Nested
    @DisplayName("Testes para createUserCart")
    class CreateUserCartTests {

        @Test
        @DisplayName("Deve criar carrinho com sucesso")
        void createUserCart_WithValidUser_ShouldCreateCart() {
            // Arrange
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            // Act
            Cart result = cartService.createUserCart(user);

            // Assert
            assertNotNull(result);
            assertEquals(cart.getId(), result.getId());
            assertEquals(user, result.getUser());

            verify(cartRepository).save(any(Cart.class));
            verify(cartRepository, never()).findByUser(any(User.class));
        }

        @Test
        @DisplayName("Deve retornar carrinho existente quando save lança DataIntegrityViolationException e carrinho existe")
        void createUserCart_WhenSaveThrowsDataIntegrityViolationAndCartExists_ShouldReturnExistingCart() {
            // Arrange
            when(cartRepository.save(any(Cart.class)))
                    .thenThrow(DataIntegrityViolationException.class);
            when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

            // Act
            Cart result = cartService.createUserCart(user);

            // Assert
            assertNotNull(result);
            assertEquals(cart.getId(), result.getId());

            verify(cartRepository).save(any(Cart.class));
            verify(cartRepository).findByUser(user);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando save lança DataIntegrityViolationException e carrinho não existe")
        void createUserCart_WhenSaveThrowsDataIntegrityViolationAndCartNotFound_ShouldThrowBusinessException() {
            // Arrange
            when(cartRepository.save(any(Cart.class)))
                    .thenThrow(DataIntegrityViolationException.class);
            when(cartRepository.findByUser(user)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> cartService.createUserCart(user));

            assertEquals("Error while founding the user cart", exception.getMessage());
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());

            verify(cartRepository).save(any(Cart.class));
            verify(cartRepository).findByUser(user);
        }

        @Test
        @DisplayName("Deve propagar outras exceções não tratadas")
        void createUserCart_WhenSaveThrowsOtherException_ShouldPropagateException() {
            // Arrange
            when(cartRepository.save(any(Cart.class)))
                    .thenThrow(new RuntimeException("Database connection error"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> cartService.createUserCart(user));

            assertEquals("Database connection error", exception.getMessage());

            verify(cartRepository).save(any(Cart.class));
            verify(cartRepository, never()).findByUser(any(User.class));
        }

        @Test
        @DisplayName("Deve criar carrinho com usuário correto")
        void createUserCart_ShouldSetUserCorrectly() {
            // Arrange
            when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
                Cart cartToSave = invocation.getArgument(0);
                // Verificar se o usuário foi setado corretamente
                assertEquals(user, cartToSave.getUser());
                // Simular salvamento com ID
                cartToSave.setId(1L);
                return cartToSave;
            });

            // Act
            Cart result = cartService.createUserCart(user);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(user, result.getUser());

            verify(cartRepository).save(any(Cart.class));
        }

        @Test
        @DisplayName("Deve criar carrinho mesmo quando usuário é null?") // Teste de edge case
        void createUserCart_WithNullUser_ShouldCreateCart() {
            // Arrange
            Cart cartWithNullUser = Cart.builder()
                    .user(null)
                    .build();
            cartWithNullUser.setId(2L);
            when(cartRepository.save(any(Cart.class))).thenReturn(cartWithNullUser);

            // Act
            Cart result = cartService.createUserCart(null);

            // Assert
            assertNotNull(result);
            assertNull(result.getUser());

            verify(cartRepository).save(any(Cart.class));
        }
    }

    @Nested
    @DisplayName("Testes de integridade e concorrência")
    class ConcurrencyAndIntegrityTests {

        @Test
        @DisplayName("Deve lidar com DataIntegrityViolationException de forma elegante")
        void handleDataIntegrityViolation_ShouldRecoverGracefully() {
            // Simulando cenário de concorrência: dois usuários tentando criar carrinho
            // simultaneamente
            when(cartRepository.save(any(Cart.class)))
                    .thenThrow(DataIntegrityViolationException.class);
            when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

            // Act
            Cart result = cartService.createUserCart(user);

            // Assert
            assertNotNull(result);
            assertEquals(cart.getId(), result.getId());

            verify(cartRepository).save(any(Cart.class));
            verify(cartRepository).findByUser(user);
        }

        @Test
        @DisplayName("Deve lançar BusinessException específica quando não recupera após DataIntegrityViolation")
        void handleDataIntegrityViolation_WhenNoRecovery_ShouldThrowBusinessException() {
            // Arrange
            when(cartRepository.save(any(Cart.class)))
                    .thenThrow(DataIntegrityViolationException.class);
            when(cartRepository.findByUser(user)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> cartService.createUserCart(user));

            assertTrue(exception instanceof BusinessException);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
        }
    }
}