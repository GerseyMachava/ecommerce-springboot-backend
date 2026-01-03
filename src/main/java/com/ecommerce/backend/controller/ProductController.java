package com.ecommerce.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.backend.dto.ResponseDto.ProductResponseDto;
import com.ecommerce.backend.dto.ResponseDto.ProductResponseListDto;
import com.ecommerce.backend.dto.requestDto.ProductRequestDto;
import com.ecommerce.backend.model.enums.ApiStatus;
import com.ecommerce.backend.service.ProductService;
import com.ecommerce.backend.shared.apiResponse.ApiResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

        @GetMapping()
        public ResponseEntity<ApiResponse<ProductResponseListDto>> index(
                        @PageableDefault(sort = "name", direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
                var data = productService.getAllProducts(pageable);
                return ResponseEntity.status(
                                HttpStatus.OK).body(
                                                ApiResponse.success("All products fetched successfuly",
                                                                data,
                                                                HttpStatus.OK));

        }

        @PreAuthorize("hasRole('ROLE_ADMIN')")
        @PostMapping()
        public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(
                        @RequestBody @Valid ProductRequestDto requestDto) {
                return ResponseEntity.status(
                                HttpStatus.CREATED).body(
                                                ApiResponse.success("Product Created",
                                                                productService.createProduct(requestDto),
                                                                HttpStatus.CREATED));

        }

        @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_CUSTOMER' )")
        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<ProductResponseDto>> findProductById(@PathVariable(name = "id") long id) {
                return ResponseEntity.status(HttpStatus.OK).body(
                                ApiResponse.success("Product found successfully", productService.findProductById(id),
                                                HttpStatus.OK));
        }

        @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_CUSTOMER' )")
        @GetMapping("/search")
        public ResponseEntity<ApiResponse<ProductResponseListDto>> search(
                        @RequestParam(required = false) String name,
                        @RequestParam(required = false) Double maxPrice,
                        @PageableDefault(sort = "name") Pageable pageable) {
                var result = productService.searchProducts(name, maxPrice, pageable);
                return ResponseEntity.status(HttpStatus.OK).body(
                                ApiResponse.success("Search completed",
                                                result,
                                                HttpStatus.OK));
        }

        @PreAuthorize("hasRole('ROLE_ADMIN')")
        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(
                        @RequestBody @Valid ProductRequestDto requestDto,
                        @PathVariable(name = "id") long id) {
                return ResponseEntity.status(
                                HttpStatus.OK).body(
                                                ApiResponse.success("Product updated",
                                                                productService.updateProduct(requestDto, id),
                                                                HttpStatus.OK));
        }

        @PreAuthorize("hasRole('ROLE_ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<?>> deleteMapping(@PathVariable(name = "id") long id) {
                productService.deleteProduct(id);
                return ResponseEntity.status(
                                HttpStatus.OK).body(
                                                ApiResponse.success("Product deleted", null,
                                                                HttpStatus.OK));

        }

}
