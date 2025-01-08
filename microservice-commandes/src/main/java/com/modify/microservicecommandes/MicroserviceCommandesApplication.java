package com.modify.microservicecommandes;

import com.modify.microservicecommandes.dao.CommandeDao;
import com.modify.microservicecommandes.model.Client;
import com.modify.microservicecommandes.model.Commande;
import com.modify.microservicecommandes.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
public class MicroserviceCommandesApplication implements CommandLineRunner {

    @Autowired
    CommandeDao commandeDao;
    public static void main(String[] args) {
        SpringApplication.run(MicroserviceCommandesApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        Client client1 = new Client();
        client1.setIdClient(1L);
        client1.setNom("John Doe");
        client1.setEmail("johndoe@example.com");
        client1.setAdresse("123 Main St");
        client1.setTelephone("1234567890");


        Client client2 = new Client();
        client2.setIdClient(2L);
        client2.setNom("Jane Doe");
        client2.setEmail("janedoe@example.com");
        client2.setAdresse("456 Elm St");
        client2.setTelephone("9876543210");


        // Insertion de données dans la table Produit
        Product produit1 = new Product();
        produit1.setId(1L);
        produit1.setTitre("Produit 1");
        produit1.setDescription("Description du produit 1");
        produit1.setImage("image_url_1");
        produit1.setPrix(20.0);


        Product produit2 = new Product();
        produit2.setId(2L);
        produit2.setTitre("Produit 2");
        produit2.setDescription("Description du produit 2");
        produit2.setImage("image_url_2");
        produit2.setPrix(40.0);

        /*Commande commande1 = new Commande();
        commande1.setDescription("Commande 1 pour John");
        commande1.setQuantite(2);
        commande1.setDate(LocalDate.now());
        commande1.setMontant(40.0);
        commande1.setIdProduit(produit1.getId()); // Associer le produit 1 à la commande
        commande1.setIdClient(client1.getIdClient()); // Associer le client 1 à la commande
        commandeDao.save(commande1);

        Commande commande2 = new Commande();
        commande2.setDescription("Commande 2 pour Jane");
        commande2.setQuantite(1);
        commande2.setDate(LocalDate.now());
        commande2.setMontant(40.0);
        commande2.setIdProduit(produit2.getId()); // Associer le produit 2 à la commande
        commande2.setIdClient(client2.getIdClient()); // Associer le client 2 à la commande
        commandeDao.save(commande2);*/
    }
}
