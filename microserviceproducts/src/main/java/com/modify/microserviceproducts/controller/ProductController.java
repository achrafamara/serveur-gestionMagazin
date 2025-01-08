package com.modify.microserviceproducts.controller;

import com.modify.microserviceproducts.Exception.ProductNotFoundException;
import com.modify.microserviceproducts.configuration.ApplicationpropertiesConfiguration;
import com.modify.microserviceproducts.dao.ProductDao;
import com.modify.microserviceproducts.model.Product;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.persistence.GeneratedValue;
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
public class ProductController implements HealthIndicator {
    @Autowired
    ProductDao productDao ;

    @Autowired
    ApplicationpropertiesConfiguration applicationpropertiesConfiguration ;

    @Override
    public Health health() {
        System.out.println("****** Actuator : ProductController health() ");
        List<Product> products = productDao.findAll();
        if (products.isEmpty()) {
            return Health.down().build();
        }
        return Health.up().build();
    }
    @CircuitBreaker(name ="productService" , fallbackMethod = "fallbackListeDesProduits")
    @Retry(name = "productService", fallbackMethod = "fallbackListeDesProduits")
    @TimeLimiter(name = "productService")
    @GetMapping(value = "/Produits")
    public CompletableFuture<List<Product>> listeDesProduits() {
        return CompletableFuture.supplyAsync(() -> productDao.findAll());
    }
    @GetMapping("/Produits/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productDao.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produit avec l'id " + id + " n'existe pas"));
    }

    public CompletableFuture<List<Product>> fallbackListeDesProduits(Throwable t) {
        System.out.println("Appel du fallback : " + t.getMessage());
        return CompletableFuture.completedFuture(Collections.emptyList());
    }
    //creer un produit
    @PostMapping("/Produits")
    public Product createProduct(@RequestBody Product product) {
        return productDao.save(product);
    }
    //modifier un produit :
    @PutMapping("/Produits/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        Optional<Product> existingProduct = productDao.findById(id);
        if (existingProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = existingProduct.get();
        product.setTitre(updatedProduct.getTitre());
        product.setDescription(updatedProduct.getDescription());
        product.setImage(updatedProduct.getImage());
        product.setPrix(updatedProduct.getPrix());

        productDao.save(product);
        return ResponseEntity.ok(product);
    }
    // supprimer un produit
    @DeleteMapping("/Produits/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productDao.existsById(id)) {
            productDao.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
