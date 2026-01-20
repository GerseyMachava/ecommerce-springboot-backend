package com.ecommerce.backend.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.backend.dto.ResponseDto.ProductImageResponseDto;
import com.ecommerce.backend.service.ProductImageService;
import com.ecommerce.backend.shared.apiResponse.ApiResponse;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/image")
@AllArgsConstructor
public class ProductImageController {
    private ProductImageService service;

    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping()
    public ResponseEntity<ApiResponse<ProductImageResponseDto>> uploadImage(@RequestParam Long productId,
            MultipartFile file) {

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.success("Product image added", service.saveImage(productId, file), HttpStatus.CREATED));
        } catch (IOException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Error uploading image", HttpStatus.INTERNAL_SERVER_ERROR, e));
        }

    }

    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<List<ProductImageResponseDto>>> getProductImage(@PathVariable Long productId) {
        List<ProductImageResponseDto> data = service.getAllProductImages(productId);
        String message = data.isEmpty() ? "No image found for this product" : "All images fetched";

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(message, service.getAllProductImages(productId), HttpStatus.OK));

    }

}
