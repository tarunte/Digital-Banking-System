package com.payment.bankingui.controller;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.payment.bankingui.model.Account;
import com.payment.bankingui.model.Transaction;
import com.payment.bankingui.model.User;
import com.payment.bankingui.repository.AccountRepository;
import com.payment.bankingui.repository.TransactionRepository;
import com.payment.bankingui.repository.UserRepository;
import com.payment.bankingui.service.PdfService;

@Controller
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PdfService pdfService;

    public TransactionController(TransactionRepository transactionRepository,
                                 AccountRepository accountRepository,
                                 UserRepository userRepository,
                                 PdfService pdfService) {

        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.pdfService = pdfService;
    }

    // ===============================
    // TRANSACTION PAGE
    // ===============================
    @GetMapping("/transactions")
    public String transactions(Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Account> accounts = accountRepository.findByUser(user);

        List<Transaction> userTransactions = new ArrayList<>();

        for (Account acc : accounts) {

            List<Transaction> tx = transactionRepository
                    .findByFromAccountOrToAccount(acc.getId(), acc.getId());

            userTransactions.addAll(tx);
        }

        model.addAttribute("transactions", userTransactions);

        return "transactions";
    }

    // ===============================
    // DOWNLOAD PDF STATEMENT
    // ===============================
    @GetMapping("/statement/pdf")
    public ResponseEntity<InputStreamResource> downloadStatement(Principal principal) {

        if (principal == null) {
            return ResponseEntity.badRequest().build();
        }

        String username = principal.getName();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Account> accounts = accountRepository.findByUser(user);

        List<Transaction> userTransactions = new ArrayList<>();

        for (Account acc : accounts) {

            List<Transaction> tx = transactionRepository
                    .findByFromAccountOrToAccount(acc.getId(), acc.getId());

            userTransactions.addAll(tx);
        }

        ByteArrayInputStream pdf = pdfService.generateStatement(userTransactions);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=bank-statement.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }
}