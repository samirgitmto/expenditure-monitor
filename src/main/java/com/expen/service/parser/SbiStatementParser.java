package com.expen.service.parser;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.expen.model.Transaction;

@Service
public class SbiStatementParser implements BankStatementParser {

	private static final String DATE_PATTERN = "dd-MM-yy";
    private static final String SPACE_DELIMITER = "\\s+";
    private static final int EXPECTED_COLUMNS = 6;
	
	@Override
	public List<Transaction> parse(String pdfText) throws Exception {
		List<Transaction> transactions = parseTransactions(pdfText);
		return transactions;
	}

	public List<Transaction> parseTransactions(String inputText) {

		List<Transaction> transactions = new ArrayList<>();

		// Split input text into lines
		String[] lines = inputText.split("\\n");

		// Iterate through each line (ignoring headers)
		for (String line : lines) {
			if (line.trim().isEmpty()) continue; // Skip empty lines

			// Split by spaces or fixed-width logic
			String[] columns = line.trim().split(SPACE_DELIMITER);

			// Validate the first column as a date
			if (!isValidDate(columns[0], DATE_PATTERN)) {
				continue;
			}

			// Check if there are at least 6 columns
			if (columns.length == EXPECTED_COLUMNS) {
				LocalDate date = parseDate(columns[0], DATE_PATTERN);
				String reference = columns[1];
				String refNoOrChqNo = columns[2].equals("-") ? null : columns[2];
				BigDecimal credit = parseBigDecimal(columns[3]);
				BigDecimal debit = parseBigDecimal(columns[4]);
				BigDecimal balance = parseBigDecimal(columns[5]);

				// Create and add a Transaction object
				transactions.add(new Transaction(date, reference, refNoOrChqNo, credit, debit, balance));
			}
			else // Handle the case where Transaction Reference contains spaces
				if (columns.length > EXPECTED_COLUMNS) {
					LocalDate date = parseDate(columns[0], DATE_PATTERN);
					BigDecimal credit = parseBigDecimal(columns[columns.length - 3]);
					BigDecimal debit = parseBigDecimal(columns[columns.length - 2]);
					BigDecimal balance = parseBigDecimal(columns[columns.length - 1]);
					String refNoOrChqNo = columns[columns.length-4];

					// Combine all parts between date and credit into Transaction Reference
					StringBuilder referenceBuilder = new StringBuilder();
					for (int i = 1; i < columns.length - 4; i++) { // 7
						referenceBuilder.append(columns[i]).append(" ");
					}
					String reference = referenceBuilder.toString().trim();

					// Extract Ref No or Chq No from the reference
					String[] referenceParts = reference.split("/", 2);
					//String refNoOrChqNo = referenceParts.length > 1 ? referenceParts[1] : "-";

					// Add transaction to the list
					transactions.add(new Transaction(date, reference, refNoOrChqNo.equals("-")?null:refNoOrChqNo, credit, debit, balance));
				}
		}

		return transactions;

	}

	private static boolean isValidDate(String date, String DATE_PATTERN) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		sdf.setLenient(false); // Ensure strict parsing
		try {
			sdf.parse(date);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	private static LocalDate parseDate(String date, String DATE_PATTERN) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
		return LocalDate.parse(date, formatter);
	}

	private static BigDecimal parseBigDecimal(String value) {
		if ("-".equals(value)) {
			return BigDecimal.ZERO; // Treat "-" as zero
		}
		try {
			return new BigDecimal(value);
		} catch (NumberFormatException e) {
			return BigDecimal.ZERO; // Default to zero in case of error
		}
	}

}