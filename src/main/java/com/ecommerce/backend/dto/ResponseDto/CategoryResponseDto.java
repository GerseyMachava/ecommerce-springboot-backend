package com.ecommerce.backend.dto.ResponseDto;

public record CategoryResponseDto(
        String name,
        String description,
        Long parentCategoryId
       ) {

}
