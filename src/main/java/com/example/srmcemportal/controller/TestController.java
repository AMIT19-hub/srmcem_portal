package com.example.srmcemportal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/student")
    public String test1() {
        return "student auth tested successful";
    }
    @GetMapping("/recruiter")
    public String test2() {
        return "recruiter auth tested successful";
    }
    @GetMapping("/admin")
    public String test3() {
        return "admin access granted";
    }
}
