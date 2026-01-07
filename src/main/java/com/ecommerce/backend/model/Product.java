package com.ecommerce.backend.model;

import java.math.BigDecimal;
import java.util.List;

import com.ecommerce.backend.model.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "products")
public class Product extends BaseEntity{

    @NotBlank(message = "The name can not be null")
    private String name;
    @NotBlank(message = "The Description can not be null")
    private String description;
    @NotNull(message = "The price can not be null")
    private BigDecimal price;
    @NotNull(message = "The stock Quantity can not be null")
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
