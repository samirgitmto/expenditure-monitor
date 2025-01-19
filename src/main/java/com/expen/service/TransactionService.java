package com.expen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expen.model.Transaction;
import com.expen.repository.TransactionRepository;

@Service
public class TransactionService {

	@Autowired
	TransactionRepository transactionRepository;
	
	public Boolean bulkInsertTransactions(List<Transaction> transactions) {
		List<Transaction> list = transactionRepository.saveAll(transactions);
		Boolean addAll = true;
		if (list.size()>1)
			addAll = false;
		System.out.println("Boolean bulkInsertTransactions: " + addAll);
		return addAll;
	}
}