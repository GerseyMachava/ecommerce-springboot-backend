package com.ecommerce.backend.dto.requestDto;

import com.ecommerce.backend.model.enums.OrderStatus;

public record OrderStatusRequestDto(
    Long orderId,
    OrderStatus status
) {

}
