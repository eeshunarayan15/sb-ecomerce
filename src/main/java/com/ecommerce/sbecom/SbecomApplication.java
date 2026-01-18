package com.ecommerce.sbecom;


import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
public class SbecomApplication {

	public static void main(String[] args) {
// 1. Load the .env file
		Dotenv dotenv = Dotenv.configure()
				.directory("./") // Ensures it looks in the root folder
				.ignoreIfMissing()
				.load();

		// 2. THE FIX: Loop through EVERY entry in .env and set it as a System Property
		// This handles DB_USER, DB_PASS, JWT_SECRET, and everything else automatically.
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});
		SpringApplication.run(SbecomApplication.class, args);
	}


	@PostConstruct
	public void checkEnv() {
		// Use System.getProperty() because that's where we stored the values
		System.out.println("--- Environment Check ---");
		System.out.println("DATABASEURL: " + System.getProperty("DATABASEURL"));
		System.out.println("DB_USER: " + System.getProperty("DB_USER"));
		System.out.println("JWT_SECRET is set: " + (System.getProperty("JWT_SECRET") != null));
		System.out.println("-------------------------");
	}
}
