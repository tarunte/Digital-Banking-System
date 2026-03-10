package com.payment.bankingui.controller;

import java.security.Principal;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.payment.bankingui.model.Account;
import com.payment.bankingui.model.Transaction;
import com.payment.bankingui.model.User;
import com.payment.bankingui.repository.AccountRepository;
import com.payment.bankingui.repository.TransactionRepository;
import com.payment.bankingui.repository.UserRepository;
import com.payment.bankingui.service.BankService;

@Controller
public class WebController {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final BankService bankService;

    public WebController(AccountRepository accountRepository,
                         UserRepository userRepository,
                         TransactionRepository transactionRepository,
                         BankService bankService) {

        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.bankService = bankService;
    }

    /* =========================================
       DASHBOARD
       ========================================= */

    @GetMapping("/")
    public String dashboard(Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();

        User user = userRepository
                .findByUsername(username)
                .orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        /* =========================
           LOAD USER ACCOUNTS
           ========================= */

        List<Account> accounts = accountRepository.findByUser(user);

        double totalBalance = 0.0;

        for (Account acc : accounts) {
            if (acc.getBalance() != null) {
                totalBalance += acc.getBalance();
            }
        }

        /* =========================
           USER TRANSACTIONS ONLY
           ========================= */

        Map<String, Double> monthlyTotals = new LinkedHashMap<>();

        for (Account acc : accounts) {

            List<Transaction> transactions =
                    transactionRepository.findByFromAccountOrToAccount(
                            acc.getId(),
                            acc.getId()
                    );

            for (Transaction t : transactions) {

                if (t.getTimestamp() == null) {
                    continue;
                }

                Month monthEnum = t.getTimestamp().getMonth();
                String month = monthEnum.toString();

                monthlyTotals.put(
                        month,
                        monthlyTotals.getOrDefault(month, 0.0) + t.getAmount()
                );
            }
        }

        /* =========================
           SEND DATA TO VIEW
           ========================= */

        model.addAttribute("accounts", accounts);
        model.addAttribute("username", username);
        model.addAttribute("totalBalance", totalBalance);
        model.addAttribute("monthlyTotals", monthlyTotals);

        return "dashboard";
    }

    /* =========================================
       TRANSFER BY ACCOUNT ID
       ========================================= */

    @PostMapping("/transfer")
    public String transfer(
            @RequestParam Long fromAccount,
            @RequestParam Long toAccount,
            @RequestParam Double amount) {

        bankService.transfer(fromAccount, toAccount, amount);

        return "redirect:/";
    }

    /* =========================================
       TRANSFER BY ACCOUNT NUMBER + IFSC
       ========================================= */

    @PostMapping("/bank-transfer")
    public String transferByAccountNumber(
            @RequestParam String fromAccountNumber,
            @RequestParam String toAccountNumber,
            @RequestParam String ifscCode,
            @RequestParam Double amount) {

        bankService.transferByAccountNumber(
                fromAccountNumber,
                toAccountNumber,
                ifscCode,
                amount
        );

        return "redirect:/";
    }

    /* =========================================
       DEPOSIT USING ACCOUNT NUMBER
       ========================================= */

    @PostMapping("/deposit")
    public String deposit(
            @RequestParam String accountNumber,
            @RequestParam Double amount) {

        bankService.depositByAccountNumber(accountNumber, amount);

        return "redirect:/";
    }

    /* =========================================
       WITHDRAW USING ACCOUNT NUMBER
       ========================================= */

    @PostMapping("/withdraw")
    public String withdraw(
            @RequestParam String accountNumber,
            @RequestParam Double amount) {

        bankService.withdrawByAccountNumber(accountNumber, amount);

        return "redirect:/";
    }

}