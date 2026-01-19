package com.ecommerce.backend.service.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentCompletedEvent {

    private final Long orderId;
    private final Long paymentId;

   
}
