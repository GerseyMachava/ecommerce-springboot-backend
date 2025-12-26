package com.ecommerce.backend.service;

import javax.imageio.IIOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.backend.dto.ResponseDto.ProductResponseDto;
import com.ecommerce.backend.dto.requestDto.ProductRequestDto;
import com.ecommerce.backend.mapper.ProductMapper;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.service.interfaces.IProductService;
import com.ecommerce.backend.shared.exception.BusinessException;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
@Transactional
public class ProductService implements IProductService {

    private ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Override
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        if (productRepository.existsByName(requestDto.name())) {
            throw new BusinessException("Product name already exists");
        }
        Product product = productMapper.toEntity(requestDto);
        return productMapper.toResponseDto(productRepository.save(product));

    }

    @Override
    public ProductResponseDto findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No product found with the id " + id));
        return productMapper.toResponseDto(product);

    }

    @Override
    public ProductResponseDto findProductByName(String name) {
        Product product = productRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("No product found with the name " + name));
        return productMapper.toResponseDto(product);

    }

    @Override
    public ProductResponseDto updateProduct(ProductRequestDto requestDto, long id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No product found with the id " + id));

        productMapper.updateEntity(existingProduct, requestDto);
        return productMapper.toResponseDto(productRepository.save(existingProduct));

    }

    @Override
    public void deleteProduct(long id) {
        productRepository.findById(id).ifPresentOrElse(
                productRepository::delete,
                () -> {
                    throw new EntityNotFoundException("No product found with the id " + id);
                });
    }

}
