package com.ecommerce.backend.service.interfaces;

import java.util.List;

import com.ecommerce.backend.dto.ResponseDto.CategoryResponseDto;
import com.ecommerce.backend.dto.requestDto.CategoryRequestDto;

public interface ICategoryService {
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto);

    public List<CategoryResponseDto> getAllCategories();

    public CategoryResponseDto findById(Long id);

    public CategoryResponseDto updateCategory(Long existingCategoryId, CategoryRequestDto requestDto);

    public void deleteCategory(Long id);
}
