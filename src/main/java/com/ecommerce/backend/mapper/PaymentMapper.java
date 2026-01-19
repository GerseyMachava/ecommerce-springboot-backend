package com.ecommerce.backend.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ecommerce.backend.dto.ResponseDto.PaymentResponseDto;
import com.ecommerce.backend.dto.requestDto.PaymentRequestDto;
import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.Payment;
import com.ecommerce.backend.model.enums.PaymentStatus;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PaymentMapper {

    public PaymentResponseDto toResponseDto(Payment payment) {
        PaymentResponseDto responseDto = new PaymentResponseDto(
                payment.getId(),
                payment.getTransactionReference(),
                payment.getMethod(),
                payment.getPaidAt(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getOrder().getId(),
                payment.getOrder().getUser().getEmail());
        return responseDto;

    }

    public Payment toEntity(PaymentRequestDto requestDto, Order order) {
        Payment newPayment = new Payment();
        newPayment.setAmount(order.getTotalAmount());
        newPayment.setTransactionReference();
        newPayment.setPaidAt();
        newPayment.setMethod(requestDto.paymentMethod());
        newPayment.setStatus(PaymentStatus.PENDING);
        newPayment.setOrder(order);
        return newPayment;

    }

    public List<PaymentResponseDto> toResponseList(List<Payment> paymentList) {
        return paymentList.stream().map(
                payment -> toResponseDto(payment)).toList();
    }

}
