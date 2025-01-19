package com.expen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expen.model.Transaction;
import com.expen.service.parser.AxisStatementParser;
import com.expen.service.parser.BankStatementParser;
import com.expen.service.parser.SbiStatementParser;

/*
Context: Transaction Parser Service
The TransactionParserService will decide which BankStatementParser to use based on the bank name provided. This is the "context" class in the Strategy Pattern.
*/

@Service
public class TransactionParserService {

    // Declare all the bank parsers
    private final SbiStatementParser sbiParser;
    private final AxisStatementParser axisParser;
    //private final UnionBankStatementParser unionBankParser;

    @Autowired
    public TransactionParserService(SbiStatementParser sbiParser,
    		AxisStatementParser axisParser) {
        this.sbiParser = sbiParser;
        this.axisParser = axisParser;
    }

    public List<Transaction> parseTransactions(String pdfText, String bankName) throws Exception {
        // Choose the appropriate parser based on the bank name
        BankStatementParser parser;

        switch (bankName.toLowerCase()) {
            case "sbi":
                parser = sbiParser;
                break;
            case "axis":
                parser = axisParser;
                break;
            default:
                throw new IllegalArgumentException("Unsupported bank: " + bankName);
        }

        return parser.parse(pdfText); // Parse the transactions using the chosen parser
    }
}
