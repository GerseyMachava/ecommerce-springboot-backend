package com.ecommerce.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.backend.dto.ResponseDto.CategoryResponseDto;
import com.ecommerce.backend.dto.requestDto.CategoryRequestDto;
import com.ecommerce.backend.service.CategoryService;
import com.ecommerce.backend.shared.apiResponse.ApiResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/category")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> index() {
        List<CategoryResponseDto> data = categoryService.getAllCategories();
        String message = data.isEmpty() ? "No categories founded" : "Categories fetched";
        return ResponseEntity.status(
                HttpStatus.OK).body(ApiResponse.success(message, data, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping()
    public ResponseEntity<ApiResponse<CategoryResponseDto>> create(@RequestBody @Valid CategoryRequestDto request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Category created", categoryService.createCategory(request), HttpStatus.CREATED));
    }

    @GetMapping("{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> getById(@PathVariable(name = "categoryId") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("Category founded", categoryService.findById(id), HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> update(@PathVariable Long id,
            @RequestBody CategoryRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("Category updated", categoryService.updateCategory(id, requestDto), HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("Category updated", null, HttpStatus.OK));
    }

}
