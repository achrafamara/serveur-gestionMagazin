package com.modify.microservicecommandes.controller;

import com.modify.microservicecommandes.configuration.ApplicationpropertiesConfiguration;
import com.modify.microservicecommandes.dao.CommandeDao;
import com.modify.microservicecommandes.dto.CommandeWithClientDTO;
import com.modify.microservicecommandes.dto.CommandeWithProductDTO;
import com.modify.microservicecommandes.model.Client;
import com.modify.microservicecommandes.model.Commande;
import com.modify.microservicecommandes.model.Product;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RefreshScope
@RestController
public class CommandeController implements HealthIndicator {
    @Autowired
    CommandeDao commandeDao ;

    @Autowired
    ApplicationpropertiesConfiguration applicationpropertiesConfiguration ;
    @Autowired
    private RestTemplate restTemplate;

    private static final String PRODUCT_SERVICE_URL = "http://localhost:9006/MICROSERVICEPRODUCTS/Produits/";
    private static final String CLIENT_SERVICE_URL= "http://localhost:9006/MICROSERVICECLIENTS/clients/";


    @Override
    public Health health() {
        System.out.println("****** Actuator : CommandeController health() ");
        if(commandeDao.count()>0){
            return Health.up().withDetail("commandes : ",commandeDao.count()).build();
        }else{
            return Health.down().withDetail("commandes","pas de commandes pour le moment").build();
        }

    }
    @GetMapping("/commandes")
    public List<Commande> getCommandes(){
        LocalDate date = LocalDate.now().minusDays(applicationpropertiesConfiguration.getLastCommandes());
        System.out.println("------app properties ------ "+applicationpropertiesConfiguration.getLastCommandes());
        System.out.println(applicationpropertiesConfiguration.getLastCommandes());
        return commandeDao.findCommandesByDateAfter(date);

    }
    @GetMapping("/commandes/{id}")
    public ResponseEntity<Commande> getCommandeById(@PathVariable Long id) {
        Optional<Commande> commande = commandeDao.findById(id);
        if (commande.isPresent()) {
            return ResponseEntity.ok(commande.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public Commande createCommande(@RequestBody Commande commande) {
        return commandeDao.save(commande);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Commande> updateCommande(@PathVariable Long id, @RequestBody Commande updatedCommande) {
        Optional<Commande> existingCommande = commandeDao.findById(id);
        if (existingCommande.isPresent()) {
            Commande commande = existingCommande.get();
            commande.setDescription(updatedCommande.getDescription());
            commande.setQuantite(updatedCommande.getQuantite());
            commande.setDate(updatedCommande.getDate());
            commande.setMontant(updatedCommande.getMontant());
            commandeDao.save(commande);
            return ResponseEntity.ok(commande);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable Long id) {
        if (commandeDao.existsById(id)) {
            commandeDao.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackGetProductDetails")
    @Retry(name = "productService", fallbackMethod = "fallbackGetProductDetails")
    @TimeLimiter(name = "productService")
    @GetMapping("/products/{id}")
    public CompletableFuture<Product> getProductDetails(@PathVariable int id) {
        return CompletableFuture.supplyAsync(() -> {
            return restTemplate.getForObject(PRODUCT_SERVICE_URL + id, Product.class);
        });
    }
    public CompletableFuture<Product> fallbackGetProductDetails(Long id, Throwable t) {
        System.out.println("Fallback executed due to: " + t.getMessage());
        Product fallbackProduct = new Product();
        fallbackProduct.setId(id);
        fallbackProduct.setTitre("Unknown Product");
        fallbackProduct.setDescription("No description available");
        fallbackProduct.setImage("No image");
        fallbackProduct.setPrix(0.0);
        return CompletableFuture.completedFuture(fallbackProduct);
    }



    //creer une commande d'un client sans produits
    @PostMapping("/create/with-client")
    public ResponseEntity<Commande> createCommandeWithClient(@RequestBody Commande commande) {
        // Vérifie si le client existe via le Microservice Client
        Client client = restTemplate.getForObject(CLIENT_SERVICE_URL + commande.getIdClient(), Client.class);
        if (client == null) {
            return ResponseEntity.badRequest().body(null);
        }
        System.out.println("----client id ------" + client.getIdClient());

        // Associe le client à la commande
        commande.setIdClient(client.getIdClient());
        commande.setClient(client);
        // Sauvegarde la commande
        Commande savedCommande = commandeDao.save(commande);
        return ResponseEntity.ok(savedCommande);
    }


    //creer une commande d'un client avec plusieurs produits
    @PostMapping("/commandes/create-with-client-products")
    public ResponseEntity<Commande> createCommandeWithClientAndProducts(@RequestBody Commande commande) {
        // Vérifie si le client existe via l'API du microservice Client
        Client client = restTemplate.getForObject(CLIENT_SERVICE_URL + commande.getIdClient(), Client.class);
        if (client == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // Vérifie si les produits existent via l'API du microservice Produit
        List<Product> produits = new ArrayList<>();
        for (Long produitId : commande.getIdProduits()) {
            Product produit = restTemplate.getForObject(PRODUCT_SERVICE_URL + produitId, Product.class);
            if (produit == null) {
                return ResponseEntity.badRequest().body(null);
            }
            produits.add(produit);
        }

        // Associe le client et les produits à la commande
        commande.setClient(client);
        commande.setProduits(produits);

        // Sauvegarde de la commande (en utilisant un repository Commande)
        Commande savedCommande = commandeDao.save(commande);
        return ResponseEntity.ok(savedCommande);
    }


    // Récupérer une commande avec le client et les produits associés
    @GetMapping("/commandes/with-client-products/{id}")
    public ResponseEntity<Commande> getCommandeWithClientAndProducts(@PathVariable Long id) {
        Optional<Commande> commandeOptional = commandeDao.findById(id);
        if (commandeOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Commande commande = commandeOptional.get();

        // Récupérer les informations du client depuis le microservice Client
        Client client = restTemplate.getForObject(CLIENT_SERVICE_URL + commande.getIdClient(), Client.class);

        // Récupérer les informations des produits depuis le microservice Produit
        List<Product> produits = new ArrayList<>();
        for (Long produitId : commande.getIdProduits()) {
            Product produit = restTemplate.getForObject(PRODUCT_SERVICE_URL + produitId, Product.class);
            produits.add(produit);
        }

        commande.setClient(client);
        commande.setProduits(produits);

        return ResponseEntity.ok(commande);  // Retourner la commande enrichie
    }

    //modifier une commande
    @PutMapping("/update/with-client-products/{id}")
    public ResponseEntity<Commande> updateCommandeWithClientAndProducts(@PathVariable Long id, @RequestBody Commande updatedCommande) {
        Optional<Commande> existingCommande = commandeDao.findById(id);
        if (existingCommande.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        System.out.println(updatedCommande);
        // Vérifie si le client existe via l'API du microservice Client
        Client client = restTemplate.getForObject(CLIENT_SERVICE_URL + updatedCommande.getIdClient(), Client.class);
        if (client == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // Vérifie si les produits existent via l'API du microservice Produit
        List<Product> produits = new ArrayList<>();
        for (Long produitId : updatedCommande.getIdProduits()) {
            Product produit = restTemplate.getForObject(PRODUCT_SERVICE_URL + produitId, Product.class);
            if (produit == null) {
                return ResponseEntity.badRequest().body(null);
            }
            produits.add(produit);
        }

        Commande commande = existingCommande.get();
        commande.setDescription(updatedCommande.getDescription());
        commande.setQuantite(updatedCommande.getQuantite());
        commande.setDate(updatedCommande.getDate());
        commande.setMontant(updatedCommande.getMontant());
        commande.setIdClient(updatedCommande.getIdClient());  // Mise à jour de l'ID client
        commande.setIdProduits(updatedCommande.getIdProduits());  // Mise à jour des produits

        commande.setClient(client);
        commande.setProduits(produits);

        commandeDao.save(commande);
        return ResponseEntity.ok(commande);
    }

    //recuperer les commandes d'un client
    @GetMapping("/commandes/client/{idClient}")
    public ResponseEntity<List<Commande>> getCommandesByClient(@PathVariable Long idClient) {
        // Cherche toutes les commandes associées à l'ID du client
        List<Commande> commandes = commandeDao.findByIdClient(idClient);

        // Si aucune commande n'est trouvée pour ce client, retourne un statut 404
        if (commandes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Récupérer les informations du client depuis le microservice Client
        Client client = restTemplate.getForObject(CLIENT_SERVICE_URL + idClient, Client.class);

        // Pour chaque commande, récupérer les produits associés
        for (Commande commande : commandes) {
            // Récupérer les produits pour chaque commande à partir des IDs des produits
            List<Product> produits = new ArrayList<>();
            for (Long produitId : commande.getIdProduits()) {
                Product produit = restTemplate.getForObject(PRODUCT_SERVICE_URL + produitId, Product.class);
                if (produit != null) {
                    produits.add(produit);
                }
            }
            // Associer le client et les produits à la commande
            commande.setClient(client);
            commande.setProduits(produits);
        }

        // Retourner la liste des commandes avec le client et les produits
        return ResponseEntity.ok(commandes);
    }



}


