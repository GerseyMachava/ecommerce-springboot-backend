package com.ecommerce.backend.mapper;

import org.springframework.stereotype.Component;


import com.ecommerce.backend.dto.ResponseDto.ProductCategoryResponseDto;

import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.ProductCategory;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ProductCategoryMapper {

    public ProductCategoryResponseDto toResponseDto(ProductCategory productCategory) {
        ProductCategoryResponseDto responseDto = new ProductCategoryResponseDto(
                productCategory.getProduct().getId(),
                productCategory.getProduct().getName(),
                productCategory.getProduct().getDescription(),
                productCategory.getProduct().getPrice(),
                productCategory.getProduct().getStockQuantity(),
                productCategory.getCategory().getId(),
                productCategory.getCategory().getName(),
                productCategory.getCategory().getDescription()

        );
        return responseDto;

    }

    public ProductCategory toEntity(Product product, Category category) {
        return ProductCategory.builder()
                .category(category)
                .product(product)
                .build();
    }

    public ProductCategory updateEntity(ProductCategory existingProductCategory, Product product, Category category) {
        existingProductCategory.setCategory(category);
        existingProductCategory.setProduct(product);
        return existingProductCategory;
    }
}
