package com.ecommerce.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ecommerce.backend.dto.ResponseDto.ProductCategoryResponseDto;
import com.ecommerce.backend.dto.requestDto.ProductCategoryRequestDto;
import com.ecommerce.backend.mapper.ProductCategoryMapper;
import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.ProductCategory;
import com.ecommerce.backend.repository.ProductCategoryRepository;
import com.ecommerce.backend.service.interfaces.IProductCategoryService;
import com.ecommerce.backend.shared.exception.BusinessException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductCategoryService implements IProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductCategoryMapper productCategoryMapper;
    private final ProductService productService;
    private final CategoryService categoryService;

    private void existingValidator(ProductCategoryRequestDto requestDto) {
        if (productCategoryRepository.existsByCategoryIdAndProductId(requestDto.categoryId(), requestDto.productId())) {
            throw new BusinessException("The product is already registered to this category", HttpStatus.CONFLICT);
        }
    }

    @Override
    public ProductCategoryResponseDto create(ProductCategoryRequestDto requestDto) {
        existingValidator(requestDto);
        Product product = productService.getProduct(requestDto.productId());
        Category category = categoryService.getCategory(requestDto.categoryId());
        ProductCategory newProductCategory = productCategoryMapper.toEntity(product, category);
        productCategoryRepository.save(newProductCategory);
        return productCategoryMapper.toResponseDto(newProductCategory);

    }

    @Override
    public ProductCategoryResponseDto findById(Long id) {
        ProductCategory productCategory = productCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("ProductCategory not Found ", HttpStatus.NOT_FOUND));
        return productCategoryMapper.toResponseDto(productCategory);
    }

    @Override
    public ProductCategoryResponseDto update(Long id, ProductCategoryRequestDto requestDto) {
        existingValidator(requestDto);
        ProductCategory existingProductCategoty = productCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("ProductCategory not Found", HttpStatus.NOT_FOUND));
        Product product = productService.getProduct(requestDto.productId());
        Category category = categoryService.getCategory(requestDto.categoryId());
        ProductCategory updatedProductCategory = productCategoryMapper.updateEntity(existingProductCategoty, product,
                category);

        return productCategoryMapper.toResponseDto(updatedProductCategory);

    }

    @Override
    public List<ProductCategoryResponseDto> getAll() {
        List<ProductCategory> listProductCategories = productCategoryRepository.findAll();

        return listProductCategories.stream()
                .map(productCategory -> productCategoryMapper.toResponseDto(productCategory)).toList();
    }

    @Override
    public List<ProductCategoryResponseDto> findAllProductsByCategoryId(Long categoryId) {
        List<ProductCategory> listProductCategories = productCategoryRepository.findAllProductsByCategoryId(categoryId);

        return listProductCategories.stream()
                .map(productCategory -> productCategoryMapper.toResponseDto(productCategory)).toList();
    }

    @Override
    public void delete(Long id) {
        productCategoryRepository.findById(id).ifPresentOrElse(productCategoryRepository::delete,
                () -> {
                    throw new BusinessException("No Product category found with the id " + id, HttpStatus.NOT_FOUND);
                });
    }

}
