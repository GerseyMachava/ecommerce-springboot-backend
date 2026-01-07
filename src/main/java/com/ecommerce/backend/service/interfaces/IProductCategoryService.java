package com.ecommerce.backend.service.interfaces;

import java.util.List;

import com.ecommerce.backend.dto.ResponseDto.ProductCategoryResponseDto;
import com.ecommerce.backend.dto.requestDto.ProductCategoryRequestDto;

public interface IProductCategoryService {

    public ProductCategoryResponseDto create(ProductCategoryRequestDto requestDto);

    public ProductCategoryResponseDto findById(Long id);

    public ProductCategoryResponseDto update(Long id, ProductCategoryRequestDto requestDto);

    public List<ProductCategoryResponseDto> getAll();

    public List<ProductCategoryResponseDto> findAllProductsByCategoryId(Long categoryId);

    public void delete(Long id);

}
