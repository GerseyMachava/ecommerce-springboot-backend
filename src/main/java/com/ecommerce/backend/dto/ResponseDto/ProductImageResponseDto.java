package com.ecommerce.backend.dto.ResponseDto;

public record ProductImageResponseDto(
        Long imgProductId,
        String imageName,
        String imgType,
        Long productId) {

}
