package com.ecommerce.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.ecommerce.backend.model.base.BaseEntity;
import com.ecommerce.backend.model.enums.PaymentMethod;
import com.ecommerce.backend.model.enums.PaymentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {
    @NotNull
    private BigDecimal amount;
    @NotBlank
    private String transactionReference;
    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
    private LocalDate paidAt;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    public void setPaidAt() {
        this.paidAt = LocalDate.now();
    }

    public void setTransactionReference() {
        this.transactionReference = UUID.randomUUID().toString();
    }
}
