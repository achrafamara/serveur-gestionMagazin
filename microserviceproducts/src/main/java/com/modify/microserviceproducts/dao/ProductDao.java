package com.modify.microserviceproducts.dao;

import com.modify.microserviceproducts.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao extends JpaRepository <Product,Long> {



}
