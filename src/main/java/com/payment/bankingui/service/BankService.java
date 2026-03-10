package com.payment.bankingui.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.bankingui.model.Account;
import com.payment.bankingui.model.Transaction;
import com.payment.bankingui.model.User;
import com.payment.bankingui.repository.AccountRepository;
import com.payment.bankingui.repository.TransactionRepository;

@Service
public class BankService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountNumberService accountNumberService;

    public BankService(AccountRepository accountRepository,
                       TransactionRepository transactionRepository,
                       AccountNumberService accountNumberService) {

        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.accountNumberService = accountNumberService;
    }

    /* =========================================
       CREATE NEW ACCOUNT
       ========================================= */

    @Transactional
    public Account createAccount(User user,
                                 String ownerName,
                                 String email,
                                 String accountType,
                                 Double initialBalance) {

        if (initialBalance == null) {
            initialBalance = 0.0;
        }

        Account account = new Account();

        account.setOwnerName(ownerName);
        account.setEmail(email);
        account.setAccountType(accountType);
        account.setBalance(initialBalance);

        account.setAccountNumber(accountNumberService.generateAccountNumber());
        account.setIfscCode(accountNumberService.generateIFSC());

        account.setUser(user);

        return accountRepository.save(account);
    }

    /* =========================================
       TRANSFER BY ACCOUNT ID
       ========================================= */

    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, Double amount) {

        if (amount == null || amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        if (fromAccountId.equals(toAccountId)) {
            throw new RuntimeException("Cannot transfer to same account");
        }

        Account from = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account to = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (from.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        accountRepository.save(from);
        accountRepository.save(to);

        Transaction tx = new Transaction();
        tx.setTransactionId(generateTransactionId());
        tx.setFromAccount(fromAccountId);
        tx.setToAccount(toAccountId);
        tx.setAmount(amount);
        tx.setTimestamp(LocalDateTime.now());

        transactionRepository.save(tx);
    }

    /* =========================================
       TRANSFER BY ACCOUNT NUMBER
       ========================================= */

    @Transactional
    public void transferByAccountNumber(String fromAccNo,
                                        String toAccNo,
                                        String ifscCode,
                                        Double amount) {

        if (amount == null || amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        Account from = accountRepository
                .findByAccountNumber(fromAccNo)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account to = accountRepository
                .findByAccountNumber(toAccNo)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (!to.getIfscCode().equalsIgnoreCase(ifscCode)) {
            throw new RuntimeException("Invalid IFSC code");
        }

        if (from.getAccountNumber().equals(to.getAccountNumber())) {
            throw new RuntimeException("Cannot transfer to same account");
        }

        if (from.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        accountRepository.save(from);
        accountRepository.save(to);

        Transaction tx = new Transaction();
        tx.setTransactionId(generateTransactionId());
        tx.setFromAccount(from.getId());
        tx.setToAccount(to.getId());
        tx.setAmount(amount);
        tx.setTimestamp(LocalDateTime.now());

        transactionRepository.save(tx);
    }

    /* =========================================
       DEPOSIT BY ACCOUNT ID
       ========================================= */

    @Transactional
    public void deposit(Long accountId, Double amount) {

        if (amount == null || amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance() + amount);

        accountRepository.save(account);

        Transaction tx = new Transaction();
        tx.setTransactionId(generateTransactionId());
        tx.setFromAccount(null);
        tx.setToAccount(accountId);
        tx.setAmount(amount);
        tx.setTimestamp(LocalDateTime.now());

        transactionRepository.save(tx);
    }

    /* =========================================
       DEPOSIT BY ACCOUNT NUMBER
       ========================================= */

    @Transactional
    public void depositByAccountNumber(String accountNumber, Double amount) {

        if (amount == null || amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        deposit(account.getId(), amount);
    }

    /* =========================================
       WITHDRAW BY ACCOUNT ID
       ========================================= */

    @Transactional
    public void withdraw(Long accountId, Double amount) {

        if (amount == null || amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance() - amount);

        accountRepository.save(account);

        Transaction tx = new Transaction();
        tx.setTransactionId(generateTransactionId());
        tx.setFromAccount(accountId);
        tx.setToAccount(null);
        tx.setAmount(amount);
        tx.setTimestamp(LocalDateTime.now());

        transactionRepository.save(tx);
    }

    /* =========================================
       WITHDRAW BY ACCOUNT NUMBER
       ========================================= */

    @Transactional
    public void withdrawByAccountNumber(String accountNumber, Double amount) {

        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        withdraw(account.getId(), amount);
    }

    /* =========================================
       GENERATE TRANSACTION ID
       ========================================= */

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }
}