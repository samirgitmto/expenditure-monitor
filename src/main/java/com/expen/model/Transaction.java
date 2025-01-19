package com.expen.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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
	private Long tId;
    private LocalDate date;  // Store the date as LocalDate
    private String reference;
    private String refNoOrChqNo;
    private BigDecimal credit; // Use BigDecimal for accurate credit values
    private BigDecimal debit;  // Use BigDecimal for accurate debit values
    private BigDecimal balance; // Use BigDecimal for balance values

    public Transaction() {
	}
    
    public Transaction(LocalDate date, String reference, String refNoOrChqNo, BigDecimal credit, BigDecimal debit, BigDecimal balance) {
        this.date = date;
        this.reference = reference;
        this.refNoOrChqNo = refNoOrChqNo;
        this.credit = credit;
        this.debit = debit;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + date +
                ", reference='" + reference + '\'' +
                ", refNoOrChqNo='" + refNoOrChqNo + '\'' +
                ", credit=" + credit +
                ", debit=" + debit +
                ", balance=" + balance +
                '}';
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getRefNoOrChqNo() {
        return refNoOrChqNo;
    }

    public void setRefNoOrChqNo(String refNoOrChqNo) {
        this.refNoOrChqNo = refNoOrChqNo;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public BigDecimal getDebit() {
        return debit;
    }

    public void setDebit(BigDecimal debit) {
        this.debit = debit;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}