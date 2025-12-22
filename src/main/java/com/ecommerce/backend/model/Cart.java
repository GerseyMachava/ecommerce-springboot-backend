package com.ecommerce.backend.model;

import java.util.List;

import com.ecommerce.backend.model.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity

public class Cart extends BaseEntity {

    @OneToOne
    @JoinColumn(name="user_Id")
    @MapsId
    private User user;

    @OneToMany(mappedBy = "cart")
    List<CartItem> cartItem;
}
