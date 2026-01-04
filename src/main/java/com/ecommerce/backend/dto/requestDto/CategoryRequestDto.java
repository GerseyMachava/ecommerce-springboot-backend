package com.ecommerce.backend.dto.requestDto;

public record CategoryRequestDto(
        String name,
        String description,
        Long parentCategoryId) {

}
