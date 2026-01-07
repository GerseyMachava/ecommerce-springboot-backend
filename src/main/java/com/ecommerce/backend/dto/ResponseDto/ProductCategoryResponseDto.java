package com.ecommerce.backend.dto.ResponseDto;

import java.math.BigDecimal;

public record ProductCategoryResponseDto(
        // ProductResponseDto product,
        // CategoryResponseDto categoryResponseDto
        long productId,
        String productName,
        String productDescription,
        BigDecimal productPrice,
        int productStockQuantity,
        Long categoryId,
        String CategoryName,
        String Categorydescription

) {

}
