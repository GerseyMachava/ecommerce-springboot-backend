package com.ecommerce.backend.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.ecommerce.backend.dto.ResponseDto.ProductCategoryResponseDto;
import com.ecommerce.backend.dto.requestDto.ProductCategoryRequestDto;
import com.ecommerce.backend.mapper.ProductCategoryMapper;
import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.ProductCategory;
import com.ecommerce.backend.repository.ProductCategoryRepository;
import com.ecommerce.backend.service.CategoryService;
import com.ecommerce.backend.service.ProductCategoryService;
import com.ecommerce.backend.service.ProductService;
import com.ecommerce.backend.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ProductCategoryMapper productCategoryMapper;

    @Mock
    private ProductService productService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductCategoryService productCategoryService;

    private Product product;
    private Category category;
    private ProductCategory productCategory;
    private ProductCategoryRequestDto requestDto;
    private ProductCategoryResponseDto responseDto;

    @BeforeEach
    void setUp() {
        // Setup Produto
        product = Product.builder()
                .name("Smartphone Galaxy S23")
                .description("Smartphone top de linha Samsung")
                .price(new BigDecimal("4500.00"))
                .stockQuantity(10)
                .build();
         product.setId(1L);
        // Setup Categoria
        category = Category.builder()
                .name("Eletrônicos")
                .description("Produtos eletrônicos em geral")
                .build();
        category.setId(1L);
        // Setup ProductCategory (associação)
        productCategory = ProductCategory.builder()
                .product(product)
                .category(category)
                .build();
        productCategory.setId(1L);

        // Setup Request DTO
        requestDto = new ProductCategoryRequestDto(
                1L, 1L);

        // Setup Response DTO - usando o RECORD real!
        responseDto = new ProductCategoryResponseDto(
                1L, // productId
                "Smartphone Galaxy S23", // productName
                "Smartphone top de linha Samsung", // productDescription
                new BigDecimal("4500.00"), // productPrice
                10, // productStockQuantity
                1L, // categoryId
                "Eletrônicos", // CategoryName
                "Produtos eletrônicos em geral" // Categorydescription
        );
    }

    @Nested
    @DisplayName("create - Associar produto à categoria")
    class CreateTests {

        @Test
        @DisplayName("✅ Deve associar produto à categoria com sucesso")
        void create_WithValidData_ShouldCreateAssociation() {
            // Arrange
            when(productCategoryRepository.existsByCategoryIdAndProductId(1L, 1L)).thenReturn(false);
            when(productService.getProduct(1L)).thenReturn(product);
            when(categoryService.getCategory(1L)).thenReturn(category);
            when(productCategoryMapper.toEntity(product, category)).thenReturn(productCategory);
            when(productCategoryRepository.save(productCategory)).thenReturn(productCategory);
            when(productCategoryMapper.toResponseDto(productCategory)).thenReturn(responseDto);

            // Act
            ProductCategoryResponseDto result = productCategoryService.create(requestDto);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.productId());
            assertEquals("Smartphone Galaxy S23", result.productName());
            assertEquals("Eletrônicos", result.categoryName());
            assertEquals(new BigDecimal("4500.00"), result.productPrice());
            assertEquals(10, result.productStockQuantity());

            verify(productCategoryRepository).existsByCategoryIdAndProductId(1L, 1L);
            verify(productService).getProduct(1L);
            verify(categoryService).getCategory(1L);
            verify(productCategoryRepository).save(productCategory);
        }

        @Test
        @DisplayName("🔒 Deve rejeitar associação duplicada")
        void create_WithDuplicateAssociation_ShouldThrowException() {
            // Arrange
            when(productCategoryRepository.existsByCategoryIdAndProductId(1L, 1L)).thenReturn(true);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productCategoryService.create(requestDto));

            assertEquals("The product is already registered to this category", exception.getMessage());
            assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());

            verify(productCategoryRepository).existsByCategoryIdAndProductId(1L, 1L);
            verify(productService, never()).getProduct(any());
            verify(categoryService, never()).getCategory(any());
            verify(productCategoryRepository, never()).save(any());
        }

        @Test
        @DisplayName("❌ Deve lançar exceção quando produto não existe")
        void create_WithNonExistingProduct_ShouldThrowException() {
            // Arrange
            when(productCategoryRepository.existsByCategoryIdAndProductId(1L, 1L)).thenReturn(false);
            when(productService.getProduct(1L)).thenThrow(
                    new BusinessException("No Product found with the id 1", HttpStatus.NOT_FOUND));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productCategoryService.create(requestDto));

            assertEquals("No Product found with the id 1", exception.getMessage());

            verify(productCategoryRepository).existsByCategoryIdAndProductId(1L, 1L);
            verify(productService).getProduct(1L);
            verify(categoryService, never()).getCategory(any());
            verify(productCategoryRepository, never()).save(any());
        }

        @Test
        @DisplayName("❌ Deve lançar exceção quando categoria não existe")
        void create_WithNonExistingCategory_ShouldThrowException() {
            // Arrange
            when(productCategoryRepository.existsByCategoryIdAndProductId(1L, 1L)).thenReturn(false);
            when(productService.getProduct(1L)).thenReturn(product);
            when(categoryService.getCategory(1L)).thenThrow(
                    new BusinessException("No Category found with the id 1", HttpStatus.NOT_FOUND));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productCategoryService.create(requestDto));

            assertEquals("No Category found with the id 1", exception.getMessage());

            verify(productCategoryRepository).existsByCategoryIdAndProductId(1L, 1L);
            verify(productService).getProduct(1L);
            verify(categoryService).getCategory(1L);
            verify(productCategoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update - Atualizar associação")
    class UpdateTests {

        @Test
        @DisplayName("✅ Deve atualizar associação com sucesso")
        void update_WithValidData_ShouldUpdateAssociation() {
            // Arrange
            Product novoProduto = Product.builder()
                    .name("iPhone 15")
                    .description("Apple iPhone 15")
                    .price(new BigDecimal("5500.00"))
                    .stockQuantity(5)
                    .build();
              novoProduto.setId(2L);
            Category novaCategoria = Category.builder()
                    .name("Smartphones")
                    .description("Telefones celulares")
                    .build();
            novaCategoria.setId(2L);
            ProductCategory productCategoryAtualizada = ProductCategory.builder()
                    .product(novoProduto)
                    .category(novaCategoria)
                    .build();
            productCategoryAtualizada.setId(1L);
            ProductCategoryResponseDto responseAtualizado = new ProductCategoryResponseDto(
                    2L, "iPhone 15", "Apple iPhone 15",
                    new BigDecimal("5500.00"), 5,
                    2L, "Smartphones", "Telefones celulares");

            when(productCategoryRepository.existsByCategoryIdAndProductId(2L, 2L)).thenReturn(false);
            when(productCategoryRepository.findById(1L)).thenReturn(Optional.of(productCategory));
            when(productService.getProduct(2L)).thenReturn(novoProduto);
            when(categoryService.getCategory(2L)).thenReturn(novaCategoria);
            when(productCategoryMapper.updateEntity(productCategory, novoProduto, novaCategoria))
                    .thenReturn(productCategoryAtualizada);
            when(productCategoryMapper.toResponseDto(productCategoryAtualizada)).thenReturn(responseAtualizado);

            // Act
            ProductCategoryResponseDto result = productCategoryService.update(1L,
                   new ProductCategoryRequestDto(2L,2L));

            // Assert
            assertNotNull(result);
            assertEquals(2L, result.productId());
            assertEquals("iPhone 15", result.productName());
            assertEquals(2L, result.categoryId());
            assertEquals("Smartphones", result.categoryName());
        }

        @Test
        @DisplayName("❌ Deve lançar exceção quando associação não existe")
        void update_WithNonExistingId_ShouldThrowException() {
            // Arrange
            when(productCategoryRepository.existsByCategoryIdAndProductId(1L, 1L)).thenReturn(false);
            when(productCategoryRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productCategoryService.update(999L, requestDto));

            assertEquals("ProductCategory not Found", exception.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

            verify(productCategoryRepository).existsByCategoryIdAndProductId(1L, 1L);
            verify(productCategoryRepository).findById(999L);
        }

        @Test
        @DisplayName("🔒 Deve rejeitar atualização para associação duplicada")
        void update_WithDuplicateAssociation_ShouldThrowException() {
            // Arrange
            ProductCategoryRequestDto updateDto = new ProductCategoryRequestDto(2L, 2L);

            when(productCategoryRepository.existsByCategoryIdAndProductId(2L, 2L)).thenReturn(true);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productCategoryService.update(1L, updateDto));

            assertEquals("The product is already registered to this category", exception.getMessage());

            verify(productCategoryRepository).existsByCategoryIdAndProductId(2L, 2L);
            verify(productService, never()).getProduct(any());
            verify(categoryService, never()).getCategory(any());
        }
    }

    @Nested
    @DisplayName("findById - Buscar associação")
    class FindByIdTests {

        @Test
        @DisplayName("✅ Deve retornar associação quando existe")
        void findById_WithExistingId_ShouldReturnAssociation() {
            // Arrange
            when(productCategoryRepository.findById(1L)).thenReturn(Optional.of(productCategory));
            when(productCategoryMapper.toResponseDto(productCategory)).thenReturn(responseDto);

            // Act
            ProductCategoryResponseDto result = productCategoryService.findById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.productId());
            assertEquals(1L, result.categoryId());
            assertEquals("Smartphone Galaxy S23", result.productName());
            assertEquals("Eletrônicos", result.categoryName());

            verify(productCategoryRepository).findById(1L);
        }

        @Test
        @DisplayName("❌ Deve lançar exceção quando associação não existe")
        void findById_WithNonExistingId_ShouldThrowException() {
            // Arrange
            when(productCategoryRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productCategoryService.findById(999L));

            assertEquals("ProductCategory not Found ", exception.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        }
    }

    @Nested
    @DisplayName("findAllProductsByCategoryId - Buscar produtos por categoria")
    class FindAllProductsByCategoryIdTests {

        @Test
        @DisplayName("✅ Deve retornar todos os produtos de uma categoria")
        void findAllProductsByCategoryId_ShouldReturnProducts() {
            // Arrange
            List<ProductCategory> productCategories = List.of(productCategory);
            List<ProductCategoryResponseDto> responseDtos = List.of(responseDto);

            when(productCategoryRepository.findAllProductsByCategoryId(1L)).thenReturn(productCategories);
            when(productCategoryMapper.toResponseDto(productCategory)).thenReturn(responseDto);

            // Act
            List<ProductCategoryResponseDto> result = productCategoryService.findAllProductsByCategoryId(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());

            ProductCategoryResponseDto dto = result.get(0);
            assertEquals(1L, dto.productId());
            assertEquals("Smartphone Galaxy S23", dto.productName());
            assertEquals(1L, dto.categoryId());
            assertEquals("Eletrônicos", dto.categoryName());

            verify(productCategoryRepository).findAllProductsByCategoryId(1L);
        }

        @Test
        @DisplayName("✅ Deve retornar lista vazia quando categoria não tem produtos")
        void findAllProductsByCategoryId_WithNoProducts_ShouldReturnEmptyList() {
            // Arrange
            when(productCategoryRepository.findAllProductsByCategoryId(1L)).thenReturn(List.of());

            // Act
            List<ProductCategoryResponseDto> result = productCategoryService.findAllProductsByCategoryId(1L);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getAll - Listar todas as associações")
    class GetAllTests {

        @Test
        @DisplayName("✅ Deve retornar todas as associações")
        void getAll_ShouldReturnAllAssociations() {
            // Arrange
            List<ProductCategory> productCategories = List.of(productCategory);
            List<ProductCategoryResponseDto> responseDtos = List.of(responseDto);

            when(productCategoryRepository.findAll()).thenReturn(productCategories);
            when(productCategoryMapper.toResponseDto(productCategory)).thenReturn(responseDto);

            // Act
            List<ProductCategoryResponseDto> result = productCategoryService.getAll();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());

            verify(productCategoryRepository).findAll();
        }
    }

    @Nested
    @DisplayName("delete - Remover associação")
    class DeleteTests {

        @Test
        @DisplayName("✅ Deve remover associação existente")
        void delete_WithExistingId_ShouldDelete() {
            // Arrange
            when(productCategoryRepository.findById(1L)).thenReturn(Optional.of(productCategory));

            // Act
            productCategoryService.delete(1L);

            // Assert
            verify(productCategoryRepository).delete(productCategory);
        }

        @Test
        @DisplayName("❌ Deve lançar exceção quando associação não existe")
        void delete_WithNonExistingId_ShouldThrowException() {
            // Arrange
            when(productCategoryRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productCategoryService.delete(999L));

            assertEquals("No Product category found with the id 999", exception.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

            verify(productCategoryRepository, never()).delete(any());
        }
    }
}