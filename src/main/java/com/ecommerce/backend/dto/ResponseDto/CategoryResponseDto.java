package com.ecommerce.backend.dto.ResponseDto;

import java.util.List;

import com.ecommerce.backend.model.Category;

public record CategoryResponseDto(
        String name,
        String description,
        Long parentCategoryId
       ) {

}
