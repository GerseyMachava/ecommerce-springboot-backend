package com.ecommerce.backend.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.backend.dto.ResponseDto.ProductImageResponseDto;
import com.ecommerce.backend.mapper.ProductImageMapper;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.ProductImage;
import com.ecommerce.backend.repository.ProductImageRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ProductImageService {
    
    // Limite: 5MB (em bytes)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private ProductService productService;
    private ProductImageRepository repository;
    private ProductImageMapper mapper;

    public ProductImageResponseDto saveImage(Long productid, MultipartFile file) throws IOException {
        // Validar tipo
        if (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")) {
            throw new IllegalArgumentException("Only JPEG or PNG images are allowed");
        }
        
        // Validar tamanho
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 5MB. Current size: " + (file.getSize() / (1024 * 1024)) + "MB");
        }
        
        Product product = productService.getProduct(productid);

        ProductImage newProductImage = mapper.toEntity(file, product);
        repository.save(newProductImage);
        return mapper.toResponseDto(newProductImage);

    }

    public List<ProductImageResponseDto> getAllProductImages(Long productId) {
        List<ProductImage> imageList = repository.findAllByProductId(productId);
        return mapper.toResponseList(imageList);
    }
}
