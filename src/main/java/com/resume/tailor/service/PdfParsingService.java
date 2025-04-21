package com.resume.tailor.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class PdfParsingService {

    private static final Logger log = LoggerFactory.getLogger(PdfParsingService.class);

    public String parsePdf(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            log.warn("Attempted to parse an empty file.");
            throw new IllegalArgumentException("Cannot parse an empty file.");
        }

        if (!"application/pdf".equals(file.getContentType())) {
             log.warn("Attempted to parse a non-PDF file: {}", file.getContentType());
            throw new IllegalArgumentException("File must be a PDF.");
        }

        log.info("Starting PDF parsing for file: {}", file.getOriginalFilename());
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                log.info("Successfully extracted text from PDF: {}", file.getOriginalFilename());
                // Basic cleaning: remove excessive whitespace
                text = text.replaceAll("\s+", " ").trim();
                return text;
            } else {
                log.error("Cannot parse encrypted PDF: {}", file.getOriginalFilename());
                throw new IOException("Cannot parse encrypted PDF file.");
            }
        } catch (IOException e) {
            log.error("Error parsing PDF file {}: {}", file.getOriginalFilename(), e.getMessage(), e);
            throw new IOException("Failed to parse PDF file: " + file.getOriginalFilename(), e);
        }
    }
} 