package com.modify.microservicecommandes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int  id;
    private  String description;
    private int quantite;
    private LocalDate date ;
    private Double montant ;
    @ElementCollection
    private List<Long> idProduits; // pour le deuxieme cas d'utilisation
    private Long idClient;

    @Transient
    private Client client;   // Non persistant, pour stocker temporairement les informations du client
    @Transient
    private List<Product> produits;
}
