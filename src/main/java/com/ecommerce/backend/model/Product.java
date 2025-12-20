package com.ecommerce.backend.model;

import java.util.List;

import com.ecommerce.backend.model.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@Entity
public class Product extends BaseEntity{

    private String name;
    private String descripction;
    private double price;
    private int stockQuantity;

    @OneToMany(mappedBy = "product")
    List<CartItem> cartItem;

    @OneToMany(mappedBy = "product")
    List<ProductCategory> productCategory;

    @OneToMany(mappedBy = "product")
    List<ProductImage> productImage;

    @OneToMany(mappedBy = "product")
    List<OrderItem> orderItem;

}
