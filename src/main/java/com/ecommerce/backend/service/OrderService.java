package com.ecommerce.backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.backend.dto.ResponseDto.OrderResponseDto;
import com.ecommerce.backend.mapper.OrderMapper;
import com.ecommerce.backend.model.CartItem;
import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.OrderItem;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.model.enums.OrderStatus;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.security.SecurityService;
import com.ecommerce.backend.shared.exception.BusinessException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class OrderService {

    private SecurityService securityService;
    private CartItemService cartItemService;
    private OrderRepository orderRepository;
    private ProductService productService;
    private OrderMapper orderMapper;

    public List<OrderResponseDto> getAuthUserOrders() {
        User user = securityService.getAuthenticatedUser()
                .orElseThrow(() -> new BusinessException("user not found",
                        HttpStatus.NOT_FOUND));
        List<Order> userOrderList = orderRepository.findAllByUser(user);
        return userOrderList.stream().map(order -> orderMapper.toResponseDto(order)).toList();
    }

    @Transactional
    public OrderResponseDto createOrderFromCart() {

        User user = securityService.getAuthenticatedUser()
                .orElseThrow(() -> new BusinessException("user not found",
                        HttpStatus.NOT_FOUND));
        List<CartItem> cartItems = cartItemService.getCartitems();

        if (cartItems.isEmpty()) {
            throw new BusinessException("Cart is empty", HttpStatus.CONFLICT);
        }

        Order newOrder = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .build();

        List<OrderItem> orderItems = createOrderList(cartItems, newOrder);
        newOrder.setOrderItems(orderItems);
        newOrder.setTotalAmount(calculateTotalAmount(orderItems));
        orderRepository.save(newOrder);
        cartItemService.cleanAuthUserCartitems();

        return orderMapper.toResponseDto(newOrder);
    }

    private List<OrderItem> createOrderList(List<CartItem> cartItems, Order newOrder) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.getProduct().getStockQuantity() < item.getQuantity()) {
                throw new BusinessException(
                        "Requested quantity is not avalible anymore!" + "Product: " + item.getProduct().getName(),
                        HttpStatus.CONFLICT);
            }
            OrderItem orderItem = new OrderItem(
                    item.getQuantity(),
                    item.getProduct().getPrice(),
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    newOrder

            );
            productService.updateStockQuantity(item.getProduct().getId(), item.getQuantity());
            orderItems.add(orderItem);
        }
        return orderItems;

    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem orderItem : orderItems) {
            BigDecimal price = orderItem.getUnitPrice();
            BigDecimal quantity = BigDecimal.valueOf(orderItem.getQuantity());

            BigDecimal itemSubtotal = price.multiply(quantity);

            total = total.add(itemSubtotal);
        }

        return total;
    }

}
