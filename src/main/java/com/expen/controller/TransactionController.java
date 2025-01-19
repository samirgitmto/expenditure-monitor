package com.expen.controller;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expen.model.ApiResponse;
import com.expen.model.Transaction;
import com.expen.service.PDFParserService;
import com.expen.service.TransactionParserService;
import com.expen.service.TransactionParserServiceOld;
import com.expen.service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

	private static final Logger LOGGER = LogManager.getLogger(TransactionController.class);

	@Autowired
	TransactionService transactionService;

	@Autowired
	private TransactionParserServiceOld transactionParserServiceOld;

	@Autowired
	private TransactionParserService transactionParserService;
	
	@Autowired
	private PDFParserService pdfParserService;

	@PostMapping("/post-transactions")
	public ResponseEntity<ApiResponse<Void>> parseTransactions(@RequestParam String pdfPath,
			@RequestParam String password,
			@RequestParam String bankName) {
		String pdfText;
		try {
			pdfText = pdfParserService.extractTextFromPDF(pdfPath, password);
		} catch (Exception e) {
			LOGGER.error("Error extracting text from PDF", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse<>("Failed to extract text from the PDF", null, false));
		}

		List<Transaction> transactions;
		try {
//			transactions = transactionParserServiceOld.parseTransactionsSBI(pdfText);
			transactions = transactionParserService.parseTransactions(pdfText, bankName);  // (behavioural pattern) Strategy 
		} catch (Exception e) {
			LOGGER.error("Error parsing transactions", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse<>("Failed to parse transactions", null, false));
		}

		boolean inserted;
		try {
			inserted = transactionService.bulkInsertTransactions(transactions);
		} catch (Exception e) {
			LOGGER.error("Error inserting transactions into the database", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<>("Failed to insert transactions into the database", null, false));
		}

		if (inserted) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new ApiResponse<>("Transactions inserted successfully", null, true));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse<>("Transaction insertion failed", null, false));
		}
	}

	
	
	//	@PostMapping("/post-transactions")
	public ResponseEntity<String> parseTransactions0(@RequestParam String pdfPath, @RequestParam String password) {

		String pdfText;

		try {
			pdfText = pdfParserService.extractTextFromPDF(pdfPath, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("pdf not extracted");
		}

		List<Transaction> transactions = transactionParserServiceOld.parseTransactionsSBI(pdfText);

		Boolean bulkInsertTransactions = this.transactionService.bulkInsertTransactions(transactions);
		if (bulkInsertTransactions)
			return ResponseEntity.status(HttpStatus.CREATED).body("inserted into db");

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("insertion error");
	}

	//	public static void main(String[] args) {
	//		 try {
	//			 
	//			 // Sample data
	//		        String sampleData = """
	//		                04-11-24 UPI/CR/467503021378/MOHAMMED/ICIC/8800844836/NA - 3000.00 - 16254.58
	//		                09-11-24 UPI/DR/489900513148/Bank Acc/BARB/5508010000/Payme - - 9000.00 7254.58
	//		                12-11-24 UPI/CR/431788354749/ABDULLAH/ICIC/9824419216/NA - 4000.00 - 11254.58
	//		                """;
	//
	//		        // Parse transactions
	//		        List<Transaction> sampleTransactions = TransactionParserService.parseTransactions(sampleData);
	//
	//		        // Print parsed transactions
	//		        sampleTransactions.forEach(System.out::println);
	//			 
	//	            // Path to the PDF and password
	//	            String pdfPath = "C://Users//Dell//Downloads//sbi-november-statement.pdf";
	//	            String password = "14033080895";
	//
	//	            // Extract text from PDF
	//	            String pdfText = PDFParserService.extractTextFromPDF(pdfPath, password);
	//	            try (FileOutputStream fileOutputStream = new FileOutputStream("pdfText.txt")) {
	//					fileOutputStream.write(pdfText.getBytes());
	//					fileOutputStream.write("\nwriting List of Transactions now\n".getBytes());
	//					// Parse transactions
	////					List<Transaction> transactions = TransactionParserService.parseTransactions(pdfText);
	//					List<Transaction> transactions = TransactionParserService.parseTransactions(pdfText);
	//					for (Transaction transaction : transactions) {
	//						System.out.println(transaction.toString());
	//						fileOutputStream.write(transaction.toString().getBytes());
	//					}
	//				}
	//	            
	//	            
	//	            // Set up H2 database
	//	            //H2Database.createTable();
	//
	//	            // Insert transactions into the database
	//	            //H2Database.insertTransactions(transactions);
	//
	//	            System.out.println("Transactions have been stored in the database successfully!");
	//	        } catch (Exception e) {
	//	            e.printStackTrace();
	//	        }
	//	}
}