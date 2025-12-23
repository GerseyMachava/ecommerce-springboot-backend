package com.ecommerce.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.backend.dto.ResponseDto.ProductResponseDto;
import com.ecommerce.backend.dto.requestDto.ProductRequestDto;
import com.ecommerce.backend.service.ProductService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody @Valid ProductRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.createProduct(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> findProductById(@PathVariable(name = "id") long id) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findProductById(id));
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
