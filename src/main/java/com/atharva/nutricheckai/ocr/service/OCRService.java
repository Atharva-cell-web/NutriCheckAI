package com.atharva.nutricheckai.ocr.service;

import com.atharva.nutricheckai.ai.client.dto.Content;
import com.atharva.nutricheckai.ai.client.dto.GeminiRequest;
import com.atharva.nutricheckai.ai.client.dto.GeminiResponse;
import com.atharva.nutricheckai.ai.client.dto.Part;
import com.atharva.nutricheckai.exception.OCRException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OCRService — extracts ingredient text from images using Gemini Vision API.
 * This replaces the Tesseract-based approach which required a local binary installation.
 * Gemini Vision handles multilingual labels and handwritten text far better than Tesseract.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OCRService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    /**
     * Extracts raw ingredient text from a product label image using Gemini Vision.
     */
    public String extractText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot extract text from an empty file");
        }

        try {
            // Encode image to Base64
            byte[] imageBytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String mimeType = file.getContentType() != null ? file.getContentType() : "image/jpeg";

            // Build multimodal Gemini request: image part + text instruction part
            Part imagePart = new Part(null, new Part.InlineData(mimeType, base64Image));
            Part textPart = new Part(
                "Look at this product label image. " +
                "Find the INGREDIENTS section and extract ONLY the ingredients list as plain text. " +
                "Return the ingredients separated by commas. " +
                "Do NOT include any extra explanation — only the ingredients list."
            );

            Content content = new Content(List.of(imagePart, textPart));
            GeminiRequest request = new GeminiRequest(List.of(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    GEMINI_URL + apiKey,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            GeminiResponse geminiResponse = objectMapper.readValue(response.getBody(), GeminiResponse.class);
            String extractedText = geminiResponse.getCandidates().get(0)
                    .getContent().getParts().get(0).getText();

            log.info("Gemini Vision extracted text of length: {}", extractedText.length());
            return extractedText;

        } catch (IOException e) {
            log.error("IO error reading uploaded image: {}", e.getMessage(), e);
            throw new OCRException("Failed to read the uploaded image file", e);
        } catch (Exception e) {
            log.error("Gemini Vision OCR failed: {}", e.getMessage(), e);
            throw new OCRException("Failed to extract text from image using AI. Please try a clearer image.", e);
        }
    }

    /**
     * Parses raw extracted text into a clean list of individual ingredient strings.
     */
    public List<String> extractIngredients(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }

        // Remove "ingredients:" prefix if Gemini included it
        String cleaned = text.replaceAll("(?i)^.*ingredients\\s*:\\s*", "");
        // Remove anything in parentheses that's just numbers/percentages
        cleaned = cleaned.replaceAll("\\(\\d+[%g]?\\)", "");
        // Remove special characters except commas and letters
        cleaned = cleaned.replaceAll("[^a-zA-Z0-9,.()/% -]", " ");

        return Arrays.stream(cleaned.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty() && s.length() > 1)
                .distinct()
                .collect(Collectors.toList());
    }
}
