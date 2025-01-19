package com.expen.service.parser;

import java.util.List;

import org.springframework.stereotype.Service;

import com.expen.model.Transaction;

@Service
public class AxisStatementParser implements BankStatementParser {

	@Override
	public List<Transaction> parse(String pdfText) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
