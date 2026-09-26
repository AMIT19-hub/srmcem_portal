package com.example.srmcemportal.controller;

import com.example.srmcemportal.entity.User;
import com.example.srmcemportal.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final UserRepository userRepository;

    public StudentController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public User getProfile(Authentication authentication){
        String email= authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("student not found"));
    }
}
