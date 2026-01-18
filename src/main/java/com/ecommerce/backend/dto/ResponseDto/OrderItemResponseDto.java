package com.ecommerce.backend.dto.ResponseDto;

import java.math.BigDecimal;

public record OrderItemResponseDto(
    Long id,
    Long productId,
    String productName,
    BigDecimal price,
    int quantity
) {

}
