package com.modify.microservicecommandes.dao;

import com.modify.microservicecommandes.model.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CommandeDao extends JpaRepository<Commande,Long> {
    List<Commande> findCommandesByDateAfter(LocalDate date);
    List<Commande>  findByIdClient(Long clientId);
}
