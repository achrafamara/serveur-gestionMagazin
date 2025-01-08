package com.modify.microserviceproducts;

import com.modify.microserviceproducts.dao.ProductDao;
import com.modify.microserviceproducts.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroserviceproductsApplication implements CommandLineRunner {

	@Autowired
	ProductDao productDao;
	public static void main(String[] args) {
		SpringApplication.run(MicroserviceproductsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// Insertion de données dans la table Produit
		Product produit1 = new Product();
		produit1.setTitre("Produit 1");
		produit1.setDescription("Description du produit 1");
		produit1.setImage("image_url_1");
		produit1.setPrix(20.0);
		productDao.save(produit1);

		Product produit2 = new Product();
		produit2.setTitre("Produit 2");
		produit2.setDescription("Description du produit 2");
		produit2.setImage("image_url_2");
		produit2.setPrix(40.0);
		productDao.save(produit2);
	}
}
