package com.payment.bankingui.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.bankingui.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /* =========================================
       FIND TRANSACTIONS BY ACCOUNT
       (used for statements and dashboard)
       ========================================= */

    List<Transaction> findByFromAccountOrToAccount(Long fromAccount, Long toAccount);

    /* =========================================
       FIND ONLY SENT TRANSACTIONS
       ========================================= */

    List<Transaction> findByFromAccount(Long fromAccount);

    /* =========================================
       FIND ONLY RECEIVED TRANSACTIONS
       ========================================= */

    List<Transaction> findByToAccount(Long toAccount);

}