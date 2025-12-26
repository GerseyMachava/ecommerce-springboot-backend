package com.ecommerce.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.backend.dto.ResponseDto.ProductResponseDto;
import com.ecommerce.backend.dto.requestDto.ProductRequestDto;
import com.ecommerce.backend.model.enums.ApiStatus;
import com.ecommerce.backend.service.ProductService;
import com.ecommerce.backend.shared.apiResponse.ApiResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping()
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(
            @RequestBody @Valid ProductRequestDto requestDto) {
        return ResponseEntity.status(
                HttpStatus.CREATED).body(
                        ApiResponse.<ProductResponseDto>builder()
                                .status(ApiStatus.SUCCESS)
                                .message("Product Created Successfuly!")
                                .data(productService.createProduct(requestDto))
                                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> findProductById(@PathVariable(name = "id") long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(true, "Product found successfully", productService.findProductById(id),
                        HttpStatus.OK));
    }

    @GetMapping("/getByName/{name}")
    public ResponseEntity<ProductResponseDto> findProductByName(@PathVariable(name = "name") String name) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findProductByName(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@RequestBody @Valid ProductRequestDto requestDto,
            @PathVariable(name = "id") long id) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(requestDto, id));
    }

    @DeleteMapping("/{id}")
    public void deleteMapping(@PathVariable(name = "id") long id) {
        productService.deleteProduct(id);
    }

}
