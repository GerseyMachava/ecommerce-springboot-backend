package com.ecommerce.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.backend.dto.ResponseDto.PaymentResponseDto;
import com.ecommerce.backend.dto.requestDto.PaymentRequestDto;
import com.ecommerce.backend.dto.requestDto.PaymentStatusUpdateRequest;
import com.ecommerce.backend.service.PaymentService;
import com.ecommerce.backend.shared.apiResponse.ApiResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RequestMapping("/payment")
@RestController
public class PaymentController {

    private PaymentService paymentService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponseDto>>> index() {
        List<PaymentResponseDto> data = paymentService.findAllPayments();
        String message = data.isEmpty() ? "No payments found" : "All payments fetched";
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(message, data, HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponseDto>> create(@RequestBody @Valid PaymentRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Payment Created", paymentService.createPayment(requestDto), HttpStatus.CREATED));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> findByid(@PathVariable(name = "id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("Payment founded", paymentService.findById(id), HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/statusUpdate")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> tooglePaymentStatus(
            @RequestBody @Valid PaymentStatusUpdateRequest requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("Payment updated", paymentService.togglePaymentStatus(requestDto), HttpStatus.OK));

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> delete(@PathVariable(name = "id") Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("Payment deleted", null, HttpStatus.OK));

    }

}
