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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
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
 *
 * Uses @Retryable to transparently retry on transient 503 / overload errors.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OCRService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    /** Configured via gemini.model in application.properties / env var GEMINI_MODEL */
    @Value("${gemini.model:gemini-2.0-flash}")
    private String geminiModel;

    private String buildUrl() {
        return "https://generativelanguage.googleapis.com/v1beta/models/"
                + geminiModel + ":generateContent?key=" + apiKey;
    }

    /**
     * Extracts raw ingredient text from a product label image using Gemini Vision.
     * Retries automatically on 503 (Service Unavailable) with exponential backoff.
     */
    @Retryable(
        retryFor = { HttpServerErrorException.ServiceUnavailable.class,
                     HttpServerErrorException.class },
        maxAttemptsExpression = "${gemini.retry.max-attempts:3}",
        backoff = @Backoff(delayExpression = "${gemini.retry.backoff-ms:2000}", multiplier = 2)
    )
    public String extractText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot extract text from an empty file");
        }

        log.info("OCR: calling Gemini Vision model [{}]", geminiModel);

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
                    buildUrl(),
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
        } catch (HttpServerErrorException e) {
            // Re-throw so @Retryable can intercept and retry
            log.warn("OCR: Gemini Vision returned HTTP {}, will retry if attempts remain", e.getStatusCode());
            throw e;
        } catch (Exception e) {
            log.error("Gemini Vision OCR failed: {}", e.getMessage(), e);
            throw new OCRException("Failed to extract text from image using AI. Please try a clearer image.", e);
        }
    }

    /**
     * Recovery method — called when all OCR retry attempts are exhausted.
     */
    @Recover
    public String recoverExtractText(Exception ex, MultipartFile file) {
        log.error("All OCR retry attempts exhausted. Model: [{}]. Cause: {}", geminiModel, ex.getMessage());
        throw new OCRException(
                "The AI image service is temporarily unavailable. Please wait a moment and try again.", ex);
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
