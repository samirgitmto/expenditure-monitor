package com.expen.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.expen.model.Transaction;

@Component
public class TransactionParserServiceOld {
	
	public static List<Transaction> parseTransactionsOld(String pdfText) {
        List<Transaction> transactions = new ArrayList<>();

        // Sample Regex: Adjust based on the statement format
//        String transactionPattern = "(\\d{2}-\\d{2}-\\d{4})\\s+([A-Za-z0-9 ]+)\\s+(-?\\d+\\.\\d{2})\\s+(-?\\d+\\.\\d{2})";
        String transactionPattern = "(\\d{2}-\\d{2}-\\d{2})\\s+([A-Za-z0-9/ .]*)\\s+(CR|DR)?\\s*(-?\\d+\\.\\d{2})\\s*(-?\\d+\\.\\d{2})";
//        Pattern pattern = Pattern.compile(transactionPattern);
        Pattern pattern = Pattern.compile(transactionPattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(pdfText);

        while (matcher.find()) {
            String date = matcher.group(1);
            String description = matcher.group(2).trim();
            String type = matcher.group(3); // CR or DR
            double amount = Double.parseDouble(matcher.group(4));
            double balance = Double.parseDouble(matcher.group(5));

//            transactions.add(new Transaction(date, description, type, amount, balance));
            transactions.add(new Transaction());
        }

        
        return transactions;
    }
	
	public static List<Transaction> parseTransactionsIterOld(String inputText) {
        List<Transaction> transactions = new ArrayList<>();

        // Split input text into lines
        String[] lines = inputText.split("\\n");

        // Iterate through each line (ignoring headers)
        for (String line : lines) {
            if (line.trim().isEmpty()) continue; // Skip empty lines

            // Split by spaces or fixed-width logic
            String[] columns = line.trim().split("\\s+");

            System.out.println(Arrays.toString(columns));
         // Validate the first column as a date
            if (!isValidDate(columns[0])) {
                System.out.println("Skipping invalid row: " + line);
                continue;
            }
            
            // Check if there are at least 6 columns
            if (columns.length == 6) {
                String date = columns[0];
                String reference = columns[1];
                String refNoOrChqNo = columns[2].equals("-") ? null : columns[2];
                String credit = columns[3].equals("-") ? null : columns[3];
                String debit = columns[4].equals("-") ? null : columns[4];
                String balance = columns[5];

                // Create and add a Transaction object
//                transactions.add(new Transaction(date, reference, refNoOrChqNo, credit, debit, balance));
                transactions.add(new Transaction());
            }
            else // Handle the case where Transaction Reference contains spaces
                if (columns.length > 6) {
                    String date = columns[0];
                    String credit = columns[columns.length - 3];
                    String debit = columns[columns.length - 2];
                    String balance = columns[columns.length - 1];
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
//                    transactions.add(new Transaction(date, reference, refNoOrChqNo.equals("-")?null:refNoOrChqNo, credit.equals("-") ? null : credit, debit.equals("-") ? null : debit, balance));
                    transactions.add(new Transaction());
                }
        }

        return transactions;
    }
	
	public List<Transaction> parseTransactionsSBI(String inputText) {

        List<Transaction> transactions = new ArrayList<>();

        // Split input text into lines
        String[] lines = inputText.split("\\n");

        // Iterate through each line (ignoring headers)
        for (String line : lines) {
            if (line.trim().isEmpty()) continue; // Skip empty lines

            // Split by spaces or fixed-width logic
            String[] columns = line.trim().split("\\s+");

         // Validate the first column as a date
            if (!isValidDate(columns[0])) {
                continue;
            }
            
            // Check if there are at least 6 columns
            if (columns.length == 6) {
                LocalDate date = parseDate(columns[0]);
                String reference = columns[1];
                String refNoOrChqNo = columns[2].equals("-") ? null : columns[2];
                BigDecimal credit = parseBigDecimal(columns[3]);
                BigDecimal debit = parseBigDecimal(columns[4]);
                BigDecimal balance = parseBigDecimal(columns[5]);

                // Create and add a Transaction object
                transactions.add(new Transaction(date, reference, refNoOrChqNo, credit, debit, balance));
            }
            else // Handle the case where Transaction Reference contains spaces
                if (columns.length > 6) {
                    LocalDate date = parseDate(columns[0]);
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
	
	// Validate if the given string is a valid date
    private static boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
        sdf.setLenient(false); // Ensure strict parsing
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
 // Parse a date from a string (e.g., "04-11-24")
    private static LocalDate parseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
        return LocalDate.parse(date, formatter);
    }
    
 // Parse a monetary value (e.g., "3000.00", "-", "0.00")
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