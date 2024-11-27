package com.example.bez.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.bez.ocr.OCRService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
public class OCRController {

    @Autowired
    private OCRService ocrService;

    @PostMapping("/extract-text-to-excel")
    public ResponseEntity<Resource> extractTextToExcel(@RequestParam("file") MultipartFile file) {
        try {
            // Сохраняем загруженный файл во временный файл
            Path tempPdfFile = Files.createTempFile("uploaded_pdf", ".pdf");
            Files.copy(file.getInputStream(), tempPdfFile, StandardCopyOption.REPLACE_EXISTING);

            // Создаем временный файл для Excel
            Path tempExcelFile = Files.createTempFile("extracted_text", ".xlsx");

            // Распознаем текст и создаем Excel-файл
            ocrService.extractTextFromPdfToExcel(tempPdfFile.toFile(), tempExcelFile.toFile());

            // Удаляем временный PDF-файл
            Files.delete(tempPdfFile);

            // Возвращаем Excel-файл в ответе
            Resource resource = new FileSystemResource(tempExcelFile.toFile());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=extracted_text.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/extract-text")
    public ResponseEntity<String> extractText(@RequestParam("file") MultipartFile file) {
        try {
            // Сохраняем загруженный файл во временный файл
            Path tempFile = Files.createTempFile("uploaded_pdf", ".pdf");
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Распознаем текст
            String extractedText = ocrService.extractTextFromPdf(tempFile.toFile());

            // Удаляем временный файл
            Files.delete(tempFile);

            return ResponseEntity.ok(extractedText);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        }
    }
}