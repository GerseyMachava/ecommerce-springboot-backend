package com.ecommerce.backend.service.interfaces;

import com.ecommerce.backend.dto.ResponseDto.ProductResponseDto;
import com.ecommerce.backend.dto.requestDto.ProductRequestDto;

public interface IProductService {

    public ProductResponseDto createProduct(ProductRequestDto requestDto);

    public ProductResponseDto findProductById(Long id);

    public ProductResponseDto findProductByName(String name);

    public ProductResponseDto updateProduct(ProductRequestDto requestDto, long id);

    public void deleteProduct(long id);
}
