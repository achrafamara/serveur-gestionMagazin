package com.modify.microservicecommandes.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long id;
    private String titre;
    private String description;
    private String image;
    private Double prix;
}
