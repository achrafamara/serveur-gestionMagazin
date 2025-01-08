package com.modify.microserviceproducts.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int  id;
    private String titre ;
    private  String description;
    private String image;
    private Double prix ;



}
