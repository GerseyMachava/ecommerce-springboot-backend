package com.ecommerce.backend.model;



import com.ecommerce.backend.model.base.BaseEntity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;

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

@Table(name = "productImages")
public class ProductImage extends BaseEntity {

    private String url;
    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

}
