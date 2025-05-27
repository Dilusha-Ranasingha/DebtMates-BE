package com.example.debtmatesbe;

import com.example.debtmatesbe.model.User;
import com.example.debtmatesbe.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DebtMatesBeApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(DebtMatesBeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if SuperAdmin already exists
        if (userRepository.findByUsername("SuperAdmin") == null) {
            User superAdmin = new User();
            superAdmin.setUsername("SuperAdmin");
            superAdmin.setPassword(passwordEncoder.encode("Sup123#Adm1n"));
            superAdmin.setEmail("danadon242@gmail.com");
            superAdmin.setRole(User.Role.ADMIN); // Super admin is an admin
            userRepository.save(superAdmin);
            System.out.println("SuperAdmin created with username: SuperAdmin and email: danadon242@gmail.com");
        } else {
            System.out.println("SuperAdmin already exists in the database.");
        }
    }
}