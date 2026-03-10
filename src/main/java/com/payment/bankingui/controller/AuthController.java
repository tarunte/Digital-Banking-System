package com.payment.bankingui.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.payment.bankingui.model.User;
import com.payment.bankingui.repository.UserRepository;
import com.payment.bankingui.service.BankService;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final BankService bankService;

    public AuthController(UserRepository userRepository,
                          BCryptPasswordEncoder passwordEncoder,
                          BankService bankService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bankService = bankService;
    }

    // Open Register Page
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // Handle Registration
    @PostMapping("/register")
    public String register(User user) {

        System.out.println("Registering user: " + user.getUsername());

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");

        // Save user
        User savedUser = userRepository.save(user);

        // Create SAVINGS account
        bankService.createAccount(
                savedUser,
                savedUser.getUsername(),
                savedUser.getEmail(),
                "SAVINGS",
                1000.0
        );

        // Create CURRENT account
        bankService.createAccount(
                savedUser,
                savedUser.getUsername(),
                savedUser.getEmail(),
                "CURRENT",
                0.0
        );

        System.out.println("Accounts created for user: " + savedUser.getUsername());

        return "redirect:/login";
    }

    // Open Login Page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}