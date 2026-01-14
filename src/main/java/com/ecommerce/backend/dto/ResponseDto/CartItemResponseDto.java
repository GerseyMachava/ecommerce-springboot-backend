package com.ecommerce.backend.dto.ResponseDto;

import java.math.BigDecimal;

public record CartItemResponseDto(
    Long cartItemId,
    String productName,
    BigDecimal productPrice,
    int quantity

) {

}
