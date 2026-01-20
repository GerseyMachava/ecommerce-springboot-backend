package com.ecommerce.backend.model;



import com.ecommerce.backend.model.base.BaseEntity;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;

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

@Table(name = "productImages")
public class ProductImage extends BaseEntity {

    private String url;
    private String imageName;
    private  String imgType;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imgData;


    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;



    
  
}
