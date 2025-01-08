package com.modify.microservicecommandes.dto;

import com.modify.microservicecommandes.model.Commande;
import com.modify.microservicecommandes.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandeWithProductDTO {
    private Commande commande;
    private Product produit;
}
