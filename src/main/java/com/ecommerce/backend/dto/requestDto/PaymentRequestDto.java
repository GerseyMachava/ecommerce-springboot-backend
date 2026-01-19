package com.ecommerce.backend.dto.requestDto;

import java.math.BigDecimal;

import com.ecommerce.backend.model.enums.PaymentMethod;

import jakarta.validation.constraints.NotNull;

public record PaymentRequestDto(
        @NotNull Long orderId,
        @NotNull PaymentMethod paymentMethod,
        BigDecimal amount

) {

}
