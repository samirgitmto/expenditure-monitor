package com.expen.service.parser;

import java.util.List;

import com.expen.model.Transaction;

public interface BankStatementParser {

	List<Transaction> parse(String pdfText) throws Exception;
	
}