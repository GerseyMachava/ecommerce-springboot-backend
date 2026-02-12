package com.ecommerce.backend.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

import com.ecommerce.backend.dto.ResponseDto.CategoryResponseDto;
import com.ecommerce.backend.dto.requestDto.CategoryRequestDto;
import com.ecommerce.backend.mapper.CategoryMapper;
import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.service.CategoryService;
import com.ecommerce.backend.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category electronicsCategory;
    private Category smartphonesCategory;
    private CategoryRequestDto categoryRequestDto;
    private CategoryResponseDto categoryResponseDto;
    private CategoryRequestDto subCategoryRequestDto;
    private CategoryResponseDto subCategoryResponseDto;

    @BeforeEach
    void setUp() {
       
        electronicsCategory = Category.builder()
                .name("Eletrônicos")
                .description("Produtos eletrônicos em geral")
                .parentCategory(null)
                .build();
        electronicsCategory.setId(1L);
       
        smartphonesCategory = Category.builder()
                .name("Smartphones")
                .description("Telefones celulares")
                .parentCategory(electronicsCategory)
                .build();
        smartphonesCategory.setId(2L);

        
        categoryRequestDto = new CategoryRequestDto(
                "Eletrônicos",
                "Produtos eletrônicos em geral",
                null 
        );

        categoryResponseDto = new CategoryResponseDto(
                1L,
                "Eletrônicos",
                "Produtos eletrônicos em geral",
                null 
        );
        // Setup Request DTO para subcategoria
        subCategoryRequestDto = new CategoryRequestDto(
                "Smartphones",
                "Telefones celulares",
                1L // ID da categoria pai
        );

        // Setup Response DTO para subcategoria
        subCategoryResponseDto = new CategoryResponseDto(
                2L,
                "Smartphones",
                "Telefones celulares",
                1L // ID da categoria pai
        );
    }

    @Nested
    @DisplayName("Testes para createCategory")
    class CreateCategoryTests {

        @Test
        @DisplayName("Deve criar categoria com sucesso sem categoria pai")
        void createCategory_WithValidDataAndNoParent_ShouldCreateCategory() {
            // Arrange
            when(categoryRepository.existsByName("Eletrônicos")).thenReturn(false);
            when(categoryMapper.toEntity(categoryRequestDto, null)).thenReturn(electronicsCategory);
            when(categoryRepository.save(electronicsCategory)).thenReturn(electronicsCategory);
            when(categoryMapper.toResponseDto(electronicsCategory)).thenReturn(categoryResponseDto);

            // Act
            CategoryResponseDto result = categoryService.createCategory(categoryRequestDto);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("Eletrônicos", result.name());
            assertNull(result.parentCategoryId());

            verify(categoryRepository).existsByName("Eletrônicos");
            verify(categoryMapper).toEntity(categoryRequestDto, null);
            verify(categoryRepository).save(electronicsCategory);
            verify(categoryMapper).toResponseDto(electronicsCategory);
            verify(categoryRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("Deve criar subcategoria com sucesso")
        void createCategory_WithValidParentCategory_ShouldCreateSubCategory() {
            // Arrange
            when(categoryRepository.existsByName("Smartphones")).thenReturn(false);
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronicsCategory));
            when(categoryMapper.toEntity(subCategoryRequestDto, electronicsCategory)).thenReturn(smartphonesCategory);
            when(categoryRepository.save(smartphonesCategory)).thenReturn(smartphonesCategory);
            when(categoryMapper.toResponseDto(smartphonesCategory)).thenReturn(subCategoryResponseDto);

            // Act
            CategoryResponseDto result = categoryService.createCategory(subCategoryRequestDto);

            // Assert
            assertNotNull(result);
            assertEquals(2L, result.id());
            assertEquals("Smartphones", result.name());
            assertEquals(1L, result.parentCategoryId());

            verify(categoryRepository).existsByName("Smartphones");
            verify(categoryRepository).findById(1L);
            verify(categoryMapper).toEntity(subCategoryRequestDto, electronicsCategory);
            verify(categoryRepository).save(smartphonesCategory);
        }

        @Test
        @DisplayName("Deve lançar exceção quando nome da categoria já existe")
        void createCategory_WithDuplicateName_ShouldThrowBusinessException() {
            // Arrange
            when(categoryRepository.existsByName("Eletrônicos")).thenReturn(true);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryService.createCategory(categoryRequestDto));

            assertEquals("category with the name Eletrônicos Already exists", exception.getMessage());
            assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());

            verify(categoryRepository).existsByName("Eletrônicos");
            verify(categoryRepository, never()).findById(anyLong());
            verify(categoryRepository, never()).save(any(Category.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando categoria pai não existe")
        void createCategory_WithNonExistingParentCategory_ShouldThrowBusinessException() {
            // Arrange
            when(categoryRepository.existsByName("Smartphones")).thenReturn(false);
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            CategoryRequestDto invalidParentDto = new CategoryRequestDto(
                    "Smartphones",
                    "Telefones",
                    999L);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryService.createCategory(invalidParentDto));

            assertEquals("No Parent Category found with the id 999", exception.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

            verify(categoryRepository).existsByName("Smartphones");
            verify(categoryRepository).findById(999L);
            verify(categoryRepository, never()).save(any(Category.class));
        }
    }

    @Nested
    @DisplayName("Testes para getAllCategories")
    class GetAllCategoriesTests {

        @Test
        @DisplayName("Deve retornar lista de todas as categorias")
        void getAllCategories_ShouldReturnAllCategories() {
            // Arrange
            List<Category> categories = List.of(electronicsCategory, smartphonesCategory);
            List<CategoryResponseDto> responseDtos = List.of(categoryResponseDto, subCategoryResponseDto);

            when(categoryRepository.findAll()).thenReturn(categories);
            when(categoryMapper.toResponseDto(electronicsCategory)).thenReturn(categoryResponseDto);
            when(categoryMapper.toResponseDto(smartphonesCategory)).thenReturn(subCategoryResponseDto);

            // Act
            List<CategoryResponseDto> result = categoryService.getAllCategories();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Eletrônicos", result.get(0).name());
            assertEquals("Smartphones", result.get(1).name());

            verify(categoryRepository).findAll();
            verify(categoryMapper, times(2)).toResponseDto(any(Category.class));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há categorias")
        void getAllCategories_WhenNoCategories_ShouldReturnEmptyList() {
            // Arrange
            when(categoryRepository.findAll()).thenReturn(List.of());

            // Act
            List<CategoryResponseDto> result = categoryService.getAllCategories();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(categoryRepository).findAll();
            verify(categoryMapper, never()).toResponseDto(any(Category.class));
        }
    }

    @Nested
    @DisplayName("Testes para findById")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar categoria quando ID existe")
        void findById_WithExistingId_ShouldReturnCategory() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronicsCategory));
            when(categoryMapper.toResponseDto(electronicsCategory)).thenReturn(categoryResponseDto);

            // Act
            CategoryResponseDto result = categoryService.findById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("Eletrônicos", result.name());

            verify(categoryRepository).findById(1L);
            verify(categoryMapper).toResponseDto(electronicsCategory);
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID não existe")
        void findById_WithNonExistingId_ShouldThrowBusinessException() {
            // Arrange
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryService.findById(999L));

            assertEquals("No Category found with the id 999", exception.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

            verify(categoryRepository).findById(999L);
            verify(categoryMapper, never()).toResponseDto(any(Category.class));
        }
    }

    @Nested
    @DisplayName("Testes para updateCategory")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Deve atualizar categoria com sucesso")
        void updateCategory_WithValidData_ShouldUpdateCategory() {
            // Arrange
            CategoryRequestDto updateDto = new CategoryRequestDto(
                    "Eletrônicos e Tecnologia",
                    "Todos os produtos eletrônicos",
                    null);

            CategoryResponseDto updatedResponseDto = new CategoryResponseDto(
                    1L,
                    "Eletrônicos e Tecnologia",
                    "Todos os produtos eletrônicos",
                    null);

            Category updatedCategory = Category.builder()
                    .name("Eletrônicos e Tecnologia")
                    .description("Todos os produtos eletrônicos")
                    .parentCategory(null)
                    .build();
            updatedCategory.setId(1L);

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronicsCategory));
            when(categoryRepository.existsByNameAndIdNot("Eletrônicos e Tecnologia", 1L)).thenReturn(false);
            when(categoryMapper.updateEntity(electronicsCategory, updateDto, null)).thenReturn(updatedCategory);
            when(categoryMapper.toResponseDto(updatedCategory)).thenReturn(updatedResponseDto);

            // Act
            CategoryResponseDto result = categoryService.updateCategory(1L, updateDto);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("Eletrônicos e Tecnologia", result.name());
            assertEquals("Todos os produtos eletrônicos", result.description());

            verify(categoryRepository).findById(1L);
            verify(categoryRepository).existsByNameAndIdNot("Eletrônicos e Tecnologia", 1L);
            verify(categoryMapper).updateEntity(electronicsCategory, updateDto, null);
            verify(categoryMapper).toResponseDto(updatedCategory);
            verify(categoryRepository, never()).save(any(Category.class)); 
        }

        @Test
        @DisplayName("Deve atualizar categoria com nova categoria pai")
        void updateCategory_WithNewParentCategory_ShouldUpdateCategory() {
            // Arrange
            Category newParentCategory = Category.builder()
                    .name("Tecnologia")
                    .build();
            newParentCategory.setId(3L);

            CategoryRequestDto updateWithParentDto = new CategoryRequestDto(
                    "Smartphones",
                    "Telefones inteligentes",
                    3L);

            Category updatedCategory = Category.builder()
                    .name("Smartphones")
                    .description("Telefones inteligentes")
                    .parentCategory(newParentCategory)
                    .build();
                updatedCategory.setId(2L);

            CategoryResponseDto updatedResponseDto = new CategoryResponseDto(
                    2L,
                    "Smartphones",
                    "Telefones inteligentes",
                    3L);

            when(categoryRepository.findById(2L)).thenReturn(Optional.of(smartphonesCategory));
            when(categoryRepository.existsByNameAndIdNot("Smartphones", 2L)).thenReturn(false);
            when(categoryRepository.findById(3L)).thenReturn(Optional.of(newParentCategory));
            when(categoryMapper.updateEntity(smartphonesCategory, updateWithParentDto, newParentCategory))
                    .thenReturn(updatedCategory);
            when(categoryMapper.toResponseDto(updatedCategory)).thenReturn(updatedResponseDto);

            // Act
            CategoryResponseDto result = categoryService.updateCategory(2L, updateWithParentDto);

            // Assert
            assertNotNull(result);
            assertEquals(2L, result.id());
            assertEquals(3L, result.parentCategoryId());

            verify(categoryRepository).findById(2L);
            verify(categoryRepository).existsByNameAndIdNot("Smartphones", 2L);
            verify(categoryRepository).findById(3L);
        }

        @Test
        @DisplayName("Deve lançar exceção quando categoria a ser atualizada não existe")
        void updateCategory_WithNonExistingCategory_ShouldThrowBusinessException() {
            // Arrange
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryService.updateCategory(999L, categoryRequestDto));

            assertEquals("No Category found with the id 999", exception.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

            verify(categoryRepository).findById(999L);
            verify(categoryRepository, never()).existsByNameAndIdNot(anyString(), anyLong());
        }

        @Test
        @DisplayName("Deve lançar exceção quando nome já existe em outra categoria")
        void updateCategory_WithDuplicateName_ShouldThrowBusinessException() {
            // Arrange
            CategoryRequestDto duplicateNameDto = new CategoryRequestDto(
                    "Eletrônicos", // Nome já existe em outra categoria
                    "Descrição",
                    null);

            when(categoryRepository.findById(2L)).thenReturn(Optional.of(smartphonesCategory));
            when(categoryRepository.existsByNameAndIdNot("Eletrônicos", 2L)).thenReturn(true);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryService.updateCategory(2L, duplicateNameDto));

            assertEquals("category with the name Eletrônicos Already exists", exception.getMessage());
            assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());

            verify(categoryRepository).findById(2L);
            verify(categoryRepository).existsByNameAndIdNot("Eletrônicos", 2L);
        }

        @Test
        @DisplayName("Deve permitir atualização mantendo o mesmo nome")
        void updateCategory_WithSameName_ShouldUpdateCategory() {
            // Arrange
            CategoryRequestDto sameNameDto = new CategoryRequestDto(
                    "Smartphones", // Mesmo nome
                    "Nova descrição",
                    1L);

            when(categoryRepository.findById(2L)).thenReturn(Optional.of(smartphonesCategory));
            when(categoryRepository.existsByNameAndIdNot("Smartphones", 2L)).thenReturn(false);
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronicsCategory));
            when(categoryMapper.updateEntity(smartphonesCategory, sameNameDto, electronicsCategory))
                    .thenReturn(smartphonesCategory);
            when(categoryMapper.toResponseDto(smartphonesCategory)).thenReturn(subCategoryResponseDto);

            // Act
            CategoryResponseDto result = categoryService.updateCategory(2L, sameNameDto);

            // Assert
            assertNotNull(result);
            assertEquals(2L, result.id());
            assertEquals("Smartphones", result.name());

            verify(categoryRepository).existsByNameAndIdNot("Smartphones", 2L);
            // Não deve lançar exceção de nome duplicado
        }
    }

    @Nested
    @DisplayName("Testes para deleteCategory")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Deve deletar categoria existente")
        void deleteCategory_WithExistingId_ShouldDeleteCategory() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronicsCategory));
            doNothing().when(categoryRepository).delete(electronicsCategory);

            // Act
            categoryService.deleteCategory(1L);

            // Assert
            verify(categoryRepository).findById(1L);
            verify(categoryRepository).delete(electronicsCategory);
        }

        @Test
        @DisplayName("Deve lançar exceção quando categoria não existe")
        void deleteCategory_WithNonExistingId_ShouldThrowBusinessException() {
            // Arrange
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryService.deleteCategory(999L));

            assertEquals("No category found with the id 999", exception.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

            verify(categoryRepository).findById(999L);
            verify(categoryRepository, never()).delete(any(Category.class));
        }
    }

    @Nested
    @DisplayName("Testes para getCategory")
    class GetCategoryTests {

        @Test
        @DisplayName("Deve retornar categoria quando ID existe")
        void getCategory_WithExistingId_ShouldReturnCategory() {
            // Arrange
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronicsCategory));

            // Act
            Category result = categoryService.getCategory(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Eletrônicos", result.getName());

            verify(categoryRepository).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID não existe")
        void getCategory_WithNonExistingId_ShouldThrowBusinessException() {
            // Arrange
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryService.getCategory(999L));

            assertEquals("No  Category found with the id 999", exception.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

            verify(categoryRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("Testes de integridade referencial")
    class ReferentialIntegrityTests {

        @Test
        @DisplayName("Não deve permitir criar categoria com parentCategoryId igual ao próprio ID")
        void createCategory_WithSelfAsParent_ShouldAllowOrHandle() {
            // Este é um caso que o código atual NÃO trata
            // O banco de dados pode permitir, mas isso criaria um ciclo

            CategoryRequestDto selfParentDto = new CategoryRequestDto(
                    "Ciclica",
                    "Descrição",
                    1L // Supondo que o ID ainda não existe
            );

            when(categoryRepository.existsByName("Ciclica")).thenReturn(false);
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(BusinessException.class,
                    () -> categoryService.createCategory(selfParentDto));
        }

        @Test
        @DisplayName("Deve deletar categoria mesmo com subcategorias?")
        void deleteCategory_WithSubCategories_ShouldHandleCorrectly() {
            // O código atual não trata subcategorias
            // Ao deletar uma categoria pai, as subcategorias ficam órfãs

            // Este teste expõe uma limitação
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronicsCategory));
            doNothing().when(categoryRepository).delete(electronicsCategory);

            // Act
            categoryService.deleteCategory(1L);

            // Assert
            verify(categoryRepository).delete(electronicsCategory);
            // As subcategorias ainda existirão com parentCategory null?
            // Depende da configuração do JPA
        }
    }
}