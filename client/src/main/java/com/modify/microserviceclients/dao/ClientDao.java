package com.modify.microserviceclients.dao;

import com.modify.microserviceclients.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientDao extends JpaRepository<Client,Long> {
}
