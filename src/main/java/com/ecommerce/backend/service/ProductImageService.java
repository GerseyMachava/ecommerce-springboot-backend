package com.ecommerce.backend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${file.upload-dir:uploads/images}")
    private String uploadDir;

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

        // Gerar nome único para o arquivo
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = uploadDir + File.separator + fileName;

        // Criar diretório se não existir
        Files.createDirectories(Paths.get(uploadDir));

        // Salvar arquivo no disco
        Path path = Paths.get(filePath);
        Files.write(path, file.getBytes());

        // Salvar referência no banco de dados
        ProductImage newProductImage = mapper.toEntity(file, product, filePath);
        repository.save(newProductImage);
        return mapper.toResponseDto(newProductImage);

    }

    public List<ProductImageResponseDto> getAllProductImages(Long productId) {
        List<ProductImage> imageList = repository.findAllByProductId(productId);
        return mapper.toResponseList(imageList);
    }
}
