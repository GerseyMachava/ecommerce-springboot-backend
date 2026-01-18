package com.ecommerce.backend.dto.ResponseDto;

import java.util.List;

import com.ecommerce.backend.model.enums.OrderStatus;

public record OrderResponseDto(
        Long id,
        OrderStatus status,
        String userEmail,
        List<OrderItemResponseDto> orderItems) {

}
