package com.ecommerce.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.backend.dto.ResponseDto.ProductResponseDto;
import com.ecommerce.backend.dto.ResponseDto.ProductResponseListDto;
import com.ecommerce.backend.dto.requestDto.ProductRequestDto;
import com.ecommerce.backend.mapper.ProductMapper;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.service.interfaces.IProductService;
import com.ecommerce.backend.shared.exception.BusinessException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
@Transactional
public class ProductService implements IProductService {

    private ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Override
    public ProductResponseListDto getAllProducts(Pageable pageable) {
        Page<Product> pagedProducts = productRepository.findAll(pageable);

        return productMapper.toResponseListDto(pagedProducts);

    }

    @Override
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        if (productRepository.existsByName(requestDto.name())) {
            throw new BusinessException("Product name already exists", HttpStatus.CONFLICT);
        }
        Product product = productMapper.toEntity(requestDto);
        return productMapper.toResponseDto(productRepository.save(product));

    }

    @Override
    public ProductResponseDto findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("No product found with the id " + id, HttpStatus.NOT_FOUND));
        return productMapper.toResponseDto(product);

    }

    @Override
    public ProductResponseListDto searchProducts(String name, Double maxPrice, Pageable pageable) {
        Double priceFilter = (maxPrice != null) ? maxPrice : Double.MAX_VALUE;
        String nameFilter = (name != null) ? name : "";
        Page<Product> pagedProducts = productRepository
                .findByNameContainingIgnoreCaseAndPriceLessThanEqual(nameFilter, priceFilter, pageable);
        
        return productMapper.toResponseListDto(pagedProducts);

    }

    @Override
    public ProductResponseDto updateProduct(ProductRequestDto requestDto, long id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("No product found with the id " + id, HttpStatus.NOT_FOUND));

        productMapper.updateEntity(existingProduct, requestDto);
        return productMapper.toResponseDto(productRepository.save(existingProduct));

    }

    @Override
    public void deleteProduct(long id) {
        productRepository.findById(id).ifPresentOrElse(
                productRepository::delete,
                () -> {
                    throw new BusinessException("No product found with the id " + id, HttpStatus.NOT_FOUND);
                });
    }

    public Product getProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new BusinessException("No  product found with the id " + id,
                        HttpStatus.NOT_FOUND));
        return product;
    }


    public void updateStockQuantity(Long productId, int takenQuantity){
        Product product = getProduct(productId);
        product.setStockQuantity(product.getStockQuantity()-takenQuantity);
        productRepository.save(product);
    }
}
