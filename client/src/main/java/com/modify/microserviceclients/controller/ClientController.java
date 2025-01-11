package com.modify.microserviceclients.controller;



import com.modify.microserviceclients.model.Client;
import com.modify.microserviceclients.dao.ClientDao;
import com.modify.microserviceclients.Exception.ClientNotFoundException;
import com.modify.microserviceclients.configuration.ApplicationpropertiesConfiguration;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@RestController
public class ClientController implements HealthIndicator {

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ApplicationpropertiesConfiguration applicationpropertiesConfiguration;

    @Override
    public Health health() {
        System.out.println("****** Actuator : ClientController health() ");
        List<Client> clients = clientDao.findAll();
        if (clients.isEmpty()) {
            return Health.down().build();
        }
        return Health.up().build();
    }

    // Liste des clients avec un fallback en cas d'erreur
    @CircuitBreaker(name = "clientService", fallbackMethod = "fallbackListeDesClients")
    @Retry(name = "clientService", fallbackMethod = "fallbackListeDesClients")
    @TimeLimiter(name = "clientService")
    @GetMapping("/clients")
    public CompletableFuture<List<Client>> listeDesClients() {
        // Exécution asynchrone sans Thread.sleep()
        return CompletableFuture.supplyAsync(() -> clientDao.findAll());
    }



    // Récupérer un client par ID
    @GetMapping("/clients/{id}")
    public Client getClientById(@PathVariable Long id) {
        return clientDao.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client avec l'id " + id + " n'existe pas"));
    }

    // Fallback en cas d'erreur dans la liste des clients
    public CompletableFuture<List<Client>> fallbackListeDesClients(Throwable t) {
        System.out.println("Appel du fallback : " + t.getMessage());
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    // Créer un client
    @PostMapping("/clients")
    public Client createClient(@RequestBody Client client) {

        return clientDao.save(client);
    }

    // Modifier un client
    @PutMapping("/clients/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client updatedClient) {
        Optional<Client> existingClient = clientDao.findById(id);
        if (existingClient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Client client = existingClient.get();
        client.setNom(updatedClient.getNom());
        client.setEmail(updatedClient.getEmail());
        client.setAdresse(updatedClient.getAdresse());
        client.setTelephone(updatedClient.getTelephone());

        clientDao.save(client);
        return ResponseEntity.ok(client);
    }

    // Supprimer un client
    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        if (clientDao.existsById(id)) {
            clientDao.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

