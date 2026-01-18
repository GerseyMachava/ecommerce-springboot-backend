package com.ecommerce.backend.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ecommerce.backend.dto.ResponseDto.OrderItemResponseDto;
import com.ecommerce.backend.dto.ResponseDto.OrderResponseDto;
import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.OrderItem;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class OrderMapper {

    public OrderResponseDto toResponseDto(Order order) {
        List<OrderItemResponseDto> orderItemsResponseList = toOrderItemsResponseList(order.getOrderItems());
        OrderResponseDto responseDto = new OrderResponseDto(
                order.getId(),
                order.getStatus(),
                order.getUser().getEmail(),
                orderItemsResponseList);
        return responseDto;

    }

    private List<OrderItemResponseDto> toOrderItemsResponseList(List<OrderItem> list) {
        return list.stream().map(
                orderItem -> toOrderItemResponseDto(orderItem)).toList();

    }

    private OrderItemResponseDto toOrderItemResponseDto(OrderItem orderItem) {
        OrderItemResponseDto responseDto = new OrderItemResponseDto(
                orderItem.getId(),
                orderItem.getProductId(),
                orderItem.getProductName(),
                orderItem.getUnitPrice(),
                orderItem.getQuantity()

        );
        return responseDto;
    }

}
