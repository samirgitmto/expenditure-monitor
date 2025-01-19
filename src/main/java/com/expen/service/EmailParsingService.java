package com.expen.service;

import java.math.BigDecimal;
import java.time.LocalDate;          // For handling the current date
import java.time.LocalDateTime;
import java.time.ZoneId;             // For converting LocalDate to Date
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.search.ComparisonTerm;
import jakarta.mail.search.ReceivedDateTerm;

@Service
public class EmailParsingService {

	 @Value("${mail.username}")
	    private String username;

	    @Value("${mail.password}")
	    private String password;

	    @Value("${mail.host}")
	    private String host;

	    @Value("${mail.port}")
	    private int port;

	    @Value("${mail.protocol}")
	    private String protocol;
	
    public void fetchEmailsAndProcess() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", protocol);
        properties.put("mail.imaps.host", host);
        properties.put("mail.imaps.port", port);
        properties.put("mail.imaps.ssl.enable", "true");

        try {
            Session session = Session.getInstance(properties, null);
            Store store = session.getStore();
            store.connect(username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

//            Message[] messages = inbox.getMessages();
            Message[] messages = fetchRecentEmails(inbox);
            System.out.println("inbox length: " + messages.length);
            
            int totalExpenditurePerDay = 0;
            
            for (Message message : messages) {
                // Get the sender(s) of the message
                Address[] fromAddresses = message.getFrom();

                if (fromAddresses != null && fromAddresses.length > 0) {
                    // Print the first sender's email address
                    String senderEmail = ((InternetAddress) fromAddresses[0]).getAddress();
                    String senderName = ((InternetAddress) fromAddresses[0]).getPersonal();
                    
                    if (senderName!=null && (senderEmail.contains("phonepe") || senderName.contains("phonepe"))) {
                    	System.out.println("Sender's Email: " + senderEmail);
                        System.out.println("Sender's Name: " + senderName);
                        
                        Object content = message.getContent();
                        if (content instanceof String) {
                            System.out.println("Body: " + content);
                        } else if (content instanceof Multipart) {
                            Multipart multipart = (Multipart) content;
                            for (int i = 0; i < multipart.getCount(); i++) {
                                BodyPart part = multipart.getBodyPart(i);
                                if (part.getContent() instanceof String) {
//                                    System.out.println("multipart Body : " + part.getContent());
                                    System.out.println("multipart Body " + i);
                                }
                                
                             // Look for the HTML part (Content-Type: text/html)
                                if (part.isMimeType("text/html")) {
                                    String htmlContent = (String) part.getContent();
                                    System.out.println("HTML Content " + i);
                                    
                                    // Now you can parse the HTML content to extract details
                                   totalExpenditurePerDay += extractTransactionDetails(htmlContent);
                                }
                                
                            }
                        }
                    }
                }
            }

            System.out.println("totalExpenditurePerDay: " + totalExpenditurePerDay);
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int extractTransactionDetails(String htmlContent) {
        // This is where you would parse the HTML content to extract the relevant details
        // In this case, you can use a library like JSoup to parse HTML
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(htmlContent);

//        System.out.println(htmlContent);
        
        // Example: Extract the transaction details like paid to, amount, etc.
        String paidTo = doc.select("span:contains(Paid to)").next().text();
//        String txnId = doc.select("td:contains(Txn. ID)").next().text();
        
        String txnId = null;
        
     // Select all tables
        Elements tables = doc.select("table");

        // Iterate over all tables to find the one containing "Txn. ID"
        for (Element table : tables) {
            // Check if the table contains the text "Txn. ID"
            if (table.text().contains("Txn. ID")) {
                // Call the recursive function to search for Txn. ID in the current table
                txnId = findTransactionId(table);

                // Print the extracted Txn. ID
                if (txnId != null) {
                    System.out.println("Transaction ID found");
                    break;  // Found the Txn. ID, no need to check further
                }
            }
        }
        
//        String amount = doc.select("td:contains(₹)").text();

        String amount = null;
        
     // Select all <td> elements containing ₹
//        Elements amountElements = doc.select("td:contains(₹)");
        Elements tdElements = doc.select("td:contains(₹)");
//        System.out.println("size1: " + amountElements.size());

     // Select all <td> elements
//        Elements tdElements = doc.select("td");
//        System.out.println("size2: " + tdElements.size());

        for (Element td : tdElements) {
            // Check if this <td> contains the ₹ symbol and has no children
            if ((td.html().contains("&#8377;") || td.text().contains("₹")) && td.children().isEmpty()) {
                System.out.println("Exact <td> Element: " + td);
                System.out.println("Extracted Amount: " + td.text().replace("₹", "").replace("&#8377;", "").trim());
                amount = td.text().replace("₹", "").replace("&#8377;", "").trim();
            }
        }
        
        System.out.println("Paid to: " + paidTo);
        System.out.println("Transaction ID: " + txnId);
        System.out.println("Amount: " + amount);
        
        return amount!=null?Integer.parseInt(amount):0;
    }
    
    private String findTransactionId(Element element) {
        // Recursive case: Traverse child elements
        for (Element tr : element.children()) {
        	
        	for (Element td: tr.children()) {
        		String tdElement = td.text().trim();
        		// Use regex to match the format of Txn. ID (T followed by numbers)
                Pattern pattern = Pattern.compile("T\\d+");
                Matcher matcher = pattern.matcher(tdElement);
                if (matcher.find()) {
                    return matcher.group(0); // Return the matched Txn. ID
                } else {
                    return null;  // If the pattern is not found, return null
                }
        	}
        }

        // If no Txn. ID is found, return null
        return null;
    }
    
    public Message[] fetchRecentEmails(Folder inbox) throws MessagingException {
    	// Get the current date and set time to midnight (start of today)
        LocalDate localDate = LocalDate.now(); // Current date
        LocalDateTime localDateTime = localDate.atStartOfDay();  // Get midnight time on the current date
        Date startOfDay = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());  // Convert to Date

        // Print the start of the day (for debugging)
        System.out.println("Start of the day (midnight): " + startOfDay);

        // Create a search term to filter messages received on or after the start of today
        ReceivedDateTerm receivedDateTerm = new ReceivedDateTerm(ComparisonTerm.GE, startOfDay);

        // Search the inbox folder for messages received in the last 5 minutes
        Message[] recentMessages = inbox.search(receivedDateTerm);

        // Process the messages
        if (recentMessages.length == 0) {
            System.out.println("No messages received in the last 5 minutes.");
        } 
//            else {
//            for (Message message : recentMessages) {
//                // Print subject, recipient, and received date
//                System.out.println("Subject: " + message.getSubject());
//                Address[] recipients = message.getAllRecipients();
//                if (recipients != null) {
//                    for (Address address : recipients) {
//                        System.out.println("Recipient: " + address.toString());
//                    }
//                }
//            }
//        }
        return recentMessages;
    }
    
    private void processPaymentMessage(Message message) {
        try {
            String content = getTextFromMessage(message);
            // Extract payment details from content using regex or string parsing
            System.out.println("Message Content: " + content);

            // Example: Parse payment amount and date from the email
            String transactionId = parseTransactionId(content);
            BigDecimal amount = parseAmount(content);
            LocalDateTime date = parseDate(content);

            // Save to database
            savePaymentToDatabase(transactionId, amount, date);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTextFromMessage(Message message) throws Exception {
        String parsedMessage = message.getContent().toString();
        System.out.println("parsedMessage " + parsedMessage);
		if (message.isMimeType("text/plain")) {
            return parsedMessage;
        } else if (message.isMimeType("text/html")) {
            return parsedMessage; // Optionally, strip HTML tags
        }
        return "";
    }

    private String parseTransactionId(String content) {
        // Example: Extract transaction ID using regex
        return content.split("Transaction ID:")[1].split("\n")[0].trim();
    }

    private BigDecimal parseAmount(String content) {
        // Example: Extract amount using regex
        String amountString = content.split("Amount:")[1].split("\n")[0].trim();
        return new BigDecimal(amountString.replaceAll("[^\\d.]", ""));
    }

    private LocalDateTime parseDate(String content) {
        // Example: Extract date using regex
        String dateString = content.split("Date:")[1].split("\n")[0].trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateString, formatter);
    }

    private void savePaymentToDatabase(String transactionId, BigDecimal amount, LocalDateTime date) {
        // Implement database save logic (use a service/repository)
        System.out.println("Saving payment: " + transactionId + ", " + amount + ", " + date);
    }
}
