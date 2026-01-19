package com.ecommerce.backend.dto.ResponseDto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ecommerce.backend.model.enums.PaymentMethod;
import com.ecommerce.backend.model.enums.PaymentStatus;

public record PaymentResponseDto(
    Long id,
    String transactionReference,
    PaymentMethod paymentMethod,
    LocalDate paidAt,
    BigDecimal amount,
    PaymentStatus status,  
    Long orderid,
    String useEmail



) {

}
