package com.ecommerce.backend.dto.ResponseDto;

import java.math.BigDecimal;

public record ProductResponseDto(
     long id,
     String name,
     String description,
     BigDecimal price,
     int stockQuantity
) {

}
