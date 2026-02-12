package com.ecommerce.backend.mapper;

import org.springframework.stereotype.Component;

import com.ecommerce.backend.dto.ResponseDto.CategoryResponseDto;
import com.ecommerce.backend.dto.requestDto.CategoryRequestDto;
import com.ecommerce.backend.model.Category;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequestDto dto, Category category) {
        return Category.builder()
                .name(dto.name())
                .description(dto.description())
                .parentCategory(category)
                .build();
    }

    public CategoryResponseDto toResponseDto(Category category) {
        Long parentId = null;
        if (category.getParentCategory() != null) {
            parentId = category.getParentCategory().getId();
        }

        CategoryResponseDto responseDto = new CategoryResponseDto(
            category.getId(),
                category.getName(),
                category.getDescription(),
                parentId);
        return responseDto;

    }

    public Category updateEntity(Category existingCategory, CategoryRequestDto requestDto, Category parentCategory) {
        existingCategory.setName(requestDto.name());
        existingCategory.setDescription(requestDto.description());
        existingCategory.setParentCategory(parentCategory);
        return existingCategory;
    }

}