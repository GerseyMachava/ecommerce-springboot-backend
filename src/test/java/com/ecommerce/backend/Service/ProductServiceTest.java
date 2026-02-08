package com.ecommerce.backend.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import com.ecommerce.backend.dto.ResponseDto.ProductResponseDto;
import com.ecommerce.backend.dto.ResponseDto.ProductResponseListDto;
import com.ecommerce.backend.dto.requestDto.ProductRequestDto;
import com.ecommerce.backend.mapper.ProductMapper;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.service.ProductService;
import com.ecommerce.backend.shared.exception.BusinessException;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequestDto requestDto;
    private ProductResponseDto responseDto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
        BigDecimal price = new BigDecimal(100.0);
        product = Product.builder()
                .name("Product Test")
                .description("Description")
                .price(price)
                .stockQuantity(50)
                .build();

        requestDto = new ProductRequestDto(
                "Product Test",
                "Description",
                price,
                50);

        responseDto = new ProductResponseDto(
                1L,
                "Product Test",
                "Description",
                price,
                50);
    }

    @Test
    void getAllProducts_ShouldReturnPagedProducts() {
        // Arrange
        Page<Product> pagedProducts = new PageImpl<>(List.of(product));
        when(productRepository.findAll(pageable)).thenReturn(pagedProducts);
        when(productMapper.toResponseListDto(pagedProducts))
                .thenReturn(new ProductResponseListDto(List.of(responseDto), pagedProducts.getSize(),
                        pagedProducts.getTotalElements(), pagedProducts.getTotalPages(), pagedProducts.getNumber()));

        // Act
        ProductResponseListDto result = productService.getAllProducts(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.productResponseDto().size());
        verify(productRepository).findAll(pageable);
        verify(productMapper).toResponseListDto(pagedProducts);
    }

    @Test
    void createProduct_WithUniqueName_ShouldCreateProduct() {
        // Arrange
        when(productRepository.existsByName("Product Test")).thenReturn(false);
        when(productMapper.toEntity(requestDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDto(product)).thenReturn(responseDto);

        // Act
        ProductResponseDto result = productService.createProduct(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Product Test", result.name());
        verify(productRepository).existsByName("Product Test");
        verify(productRepository).save(product);
    }

    @Test
    void createProduct_WithDuplicateName_ShouldThrowBusinessException() {
        // Arrange
        when(productRepository.existsByName("Product Test")).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.createProduct(requestDto));

        assertEquals("Product name already exists", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        verify(productRepository).existsByName("Product Test");
        verify(productRepository, never()).save(any());
    }

    @Test
    void findProductById_WithExistingId_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDto(product)).thenReturn(responseDto);

        // Act
        ProductResponseDto result = productService.findProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(productRepository).findById(1L);
    }

    @Test
    void findProductById_WithNonExistingId_ShouldThrowBusinessException() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.findProductById(999L));

        assertEquals("No product found with the id 999", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(productRepository).findById(999L);
    }

    @Test
    void searchProducts_WithNameAndMaxPrice_ShouldReturnFilteredProducts() {
        // Arrange
        String name = "Test";
        Double maxPrice = 200.0;
        Page<Product> pagedProducts = new PageImpl<>(List.of(product));

        when(productRepository.findByNameContainingIgnoreCaseAndPriceLessThanEqual(
                name, maxPrice, pageable)).thenReturn(pagedProducts);
        when(productMapper.toResponseListDto(pagedProducts))
                .thenReturn(new ProductResponseListDto(List.of(responseDto), pagedProducts.getSize(),
                        pagedProducts.getTotalElements(), pagedProducts.getTotalPages(), pagedProducts.getNumber()));

        // Act
        ProductResponseListDto result = productService.searchProducts(name, maxPrice, pageable);

        // Assert
        assertNotNull(result);
        verify(productRepository).findByNameContainingIgnoreCaseAndPriceLessThanEqual(
                name, maxPrice, pageable);
    }

    @Test
    void searchProducts_WithNullParameters_ShouldUseDefaultValues() {
        // Arrange
        Page<Product> pagedProducts = new PageImpl<>(List.of(product));

        when(productRepository.findByNameContainingIgnoreCaseAndPriceLessThanEqual(
                "", Double.MAX_VALUE, pageable)).thenReturn(pagedProducts);
        when(productMapper.toResponseListDto(pagedProducts))
                .thenReturn(new ProductResponseListDto(List.of(responseDto), pagedProducts.getSize(),
                        pagedProducts.getTotalElements(), pagedProducts.getTotalPages(), pagedProducts.getNumber()));

        // Act
        ProductResponseListDto result = productService.searchProducts(null, null, pageable);

        // Assert
        assertNotNull(result);
        verify(productRepository).findByNameContainingIgnoreCaseAndPriceLessThanEqual(
                "", Double.MAX_VALUE, pageable);
    }

    @Test
    void updateProduct_WithExistingId_ShouldUpdateProduct() {
        BigDecimal price = new BigDecimal(150.0);
        // Arrange
        ProductRequestDto updateDto = new ProductRequestDto(
                "Updated Product",
                "Updated Description",
                price,
                30);

        ProductResponseDto updatedResponse = new ProductResponseDto(
                1L,
                "Updated Product",
                "Updated Description",
                price,
                30);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productMapper).updateEntity(product, updateDto);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponseDto(product)).thenReturn(updatedResponse);

        // Act
        ProductResponseDto result = productService.updateProduct(updateDto, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Product", result.name());
        verify(productRepository).findById(1L);
        verify(productMapper).updateEntity(product, updateDto);
        verify(productRepository).save(product);
    }

    @Test
    void updateProduct_WithNonExistingId_ShouldThrowBusinessException() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.updateProduct(requestDto, 999L));

        assertEquals("No product found with the id 999", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(productRepository).findById(999L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_WithExistingId_ShouldDeleteProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository).findById(1L);
        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_WithNonExistingId_ShouldThrowBusinessException() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.deleteProduct(999L));

        assertEquals("No product found with the id 999", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(productRepository).findById(999L);
        verify(productRepository, never()).delete(any());
    }

    @Test
    void updateStockQuantity_ShouldDecreaseStock() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        int initialStock = product.getStockQuantity();
        int takenQuantity = 10;

        // Act
        productService.updateStockQuantity(1L, takenQuantity);

        // Assert
        assertEquals(initialStock - takenQuantity, product.getStockQuantity());
        verify(productRepository).findById(1L);
        verify(productRepository).save(product);
    }

    @Test
    void updateStockQuantity_WithNonExistingProduct_ShouldThrowBusinessException() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> productService.updateStockQuantity(999L, 10));

        assertEquals("No  product found with the id 999", exception.getMessage());
        verify(productRepository).findById(999L);
        verify(productRepository, never()).save(any());
    }
}