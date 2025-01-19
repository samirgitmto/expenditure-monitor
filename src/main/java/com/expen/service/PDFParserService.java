package com.expen.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PDFParserService {
    public String extractTextFromPDF(String filePath, String password) throws Exception {
        try (PDDocument document = PDDocument.load(new File(filePath), password)) {
            if (document.isEncrypted()) {
                document.setAllSecurityToBeRemoved(true);
            }
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }
}
