package com.ecommerce.backend.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.ecommerce.backend.dto.ResponseDto.OrderItemResponseDto;
import com.ecommerce.backend.dto.ResponseDto.OrderResponseDto;
import com.ecommerce.backend.dto.requestDto.OrderStatusRequestDto;
import com.ecommerce.backend.mapper.OrderMapper;
import com.ecommerce.backend.model.CartItem;
import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.OrderItem;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.model.enums.OrderStatus;
import com.ecommerce.backend.model.enums.UserRole;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.security.SecurityService;
import com.ecommerce.backend.service.CartItemService;
import com.ecommerce.backend.service.OrderService;
import com.ecommerce.backend.service.ProductService;
import com.ecommerce.backend.shared.exception.BusinessException;

import jakarta.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

        @Mock
        private OrderRepository orderRepository;
        @Mock
        private OrderMapper orderMapper;
        @InjectMocks
        private OrderService orderService;

        @Mock
        private SecurityService securityService;

        @Mock
        private CartItemService cartItemService;

        @Mock
        private ProductService productService;

        private User user;
        private Order order;
        private OrderResponseDto responseDto;
        private OrderItemResponseDto orderItemResponseDto;
        private List<Order> orderItems = new ArrayList<>();
        private OrderResponseDto orderResponseDto;
        private List<CartItem> cartItems;

        @BeforeEach
        void setUp() {
                BigDecimal totalAmount = new BigDecimal("8000.00");

                // Setup User
                user = User.builder()
                          
                                .email("test@mail.com")
                                .password("12344321")
                                .role(UserRole.CUSTOMER)
                                .enabled(true)
                                .locked(false)
                                .build();
                        user.setId(1L);

                // Setup Product
                Product product = Product.builder()
                                .name("Smartphone")
                                .price(new BigDecimal("2000.00"))
                                .stockQuantity(50)
                                .build();
                product.setId(1L);

                // Setup OrderItem
                OrderItem orderItem = OrderItem.builder()
                                // .id(1L)
                                .quantity(4)
                                .unitPrice(new BigDecimal("2000.00"))
                                .build();
                        orderItem.setId(1L);
                CartItem cartItem = CartItem.builder()
                                .product(product)
                                .quantity(2)
                                .build();
                cartItems = List.of(cartItem);
                List<OrderItem> orderItems = new ArrayList<>();
                orderItems.add(orderItem);

                // Setup Order
                order = Order.builder()
                               
                                .totalAmount(totalAmount)
                                .status(OrderStatus.PENDING)
                                .user(user)
                                .orderItems(orderItems)
                                .build();
                order.setId(1L);
                // Setup OrderItemResponseDto
                orderItemResponseDto = new OrderItemResponseDto(
                                1L,
                                1L,
                                "Smartphone",
                                new BigDecimal("2000.00"),
                                6);

                List<OrderItemResponseDto> orderItemsDto = new ArrayList<>();
                orderItemsDto.add(orderItemResponseDto);

                // Setup OrderResponseDto
                responseDto = new OrderResponseDto(
                                1L,
                                OrderStatus.PENDING,
                                "test@mail.com",
                                orderItemsDto);
                orderResponseDto = new OrderResponseDto(
                                1L,
                                OrderStatus.PENDING,
                                "test@example.com",
                                List.of());

        }

        @Test
        void getAuthUserOrders_WithAuthenticatedUser_ShouldReturnOrders() {
                // Arrange
                List<Order> userOrders = List.of(order);
                when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
                when(orderRepository.findAllByUser(user)).thenReturn(userOrders);
                when(orderMapper.toResponseDto(any(Order.class))).thenReturn(responseDto);

                // Act
                List<OrderResponseDto> result = orderService.getAuthUserOrders();

                // Assert
                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals(1L, result.get(0).id());

                verify(securityService).getAuthenticatedUser();
                verify(orderRepository).findAllByUser(user);
                verify(orderMapper).toResponseDto(order);
        }

        @Test
        void getAuthUserOrders_WithUnauthenticatedUser_ShouldThrowBusinessException() {
                // Arrange
                when(securityService.getAuthenticatedUser()).thenReturn(Optional.empty());

                // Act & Assert
                BusinessException exception = assertThrows(BusinessException.class,
                                () -> orderService.getAuthUserOrders());

                assertEquals("user not found", exception.getMessage());
                assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

                verify(securityService).getAuthenticatedUser();
                verify(orderRepository, never()).findAllByUser(any());
        }

        @Test
        void getAuthUserOrders_WithNoOrders_ShouldReturnEmptyList() {
                // Arrange
                when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
                when(orderRepository.findAllByUser(user)).thenReturn(List.of());

                // Act
                List<OrderResponseDto> result = orderService.getAuthUserOrders();

                // Assert
                assertNotNull(result);
                assertTrue(result.isEmpty());

                verify(securityService).getAuthenticatedUser();
                verify(orderRepository).findAllByUser(user);
                verify(orderMapper, never()).toResponseDto(any());
        }

        @Test
        void toogleOrderStatus_WithExistingOrder_ShouldUpdateStatus() {
                // Arrange
                OrderStatusRequestDto statusRequestDto = new OrderStatusRequestDto(1L, OrderStatus.SHIPPED);

                Order updatedOrder = Order.builder()
                                .user(user)
                                .status(OrderStatus.SHIPPED)
                                .build();

                OrderResponseDto updatedResponse = new OrderResponseDto(
                                1L,
                                OrderStatus.SHIPPED,
                                "test@example.com",
                                List.of());

                when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
                when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
                when(orderMapper.toResponseDto(any(Order.class))).thenReturn(updatedResponse);

                // Act
                OrderResponseDto result = orderService.toogleOrderStatus(statusRequestDto);

                // Assert
                assertNotNull(result);
                assertEquals(OrderStatus.SHIPPED, result.status());

                verify(orderRepository).findById(1L);
                verify(orderRepository).save(order);
                verify(orderMapper).toResponseDto(order);
        }

        @Test
        void toogleOrderStatus_WithNonExistingOrder_ShouldThrowBusinessException() {
                // Arrange
                OrderStatusRequestDto statusRequestDto = new OrderStatusRequestDto(999L, OrderStatus.SHIPPED);
                when(orderRepository.findById(999L)).thenReturn(Optional.empty());

                // Act & Assert
                BusinessException exception = assertThrows(BusinessException.class,
                                () -> orderService.toogleOrderStatus(statusRequestDto));

                assertEquals("Order not found. Id: 999", exception.getMessage());
                assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

                verify(orderRepository).findById(999L);
                verify(orderRepository, never()).save(any());
        }

        @Test
        void findOrderByid_WithExistingOrder_ShouldReturnOrder() {
                // Arrange
                when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

                // Act
                Order result = orderService.findOrderByid(1L);

                // Assert
                assertNotNull(result);
                assertEquals(1L, result.getId());
                assertEquals(OrderStatus.PENDING, result.getStatus());

                verify(orderRepository).findById(1L);
        }

        @Test
        void findOrderByid_WithNonExistingOrder_ShouldThrowBusinessException() {
                // Arrange
                when(orderRepository.findById(999L)).thenReturn(Optional.empty());

                // Act & Assert
                BusinessException exception = assertThrows(BusinessException.class,
                                () -> orderService.findOrderByid(999L));

                assertEquals("No order found with the id: 999", exception.getMessage());
                assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

                verify(orderRepository).findById(999L);
        }

        @Test
        @Transactional
        void createOrderFromCart_WithValidCart_ShouldCreateOrder() {
                // Arrange
                when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
                when(cartItemService.getCartItemsEntity()).thenReturn(cartItems);
                when(orderRepository.save(any(Order.class))).thenReturn(order);
                when(orderMapper.toResponseDto(any(Order.class))).thenReturn(responseDto);

                // Mock do updateStockQuantity (método void)
                doNothing().when(productService).updateStockQuantity(anyLong(), anyInt());

                // Act
                OrderResponseDto result = orderService.createOrderFromCart();

                // Assert
                assertNotNull(result);
                assertEquals(1L, result.id());
                assertEquals(OrderStatus.PENDING, result.status());

                verify(securityService).getAuthenticatedUser();
                verify(cartItemService).getCartItemsEntity();
                verify(orderRepository).save(any(Order.class));
                verify(cartItemService).cleanAuthUserCartItems();
                verify(productService).updateStockQuantity(1L, 2);
        }

        @Test
        void createOrderFromCart_WithUnauthenticatedUser_ShouldThrowBusinessException() {
                // Arrange
                when(securityService.getAuthenticatedUser()).thenReturn(Optional.empty());

                // Act & Assert
                BusinessException exception = assertThrows(BusinessException.class,
                                () -> orderService.createOrderFromCart());

                assertEquals("user not found", exception.getMessage());
                assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

                verify(securityService).getAuthenticatedUser();
                verify(cartItemService, never()).getCartItemsEntity();
        }

        @Test
        void createOrderFromCart_WithEmptyCart_ShouldThrowBusinessException() {
                // Arrange
                when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
                when(cartItemService.getCartItemsEntity()).thenReturn(List.of());

                // Act & Assert
                BusinessException exception = assertThrows(BusinessException.class,
                                () -> orderService.createOrderFromCart());

                assertEquals("Cart is empty", exception.getMessage());
                assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());

                verify(securityService).getAuthenticatedUser();
                verify(cartItemService).getCartItemsEntity();
                verify(orderRepository, never()).save(any());
        }

        @Test
        void createOrderFromCart_WithInsufficientStock_ShouldThrowBusinessException() {
                // Arrange
                // Produto com estoque insuficiente
                Product lowStockProduct = Product.builder()
                                .name("Produto com estoque baixo")
                                .price(new BigDecimal("100.00"))
                                .stockQuantity(1)
                                .build();

                CartItem problematicCartItem = CartItem.builder()
                                .product(lowStockProduct)
                                .quantity(5) // Quantidade maior que estoque
                                .build();

                List<CartItem> problematicCartItems = List.of(problematicCartItem);

                when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
                when(cartItemService.getCartItemsEntity()).thenReturn(problematicCartItems);

                // Act & Assert
                BusinessException exception = assertThrows(BusinessException.class,
                                () -> orderService.createOrderFromCart());

                assertTrue(exception.getMessage().contains("Requested quantity is not avalible anymore!"));
                assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());

                verify(securityService).getAuthenticatedUser();
                verify(cartItemService).getCartItemsEntity();
                verify(orderRepository, never()).save(any());
                verify(productService, never()).updateStockQuantity(anyLong(), anyInt());
        }

        @Test
        void calculateTotalAmount_ShouldCalculateCorrectly() {
                // Arrange
                List<OrderItem> testItems = new ArrayList<>();

                OrderItem item1 = OrderItem.builder()
                                .quantity(2)
                                .unitPrice(new BigDecimal("100.00"))
                                .build();

                OrderItem item2 = OrderItem.builder()
                                .quantity(3)
                                .unitPrice(new BigDecimal("50.00"))
                                .build();

                testItems.add(item1);
                testItems.add(item2);

                // Usando reflection para testar o método privado
                // Ou, alternativamente, podemos testar através do createOrderFromCart

                // Simulando um cenário de criação de pedido
                when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
                when(cartItemService.getCartItemsEntity()).thenReturn(cartItems);
                when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                        Order savedOrder = invocation.getArgument(0);
                        // Verifica se o total foi calculado corretamente
                        BigDecimal expectedTotal = new BigDecimal("4000.00"); // 2 * 2000.00
                        assertEquals(0, expectedTotal.compareTo(savedOrder.getTotalAmount()));
                        return savedOrder;
                });
                when(orderMapper.toResponseDto(any(Order.class))).thenReturn(orderResponseDto);
                doNothing().when(productService).updateStockQuantity(anyLong(), anyInt());

                // Act
                orderService.createOrderFromCart();

                // Assert já feito no Answer acima
                verify(orderRepository).save(any(Order.class));
        }

        @Test
        void createOrderFromCart_ShouldCallUpdateStockQuantityForEachItem() {
                // Arrange
                // Criando múltiplos itens no carrinho
                Product product1 = Product.builder()
                                .name("Product 1")
                                .price(new BigDecimal("100.00"))
                                .stockQuantity(20)
                                .build();
                product1.setId(1L);

                Product product2 = Product.builder()
                                .name("Product 2")
                                .price(new BigDecimal("200.00"))
                                .stockQuantity(15)
                                .build();
                product2.setId(2L);

                CartItem cartItem1 = CartItem.builder()
                                .product(product1)
                                .quantity(3)
                                .build();

                CartItem cartItem2 = CartItem.builder()
                                .product(product2)
                                .quantity(2)
                                .build();

                List<CartItem> multipleCartItems = List.of(cartItem1, cartItem2);

                when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
                when(cartItemService.getCartItemsEntity()).thenReturn(multipleCartItems);
                when(orderRepository.save(any(Order.class))).thenReturn(order);
                when(orderMapper.toResponseDto(any(Order.class))).thenReturn(orderResponseDto);
                doNothing().when(productService).updateStockQuantity(anyLong(), anyInt());

                // Act
                orderService.createOrderFromCart();

                // Assert
                // Verifica se updateStockQuantity foi chamado para cada produto
                verify(productService).updateStockQuantity(1L, 3);
                verify(productService).updateStockQuantity(2L, 2);
                verify(productService, times(2)).updateStockQuantity(anyLong(), anyInt());
        }

        @Test
        void createOrderFromCart_ShouldClearCartAfterSuccess() {
                // Arrange
                when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
                when(cartItemService.getCartItemsEntity()).thenReturn(cartItems);
                when(orderRepository.save(any(Order.class))).thenReturn(order);
                when(orderMapper.toResponseDto(any(Order.class))).thenReturn(orderResponseDto);
                doNothing().when(productService).updateStockQuantity(anyLong(), anyInt());

                // Act
                orderService.createOrderFromCart();

                // Assert
                verify(cartItemService).cleanAuthUserCartItems();
        }

        @Test
        void createOrderFromCart_ShouldNotClearCartOnFailure() {
                // Arrange
                when(securityService.getAuthenticatedUser()).thenReturn(Optional.of(user));
                when(cartItemService.getCartItemsEntity()).thenReturn(cartItems);

                // Simula falha ao salvar o pedido
                when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("Database error"));
                doNothing().when(productService).updateStockQuantity(anyLong(), anyInt());

                // Act & Assert
                assertThrows(RuntimeException.class, () -> orderService.createOrderFromCart());

                // Verifica que NÃO limpou o carrinho em caso de falha
                verify(cartItemService, never()).cleanAuthUserCartItems();
        }
}
