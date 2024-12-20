package com.example.bez;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.bez.storage.StorageServiceInterface;

@SpringBootApplication
public class BezApplication {

	public static void main(String[] args) {
		SpringApplication.run(BezApplication.class, args);
	}

	@Bean
	CommandLineRunner init(
		StorageServiceInterface storageService ) {
		return (args) -> {
			// storageService.deleteAll();
			storageService.init();
			
		};
	}
}
