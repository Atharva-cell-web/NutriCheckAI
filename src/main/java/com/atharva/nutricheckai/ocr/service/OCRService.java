package com.atharva.nutricheckai.ocr.service;

import com.atharva.nutricheckai.exception.OCRException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OCRService — extracts ingredient text from product label images using OCR.space API.
 *
 * OCR.space is a dedicated OCR service (not an LLM) which means:
 *  - It never gets "overloaded" like AI models do
 *  - Response time is 1–3 seconds (vs 30+ seconds with Gemini Vision)
 *  - Free tier: 500 requests/day, 1 MB per image — more than enough for a portfolio project
 *  - Fallback key K88888888 works without registration for demos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OCRService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * OCR.space API key. Register free at https://ocr.space/ocrapi
     * Falls back to the public demo key K88888888 if OCR_API_KEY is not set.
     */
    @Value("${ocr.api.key:K88888888}")
    private String ocrApiKey;

    private static final String OCR_SPACE_URL = "https://api.ocr.space/parse/image";

    /**
     * Extracts raw ingredient text from a product label image using OCR.space.
     *
     * @param file uploaded image (JPEG or PNG)
     * @return raw extracted text from the image
     * @throws OCRException if the image cannot be processed or contains no text
     */
    public String extractText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot extract text from an empty file");
        }

        log.info("OCR: sending image to OCR.space (filename: {}, size: {} bytes)",
                file.getOriginalFilename(), file.getSize());

        try {
            byte[] imageBytes = file.getBytes();
            final String filename = (file.getOriginalFilename() != null && !file.getOriginalFilename().isBlank())
                    ? file.getOriginalFilename() : "image.jpg";

            // Build multipart/form-data request for OCR.space
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("apikey", ocrApiKey);
            body.add("language", "eng");
            body.add("isOverlayRequired", "false");
            body.add("detectOrientation", "true");
            body.add("scale", "true");
            body.add("file", new ByteArrayResource(imageBytes) {
                @Override
                public String getFilename() {
                    return filename;
                }
            });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    OCR_SPACE_URL, HttpMethod.POST, requestEntity, String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());

            // Check for OCR.space processing error
            boolean isErrored = root.path("IsErroredOnProcessing").asBoolean(false);
            if (isErrored) {
                String errorMsg = root.path("ErrorMessage").asText("Unknown OCR error");
                log.error("OCR.space processing error: {}", errorMsg);
                throw new OCRException("Image could not be processed. " + errorMsg, null);
            }

            JsonNode parsedResults = root.path("ParsedResults");
            if (parsedResults.isEmpty()) {
                throw new OCRException(
                        "No text could be extracted from the image. Please ensure the ingredients label is clearly visible.", null);
            }

            String extractedText = parsedResults.get(0).path("ParsedText").asText();
            if (extractedText == null || extractedText.trim().isEmpty()) {
                throw new OCRException(
                        "The image does not contain readable text. Please try a higher quality photo of the ingredients label.", null);
            }

            log.info("OCR.space successfully extracted text ({} characters)", extractedText.length());
            return extractedText;

        } catch (IOException e) {
            log.error("IO error reading uploaded image: {}", e.getMessage(), e);
            throw new OCRException("Failed to read the uploaded image file", e);
        } catch (OCRException e) {
            // Re-throw OCRExceptions as-is (don't wrap them)
            throw e;
        } catch (Exception e) {
            log.error("OCR.space request failed: {}", e.getMessage(), e);
            throw new OCRException("Failed to extract text from image. Please try a clearer image.", e);
        }
    }

    /**
     * Parses raw OCR-extracted text into a clean list of individual ingredient strings.
     * Handles common label formatting: colons, parentheses, percentages, etc.
     */
    public List<String> extractIngredients(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }

        // Remove "ingredients:" prefix if OCR picked it up
        String cleaned = text.replaceAll("(?i)^.*ingredients\\s*:\\s*", "");
        // Remove content in parentheses that's just numbers/percentages
        cleaned = cleaned.replaceAll("\\(\\d+[%g]?\\)", "");
        // Remove special characters except commas and common ingredient notation
        cleaned = cleaned.replaceAll("[^a-zA-Z0-9,.()/% -]", " ");

        return Arrays.stream(cleaned.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty() && s.length() > 1)
                .distinct()
                .collect(Collectors.toList());
    }
}
