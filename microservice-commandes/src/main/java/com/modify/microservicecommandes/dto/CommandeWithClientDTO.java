package com.modify.microservicecommandes.dto;


import com.modify.microservicecommandes.model.Client;
import com.modify.microservicecommandes.model.Commande;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandeWithClientDTO {
    private Commande commande;
    private Client client;
}
