package com.ecommerce.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.backend.dto.ResponseDto.CategoryResponseDto;
import com.ecommerce.backend.dto.requestDto.CategoryRequestDto;
import com.ecommerce.backend.mapper.CategoryMapper;
import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.service.interfaces.ICategoryService;
import com.ecommerce.backend.shared.exception.BusinessException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        if (categoryRepository.existsByName(requestDto.name())) {
            throw new BusinessException("category with the name " + requestDto.name() + " Already exists",
                    HttpStatus.CONFLICT);
        }

        Category parentCategory = null;
        if (requestDto.parentCategoryId() != null) {
            parentCategory = categoryRepository.findById(requestDto.parentCategoryId()).orElseThrow(
                    () -> new BusinessException("No Parent Category found with the id " + requestDto.parentCategoryId(),
                            HttpStatus.NOT_FOUND));
        }

        Category category = categoryMapper.toEntity(requestDto, parentCategory);
        categoryRepository.save(category);
        return categoryMapper.toResponseDto(category);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(category -> categoryMapper.toResponseDto(category)).toList();
    }

    @Override
    public CategoryResponseDto findById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new BusinessException("No  Category found with the id " + id,
                        HttpStatus.NOT_FOUND));
        return categoryMapper.toResponseDto(category);
    }

    @Override
    public CategoryResponseDto updateCategory(Long existingCategoryId, CategoryRequestDto requestDto) {
        Category existingCategory = categoryRepository.findById(existingCategoryId).orElseThrow(
                () -> new BusinessException("No Category found with the id " + requestDto.parentCategoryId(),
                        HttpStatus.NOT_FOUND));
        if (categoryRepository.existsByNameAndIdNot(requestDto.name(), existingCategory.getId())) {
            throw new BusinessException("category with the name " + requestDto.name() + " Already exists",
                    HttpStatus.CONFLICT);
        }

        Category parentCategory = null;
        if (requestDto.parentCategoryId() != null) {
            parentCategory = categoryRepository.findById(requestDto.parentCategoryId()).orElseThrow(
                    () -> new BusinessException("No Parent Category found with the id " + requestDto.parentCategoryId(),
                            HttpStatus.NOT_FOUND));
        }

        Category updatedCategory = categoryMapper.updateEntity(existingCategory, requestDto, parentCategory);
        return categoryMapper.toResponseDto(updatedCategory);

    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.findById(id).ifPresentOrElse(
                categoryRepository::delete, () -> {
                    throw new BusinessException("No category found with the id " + id, HttpStatus.NOT_FOUND);

                });
    }

    public Category getCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new BusinessException("No  Category found with the id " + id,
                        HttpStatus.NOT_FOUND));
        return category;
    }

}
