package com.payment.bankingui.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.payment.bankingui.model.Account;
import com.payment.bankingui.repository.AccountRepository;

@Service
public class AccountNumberService {

    private final AccountRepository accountRepository;

    public AccountNumberService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /* =========================================
       GENERATE UNIQUE ACCOUNT NUMBER
       ========================================= */

    public String generateAccountNumber() {

        long baseNumber = 1002003000L;

        Optional<Account> lastAccount =
                accountRepository.findTopByOrderByAccountNumberDesc();

        if (lastAccount.isPresent()) {

            String lastAccNo = lastAccount.get().getAccountNumber();

            if (lastAccNo != null && !lastAccNo.isEmpty()) {

                try {
                    long lastNumber = Long.parseLong(lastAccNo);
                    return String.valueOf(lastNumber + 1);
                } catch (NumberFormatException e) {
                    // fallback if corrupted account number exists
                    return String.valueOf(baseNumber + 1);
                }

            }
        }

        return String.valueOf(baseNumber + 1);
    }

    /* =========================================
       GENERATE IFSC CODE
       ========================================= */

    public String generateIFSC() {

        return "WIN0001";

    }
}