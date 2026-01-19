package com.ecommerce.backend.service.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.Payment;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.PaymentRepository;
import com.ecommerce.backend.service.events.PaymentCompletedEvent;
import com.ecommerce.backend.shared.exception.BusinessException;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class OrderEventListener {

    private OrderRepository orderRepository;
    private PaymentRepository paymentRepository;

    @EventListener
    @Transactional
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new BusinessException("No order found whith the id: " + event.getOrderId(),
                        HttpStatus.NOT_FOUND));
        Payment payment = paymentRepository.findById(event.getPaymentId())
                .orElseThrow(() -> new BusinessException("No payment found whith the id: " + event.getPaymentId(),
                        HttpStatus.NOT_FOUND));
        order.setPayment(payment);
        orderRepository.save(order);
    }

}
