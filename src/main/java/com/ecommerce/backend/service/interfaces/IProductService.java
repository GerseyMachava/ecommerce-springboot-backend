package com.ecommerce.backend.service.interfaces;


import org.springframework.data.domain.Pageable;

import com.ecommerce.backend.dto.ResponseDto.ProductResponseDto;
import com.ecommerce.backend.dto.ResponseDto.ProductResponseListDto;
import com.ecommerce.backend.dto.requestDto.ProductRequestDto;

public interface IProductService {

    public ProductResponseListDto getAllProducts(Pageable pageable);

    public ProductResponseDto createProduct(ProductRequestDto requestDto);

    public ProductResponseDto findProductById(Long id);

    public ProductResponseListDto searchProducts(String name, Double maxPrice, Pageable pageable);

    public ProductResponseDto updateProduct(ProductRequestDto requestDto, long id);

    public void deleteProduct(long id);
}
