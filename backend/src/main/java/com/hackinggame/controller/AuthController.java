package com.hackinggame.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @GetMapping("/test")
    public String test() {
        return "Backend is running 🚀";
    }

    @PostMapping("/login")
    public String login() {
        return "Login API working (no DB yet)";
    }

    @PostMapping("/register")
    public String register() {
        return "Register API working (no DB yet)";
    }
}