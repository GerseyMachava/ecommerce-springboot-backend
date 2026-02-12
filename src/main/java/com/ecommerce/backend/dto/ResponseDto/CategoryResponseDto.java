package com.ecommerce.backend.dto.ResponseDto;

public record CategoryResponseDto(
        Long id,
        String name,
        String description,
        Long parentCategoryId
       ) {

}
