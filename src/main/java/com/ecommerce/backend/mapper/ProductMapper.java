package com.ecommerce.backend.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import com.ecommerce.backend.dto.ResponseDto.ProductResponseDto;
import com.ecommerce.backend.dto.ResponseDto.ProductResponseListDto;
import com.ecommerce.backend.dto.requestDto.ProductRequestDto;
import com.ecommerce.backend.model.Product;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequestDto dto) {
        return Product.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .stockQuantity(dto.stockQuantity())
                .build();
    }

    public ProductResponseDto toResponseDto(Product product) {
        ProductResponseDto responseDto = new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity()

        );
        return responseDto;
    }

    public Product updateEntity(Product product, ProductRequestDto requestDto) {
        product.setName(requestDto.name());
        product.setDescription(requestDto.description());
        product.setPrice(requestDto.price());
        product.setStockQuantity(requestDto.stockQuantity());
        return product;
    }

    /*public List<ProductResponseDto> toListResponseDto(List<Product> listProduct) {
        return listProduct.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

    }
                /* */

    public ProductResponseListDto toResponseListDto(Page<Product> pagedProduct) {
        List<ProductResponseDto> dtos = pagedProduct.getContent().stream()
                .map(this::toResponseDto)
                .toList();
        return new ProductResponseListDto(
                dtos,
                pagedProduct.getSize(),
                pagedProduct.getTotalElements(),
                pagedProduct.getTotalPages(),
                pagedProduct.getNumber());
    }

}
