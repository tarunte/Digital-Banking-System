package com.payment.bankingui.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.bankingui.model.Account;
import com.payment.bankingui.model.User;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {


    /* =================================================
       FIND ACCOUNTS BELONGING TO A USER
       Used in dashboard
       ================================================= */

    List<Account> findByUser(User user);


    /* =================================================
       FIND ACCOUNT BY ACCOUNT NUMBER
       Used in deposit / withdraw / transfers
       ================================================= */

    Optional<Account> findByAccountNumber(String accountNumber);


    /* =================================================
       FIND ACCOUNT BY ACCOUNT NUMBER + IFSC
       Used in secure bank transfers
       ================================================= */

    Optional<Account> findByAccountNumberAndIfscCode(String accountNumber,
                                                     String ifscCode);


    /* =================================================
       FIND LATEST ACCOUNT NUMBER
       Used by AccountNumberService to generate next number
       ================================================= */

    Optional<Account> findTopByOrderByAccountNumberDesc();


    /* =================================================
       CHECK IF ACCOUNT NUMBER EXISTS
       Used to prevent duplicates
       ================================================= */

    boolean existsByAccountNumber(String accountNumber);


    /* =================================================
       FIND ACCOUNT BY USER + ACCOUNT TYPE
       (Useful for savings/current checks)
       ================================================= */

    Optional<Account> findByUserAndAccountType(User user, String accountType);


    /* =================================================
       FIND ALL ACCOUNTS BY EMAIL
       (Useful for admin or auditing)
       ================================================= */

    List<Account> findByEmail(String email);

}