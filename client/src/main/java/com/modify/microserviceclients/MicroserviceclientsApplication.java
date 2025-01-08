package com.modify.microserviceclients;

import com.modify.microserviceclients.dao.ClientDao;
import com.modify.microserviceclients.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroserviceclientsApplication implements CommandLineRunner {

	@Autowired
	ClientDao clientDao;
	public static void main(String[] args) {
		SpringApplication.run(MicroserviceclientsApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		// Insertion de données dans la table Client
		Client client1 = new Client();
		client1.setNom("John Doe");
		client1.setEmail("johndoe@example.com");
		client1.setAdresse("123 Main St");
		client1.setTelephone("1234567890");
		clientDao.save(client1);

		Client client2 = new Client();
		client2.setNom("Jane Doe");
		client2.setEmail("janedoe@example.com");
		client2.setAdresse("456 Elm St");
		client2.setTelephone("9876543210");
		clientDao.save(client2);
	}
}
