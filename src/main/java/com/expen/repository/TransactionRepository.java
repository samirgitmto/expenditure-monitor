package com.expen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.expen.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}