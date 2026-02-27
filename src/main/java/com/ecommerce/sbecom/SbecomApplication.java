package com.ecommerce.sbecom;


import com.ecommerce.sbecom.config.AppConstants;
import com.ecommerce.sbecom.model.Role;
import com.ecommerce.sbecom.repository.RoleRepository;
//import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;
@EnableCaching
@SpringBootApplication
@RequiredArgsConstructor
public class SbecomApplication         implements CommandLineRunner {
	private final RoleRepository roleRepository;

	public static void main(String[] args) {
//// 1. Load the .env file
//		Dotenv dotenv = Dotenv.configure()
//				.directory("./") // Ensures it looks in the root folder
//				.ignoreIfMissing()
//				.load();
//
//		// 2. THE FIX: Loop through EVERY entry in .env and set it as a System Property
//		// This handles DB_USER, DB_PASS, JWT_SECRET, and everything else automatically.
//		dotenv.entries().forEach(entry -> {
//			System.setProperty(entry.getKey(), entry.getValue());
//		});
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
	@Override
	public void run(String... args) throws Exception {
		roleRepository.findByName("ROLE_"+ AppConstants.ADMIN_ROLE).ifPresentOrElse(role->{
			System.out.println("ADMIN ROLE ALREADY EXISTS"+role.getName());
		},()->{
			Role role = new Role();
			role.setName("ROLE_"+AppConstants.ADMIN_ROLE);
			Role save = roleRepository.save(role);
		});

		roleRepository.findByName("ROLE_"+AppConstants.USER_ROLE).ifPresentOrElse(role->{
			System.out.println("USER ROLE ALREADY EXISTS"+role.getName());
		},()->{
			Role role = new Role();
			role.setName("ROLE_"+AppConstants.USER_ROLE);
			Role save = roleRepository.save(role);
		});
		roleRepository.findByName("ROLE_"+AppConstants.GUEST_ROLE).ifPresentOrElse(role->{
			System.out.println("GUEST ROLE ALREADY EXISTS"+role.getName());
		},()->{
			Role role = new Role();
			role.setName("ROLE_"+AppConstants.GUEST_ROLE);
			Role save = roleRepository.save(role);
		});
	}
}
