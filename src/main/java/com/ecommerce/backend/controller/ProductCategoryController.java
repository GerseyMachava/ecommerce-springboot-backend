package com.ecommerce.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.backend.dto.ResponseDto.ProductCategoryResponseDto;
import com.ecommerce.backend.dto.requestDto.ProductCategoryRequestDto;
import com.ecommerce.backend.service.ProductCategoryService;
import com.ecommerce.backend.shared.apiResponse.ApiResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RequestMapping("/productsCategories")
@RestController
@AllArgsConstructor
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ProductCategoryResponseDto>>> index() {
        List<ProductCategoryResponseDto> data = productCategoryService.getAll();
        String message = data.isEmpty() ? "No products founded" : "All products fetched";

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(message, data, HttpStatus.OK));
    }

    @GetMapping("/findAllByCategoryId/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductCategoryResponseDto>>> findAllProductsByCategoryId(
            @PathVariable(name = "categoryId") Long categoryId) {
        List<ProductCategoryResponseDto> data = productCategoryService.findAllProductsByCategoryId(categoryId);
        String message = data.isEmpty() ? "No products founded" : "All products fetched";

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(message, data, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping()
    public ResponseEntity<ApiResponse<ProductCategoryResponseDto>> create(
            @RequestBody @Valid ProductCategoryRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Product Category Created",
                productCategoryService.create(requestDto), HttpStatus.CREATED));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCategoryResponseDto>> update(
            @RequestBody @Valid ProductCategoryRequestDto requestDto,
            @PathVariable(name = "id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Product Category Created",
                productCategoryService.update(id, requestDto), HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCategoryResponseDto>> getById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("Product Category Founded", productCategoryService.findById(id), HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCategoryResponseDto>> delete(@PathVariable(name = "id") Long id) {
        productCategoryService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                ApiResponse.success("Product Category deleted", null, HttpStatus.NO_CONTENT));
    }

}
