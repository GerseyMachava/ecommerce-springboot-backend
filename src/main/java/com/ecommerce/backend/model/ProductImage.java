package com.ecommerce.backend.model;


import java.util.List;

import com.ecommerce.backend.model.base.BaseEntity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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


public class ProductImage extends BaseEntity {

    private String url;
    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

}
