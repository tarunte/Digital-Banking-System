package com.payment.bankingui.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "account")
public class Account {


/* =========================================
   PRIMARY KEY
   ========================================= */

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

/* =========================================
   BASIC ACCOUNT DETAILS
   ========================================= */

@Column(name = "owner_name", nullable = false)
private String ownerName;

@Column(nullable = false)
private String email;

@Column(nullable = false)
private Double balance = 0.0;

@Column(name = "account_type")
private String accountType;

/* =========================================
   REAL BANKING FIELDS
   ========================================= */

@Column(name = "account_number", unique = true)
private String accountNumber;

@Column(name = "ifsc_code")
private String ifscCode;

/* =========================================
   USER RELATIONSHIP
   ========================================= */

@ManyToOne
@JoinColumn(name = "user_id")
private User user;

/* =========================================
   CONSTRUCTORS
   ========================================= */

public Account() {
}

public Account(String ownerName,
               String email,
               Double balance,
               String accountType,
               String accountNumber,
               String ifscCode,
               User user) {

    this.ownerName = ownerName;
    this.email = email;
    this.balance = balance;
    this.accountType = accountType;
    this.accountNumber = accountNumber;
    this.ifscCode = ifscCode;
    this.user = user;
}

/* =========================================
   GETTERS AND SETTERS
   ========================================= */

public Long getId() {
    return id;
}

public String getOwnerName() {
    return ownerName;
}

public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
}

public String getEmail() {
    return email;
}

public void setEmail(String email) {
    this.email = email;
}

public Double getBalance() {
    return balance;
}

public void setBalance(Double balance) {
    this.balance = balance;
}

public String getAccountType() {
    return accountType;
}

public void setAccountType(String accountType) {
    this.accountType = accountType;
}

public String getAccountNumber() {
    return accountNumber;
}

public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
}

public String getIfscCode() {
    return ifscCode;
}

public void setIfscCode(String ifscCode) {
    this.ifscCode = ifscCode;
}

public User getUser() {
    return user;
}

public void setUser(User user) {
    this.user = user;
}


}
