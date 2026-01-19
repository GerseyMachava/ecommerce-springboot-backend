package com.ecommerce.backend.dto.requestDto;

import com.ecommerce.backend.model.enums.PaymentStatus;

public record PaymentStatusUpdateRequest(
    long paymentId,
    PaymentStatus status
) {

}
