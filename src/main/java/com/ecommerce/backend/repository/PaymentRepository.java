package com.ecommerce.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.backend.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Boolean existsByOrderId(Long orderId);

}
