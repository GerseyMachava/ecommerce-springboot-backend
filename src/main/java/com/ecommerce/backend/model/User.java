package com.ecommerce.backend.model;

import java.util.List;
import java.util.UUID;

import com.ecommerce.backend.model.base.BaseEntity;
import com.ecommerce.backend.model.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    private String name;
    private String email;
    private UUID password;
    private Role role;

    @OneToMany(mappedBy = "user")
    List<Address> adress;

    @OneToMany(mappedBy = "user")
    List<Orders> order;

}
