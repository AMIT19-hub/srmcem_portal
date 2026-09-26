package com.example.srmcemportal.config;

import com.example.srmcemportal.entity.Role;
import com.example.srmcemportal.entity.User;
import com.example.srmcemportal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminConfig {
    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {

        return args -> {

            String adminEmail = "admin@srmcem.com";
            if (!userRepository.existsByEmail(adminEmail)) {
                User user = new User();

                user.setName("Admin");
                user.setEmail(adminEmail);
                user.setPassword(passwordEncoder.encode("admin123"));
                user.setRole(Role.ADMIN);
                userRepository.save(user);
                System.out.println("Admin account created successfully");
            }
        };
    }
}
