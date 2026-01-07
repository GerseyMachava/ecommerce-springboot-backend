package com.ecommerce.backend.model;

import com.ecommerce.backend.model.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "productCategories")
public class ProductCategory extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_Id")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "category_Id")
    private Category category;

}
