package com.ecommerce.backend.model;

import com.ecommerce.backend.model.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "address")
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
