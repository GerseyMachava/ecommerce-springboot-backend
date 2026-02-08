package com.ecommerce.backend.mapper;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.backend.dto.ResponseDto.ProductImageResponseDto;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.ProductImage;

@Component
public class ProductImageMapper {

    public ProductImage toEntity(MultipartFile file, Product product, String filePath) throws IOException {
        ProductImage productImage = ProductImage.builder()
                .imageName(file.getOriginalFilename())
                .imgType(file.getContentType())
                .url(filePath)
                .product(product)
                .build();
        return productImage;
    }

    public ProductImageResponseDto toResponseDto(ProductImage productImage) {
        ProductImageResponseDto responseDto = new ProductImageResponseDto(
                productImage.getId(),
                productImage.getImageName(),
                productImage.getImgType(),
                productImage.getUrl(),
                productImage.getProduct().getId());
        return responseDto;
    }

    public List<ProductImageResponseDto> toResponseList(List<ProductImage> productImageList) {
        return productImageList.stream().map(
                productImage -> toResponseDto(productImage)).toList();
    }

}
