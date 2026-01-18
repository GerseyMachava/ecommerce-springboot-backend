package com.ecommerce.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.backend.dto.ResponseDto.OrderResponseDto;
import com.ecommerce.backend.dto.requestDto.OrderStatusRequestDto;
import com.ecommerce.backend.service.OrderService;
import com.ecommerce.backend.shared.apiResponse.ApiResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RequestMapping("/order")
@RestController
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> UserOrderList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Orders Fetched!", orderService.getAuthUserOrders(), HttpStatus.OK));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<OrderResponseDto>> create() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order Created", orderService.createOrderFromCart(), HttpStatus.CREATED));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/toogleStatus")
    public ResponseEntity<ApiResponse<OrderResponseDto>> toogleStatus(
            @RequestBody @Valid OrderStatusRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Order status updated", orderService.toogleOrderStatus(requestDto),
                        HttpStatus.OK));
    }

}
