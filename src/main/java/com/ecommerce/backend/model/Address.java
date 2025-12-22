package com.ecommerce.backend.model;

import com.ecommerce.backend.model.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Address extends BaseEntity{

    private String street;
    private String district;
    private String province;
    private String country;
    private String zipCode;
    
    

    

    @ManyToOne
    @JoinColumn(name="user_Id")
    private User user;





}
