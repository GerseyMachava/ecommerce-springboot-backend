package com.ecommerce.backend.dto.requestDto;

import java.math.BigDecimal;

public record ProductRequestDto(
                String name,
                String description,
                BigDecimal price,
                int stockQuantity) {

}
