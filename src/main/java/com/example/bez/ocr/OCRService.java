package com.example.bez.ocr;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.leptonica.global.leptonica;
import org.bytedeco.tesseract.TessBaseAPI;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class OCRService {

    public void extractTextFromPdfToExcel(File pdfFile, File outputExcelFile) throws IOException {
        // Создаем временную директорию для хранения изображений
        Path tempDir = Files.createTempDirectory("pdf_images");

        // Конвертируем PDF в изображения (одна страница - одно изображение)
        String[] cmd = {
                "pdftoppm",
                pdfFile.getAbsolutePath(),
                tempDir.toAbsolutePath().toString() + "/page",
                "-png"
        };
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("PDF to image conversion was interrupted", e);
        }

        // Инициализируем Tesseract
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        if (tessBaseAPI.Init("src/main/resources/tessdata", "rus") != 0) {
            throw new RuntimeException("Could not initialize Tesseract.");
        }

        // Создаем Excel-файл
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Extracted Text");

        // Распознаем текст на каждом изображении и записываем в Excel
        File[] imageFiles = tempDir.toFile().listFiles((dir, name) -> name.endsWith(".png"));
        if (imageFiles != null) {
            int rowIndex = 0;
            for (File imageFile : imageFiles) {
                PIX image = leptonica.pixRead(imageFile.getAbsolutePath());
                tessBaseAPI.SetImage(image);
                BytePointer outText = tessBaseAPI.GetUTF8Text();
                String extractedText = outText.getString();
                outText.deallocate();
                leptonica.pixDestroy(image);

                // Записываем текст в Excel
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(extractedText);
            }
        }

        // Закрываем Tesseract
        tessBaseAPI.End();

        // Удаляем временные файлы
        for (File file : tempDir.toFile().listFiles()) {
            file.delete();
        }
        tempDir.toFile().delete();

        // Сохраняем Excel-файл
        try (FileOutputStream fileOut = new FileOutputStream(outputExcelFile)) {
            workbook.write(fileOut);
        }
    }

    public String extractTextFromPdf(File pdfFile) throws IOException {
        // Создаем временную директорию для хранения изображений
        Path tempDir = Files.createTempDirectory("pdf_images");

        // Конвертируем PDF в изображения (одна страница - одно изображение)
        String[] cmd = {
                "pdftoppm",
                pdfFile.getAbsolutePath(),
                tempDir.toAbsolutePath().toString() + "/page",
                "-png"
        };
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("PDF to image conversion was interrupted", e);
        }

        // Инициализируем Tesseract
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        if (tessBaseAPI.Init("src/main/resources/tessdata", "rus") != 0) {
            throw new RuntimeException("Could not initialize Tesseract.");
        }

        // Распознаем текст на каждом изображении
        StringBuilder extractedText = new StringBuilder();
        File[] imageFiles = tempDir.toFile().listFiles((dir, name) -> name.endsWith(".png"));
        if (imageFiles != null) {
            for (File imageFile : imageFiles) {
                PIX image = leptonica.pixRead(imageFile.getAbsolutePath());
                tessBaseAPI.SetImage(image);
                BytePointer outText = tessBaseAPI.GetUTF8Text();
                extractedText.append(outText.getString());
                outText.deallocate();
                leptonica.pixDestroy(image);
            }
        }

        // Закрываем Tesseract
        tessBaseAPI.End();

        // Удаляем временные файлы
        for (File file : tempDir.toFile().listFiles()) {
            file.delete();
        }
        tempDir.toFile().delete();

        return extractedText.toString();
    }
}