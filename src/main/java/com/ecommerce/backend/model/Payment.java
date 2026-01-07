package com.ecommerce.backend.model;

import java.time.LocalDate;


import com.ecommerce.backend.model.base.BaseEntity;
import com.ecommerce.backend.model.enums.PaymentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;

import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    private double amount;
    private String method;
    private LocalDate paidAt;
    private PaymentStatus status;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
    private Orders order;



}
