package com.payment.bankingui.controller;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.bankingui.model.Account;
import com.payment.bankingui.model.Transaction;
import com.payment.bankingui.model.User;
import com.payment.bankingui.repository.AccountRepository;
import com.payment.bankingui.repository.TransactionRepository;
import com.payment.bankingui.repository.UserRepository;
import com.payment.bankingui.service.PdfService;

@RestController
public class StatementController {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PdfService pdfService;

    public StatementController(TransactionRepository transactionRepository,
                               AccountRepository accountRepository,
                               UserRepository userRepository,
                               PdfService pdfService) {

        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.pdfService = pdfService;
    }

    @GetMapping("/statement")
    public ResponseEntity<byte[]> downloadStatement(Principal principal) {

        // Get logged-in user
        User user = userRepository
                .findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get user's accounts
        List<Account> accounts = accountRepository.findByUser(user);

        List<Transaction> userTransactions = new ArrayList<>();

        // Get transactions for each account
        for (Account acc : accounts) {

            List<Transaction> tx = transactionRepository
                    .findByFromAccountOrToAccount(acc.getId(), acc.getId());

            userTransactions.addAll(tx);
        }

        ByteArrayInputStream pdf = pdfService.generateStatement(userTransactions);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statement.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf.readAllBytes());
    }
}