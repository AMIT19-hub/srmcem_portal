package com.example.srmcemportal.service;

import com.example.srmcemportal.security.JwtService;
import com.example.srmcemportal.dto.LoginRequest;
import com.example.srmcemportal.dto.RegisterRequest;
import com.example.srmcemportal.entity.Role;
import com.example.srmcemportal.entity.User;
import com.example.srmcemportal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already registered";
        }


        if (request.getRole() == Role.ADMIN) {
            return "Admin registration not allowed";
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);
        return "Registration successful";
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return "Invalid email or password";

        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return "Invalid email or password";
        }

        return jwtService.generateToken(user.getEmail(), user.getRole().name());
    }
}
