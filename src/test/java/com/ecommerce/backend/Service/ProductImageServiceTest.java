package com.ecommerce.backend.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.ecommerce.backend.dto.ResponseDto.ProductImageResponseDto;
import com.ecommerce.backend.mapper.ProductImageMapper;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.ProductImage;
import com.ecommerce.backend.repository.ProductImageRepository;
import com.ecommerce.backend.service.ProductImageService;
import com.ecommerce.backend.service.ProductService;
import com.ecommerce.backend.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class ProductImageServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private ProductImageMapper productImageMapper;

    @InjectMocks
    private ProductImageService productImageService;

    @TempDir
    Path tempDir;

    private Product product;
    private ProductImage productImage;
    private ProductImageResponseDto responseDto;
    private MockMultipartFile validJpegFile;
    private MockMultipartFile validPngFile;
    private MockMultipartFile invalidTypeFile;
    private MockMultipartFile oversizedFile;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .name("Smartphone Galaxy S23")
                .description("Smartphone top de linha")
                .price(new BigDecimal("4500.00"))
                .stockQuantity(10)
                .build();
        product.setId(1L); //

        productImage = ProductImage.builder()
                .product(product)
                .url("uploads/images/test-image.jpg")
                .build();
        productImage.setId(1L); // ✅ SET ID DEPOIS!

        responseDto = new ProductImageResponseDto(
                1L,
                "test-image.jpg",
                " image/jpeg",
                "uploads/images/test-image.jpg",
                1L// imageUrl
        );

        // Arquivos de teste
        validJpegFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "fake image content".getBytes());

        validPngFile = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "fake image content".getBytes());

        invalidTypeFile = new MockMultipartFile(
                "file",
                "test-image.gif",
                "image/gif",
                "fake image content".getBytes());

        oversizedFile = new MockMultipartFile(
                "file",
                "large-image.jpg",
                "image/jpeg",
                new byte[6 * 1024 * 1024] // 6MB
        );

        // Configurar uploadDir via Reflection (já que é @Value)
        ReflectionTestUtils.setField(productImageService, "uploadDir",
                tempDir.toString());
    }

    @Nested
    @DisplayName("saveImage - Upload de imagem")
    class SaveImageTests {

        @Test
        @DisplayName("✅ Deve salvar imagem JPEG com sucesso")
        void saveImage_WithValidJpeg_ShouldSaveImage() throws IOException {
            // Arrange
            when(productService.getProduct(1L)).thenReturn(product);
            when(productImageMapper.toEntity(eq(validJpegFile), eq(product), anyString()))
                    .thenReturn(productImage);
            when(productImageRepository.save(productImage)).thenReturn(productImage);
            when(productImageMapper.toResponseDto(productImage)).thenReturn(responseDto);

            // Act
            ProductImageResponseDto result = productImageService.saveImage(1L, validJpegFile);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.imgProductId());
            assertEquals(1L, result.productId());

            verify(productService).getProduct(1L);
            verify(productImageRepository).save(productImage);
            verify(productImageMapper).toResponseDto(productImage);
        }

        @Test
        @DisplayName("✅ Deve salvar imagem PNG com sucesso")
        void saveImage_WithValidPng_ShouldSaveImage() throws IOException {
            // Arrange
            when(productService.getProduct(1L)).thenReturn(product);
            when(productImageMapper.toEntity(eq(validPngFile), eq(product), anyString()))
                    .thenReturn(productImage);
            when(productImageRepository.save(productImage)).thenReturn(productImage);
            when(productImageMapper.toResponseDto(productImage)).thenReturn(responseDto);

            // Act
            ProductImageResponseDto result = productImageService.saveImage(1L, validPngFile);

            // Assert
            assertNotNull(result);
            verify(productImageRepository).save(productImage);
        }

        @Test
        @DisplayName("❌ Deve rejeitar arquivo que não é JPEG ou PNG")
        void saveImage_WithInvalidFileType_ShouldThrowException() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> productImageService.saveImage(1L, invalidTypeFile));

            assertEquals("Only JPEG or PNG images are allowed", exception.getMessage());

            verify(productService, never()).getProduct(any());
            verify(productImageRepository, never()).save(any());
        }

        @Test
        @DisplayName("📏 Deve rejeitar arquivo maior que 5MB")
        void saveImage_WithOversizedFile_ShouldThrowException() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> productImageService.saveImage(1L, oversizedFile));

            assertTrue(exception.getMessage().contains("File size exceeds maximum allowed size of 5MB"));

            verify(productService, never()).getProduct(any());
            verify(productImageRepository, never()).save(any());
        }

        @Test
        @DisplayName("❌ Deve lançar exceção quando produto não existe")
        void saveImage_WithNonExistingProduct_ShouldThrowException() {
            // Arrange
            when(productService.getProduct(999L)).thenThrow(
                    new BusinessException("No Product found with the id 999", HttpStatus.NOT_FOUND));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productImageService.saveImage(999L, validJpegFile));

            assertEquals("No Product found with the id 999", exception.getMessage());

            verify(productService).getProduct(999L);
            verify(productImageRepository, never()).save(any());

            // Verificar que nenhum arquivo foi salvo
            try (var files = Files.list(tempDir)) {
                assertEquals(0, files.count());
            } catch (IOException e) {
                fail("Não deveria ter arquivos no diretório");
            }
        }

        @Test
        @DisplayName("📁 Deve criar diretório de upload se não existir")
        void saveImage_WhenUploadDirDoesNotExist_ShouldCreateDirectory() throws IOException {
            // Arrange
            String nonExistentDir = tempDir.toString() + "/subdir/images";
            ReflectionTestUtils.setField(productImageService, "uploadDir", nonExistentDir);

            when(productService.getProduct(1L)).thenReturn(product);
            when(productImageMapper.toEntity(eq(validJpegFile), eq(product), anyString()))
                    .thenReturn(productImage);
            when(productImageRepository.save(productImage)).thenReturn(productImage);
            when(productImageMapper.toResponseDto(productImage)).thenReturn(responseDto);

            // Act
            ProductImageResponseDto result = productImageService.saveImage(1L, validJpegFile);

            // Assert
            assertNotNull(result);
            Path createdDir = Paths.get(nonExistentDir);
            assertTrue(Files.exists(createdDir));
            assertTrue(Files.isDirectory(createdDir));
        }

        @Test
        @DisplayName("🔄 Deve gerar nome único com UUID")
        void saveImage_ShouldGenerateUniqueFilename() throws IOException {
            // Arrange
            when(productService.getProduct(1L)).thenReturn(product);
            when(productImageMapper.toEntity(eq(validJpegFile), eq(product), anyString()))
                    .thenReturn(productImage);
            when(productImageRepository.save(productImage)).thenReturn(productImage);
            when(productImageMapper.toResponseDto(productImage)).thenReturn(responseDto);

            // Act
            productImageService.saveImage(1L, validJpegFile);

            // Assert
            verify(productImageMapper).toEntity(
                    eq(validJpegFile),
                    eq(product),
                    argThat(path -> {
                        // Verifica se o path contém UUID + nome original
                        String fileName = Paths.get(path).getFileName().toString();
                        return fileName.contains("_test-image.jpg") &&
                                fileName.length() > "test-image.jpg".length() + 36; // UUID tem 36 chars
                    }));
        }
    }

    @Nested
    @DisplayName("getAllProductImages - Listar imagens do produto")
    class GetAllProductImagesTests {

        @Test
        @DisplayName("✅ Deve retornar todas as imagens do produto")
        void getAllProductImages_WithExistingProduct_ShouldReturnImages() {
            // Arrange
            List<ProductImage> imageList = List.of(productImage);
            List<ProductImageResponseDto> responseList = List.of(responseDto);

            when(productImageRepository.findAllByProductId(1L)).thenReturn(imageList);
            when(productImageMapper.toResponseList(imageList)).thenReturn(responseList);

            // Act
            List<ProductImageResponseDto> result = productImageService.getAllProductImages(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).productId());

            verify(productImageRepository).findAllByProductId(1L);
            verify(productImageMapper).toResponseList(imageList);
        }

        @Test
        @DisplayName("✅ Deve retornar lista vazia quando produto não tem imagens")
        void getAllProductImages_WithNoImages_ShouldReturnEmptyList() {
            // Arrange
            when(productImageRepository.findAllByProductId(1L)).thenReturn(List.of());
            when(productImageMapper.toResponseList(List.of())).thenReturn(List.of());

            // Act
            List<ProductImageResponseDto> result = productImageService.getAllProductImages(1L);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(productImageRepository).findAllByProductId(1L);
        }

        @Test
        @DisplayName("✅ Deve funcionar mesmo com produto inexistente (retorna lista vazia)")
        void getAllProductImages_WithNonExistingProduct_ShouldReturnEmptyList() {
            // Arrange
            when(productImageRepository.findAllByProductId(999L)).thenReturn(List.of());
            when(productImageMapper.toResponseList(List.of())).thenReturn(List.of());

            // Act
            List<ProductImageResponseDto> result = productImageService.getAllProductImages(999L);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(productImageRepository).findAllByProductId(999L);
            // Não deve chamar productService.getProduct() - não é necessário
            verify(productService, never()).getProduct(any());
        }
    }
}