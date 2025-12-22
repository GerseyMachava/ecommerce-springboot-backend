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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
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
