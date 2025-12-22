package com.ecommerce.backend.model;

import com.ecommerce.backend.model.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class OrderItem extends BaseEntity{

    private int quantity;
    private double unitPrice;


    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
