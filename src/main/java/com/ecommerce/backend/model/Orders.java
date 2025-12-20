package com.ecommerce.backend.model;

import java.util.List;

import com.ecommerce.backend.model.base.BaseEntity;
import com.ecommerce.backend.model.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "orders")
public class Orders extends BaseEntity{

    private double totaAmount;
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "user_Id")
    private User user;

    @OneToMany(mappedBy = "order")
    List<OrderItem> orderItem;


     @OneToOne
     @JoinColumn(name = "payment_id")
     @MapsId
     private Payment payment;

}
