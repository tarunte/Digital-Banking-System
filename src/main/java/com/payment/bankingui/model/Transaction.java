package com.payment.bankingui.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String transactionId;

    @Column(name = "from_account")
    private Long fromAccount;

    @Column(name = "to_account")
    private Long toAccount;

    private Double amount;

    private LocalDateTime timestamp;

    public Transaction() {
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(Long fromAccount, Long toAccount, Double amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.transactionId = "TXN" + System.currentTimeMillis();
    }

    public Long getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Long fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Long getToAccount() {
        return toAccount;
    }

    public void setToAccount(Long toAccount) {
        this.toAccount = toAccount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}