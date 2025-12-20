package com.ecommerce.backend.model;

import java.time.LocalDate;
import java.util.List;

import com.ecommerce.backend.model.base.BaseEntity;
import com.ecommerce.backend.model.enums.PaymentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@Entity
public class Payment extends BaseEntity {

    private double amount;
    private String method;
    private LocalDate paidAt;
    private PaymentStatus status;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
    private Orders order;



}
