package com.atharva.nutricheckai.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * AI client for ingredient text analysis using Groq API (OpenAI-compatible).
 * Groq provides blazing-fast LPU-based inference with a generous free tier
 * (14,400 requests/day) and very low 503 rate compared to Gemini free tier.
 *
 * Method name kept as callGemini() to avoid changing callers (AIAnalysisService, AITestController).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${groq.api.key}")
    private String apiKey;

    /** Groq model for ingredient analysis — change via GROQ_MODEL env var */
    @Value("${groq.model:qwen/qwen3.8-27b}")
    private String model;

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    /**
     * Calls Groq API for ingredient safety analysis.
     * Retries on 503 (overload) or 429 (rate limit) with exponential backoff.
     */
    @Retryable(
        retryFor = {
            HttpServerErrorException.ServiceUnavailable.class,
            HttpServerErrorException.class,
            HttpClientErrorException.TooManyRequests.class
        },
        maxAttemptsExpression = "${gemini.retry.max-attempts:3}",
        backoff = @Backoff(delayExpression = "${gemini.retry.backoff-ms:2000}", multiplier = 2)
    )
    public String callGemini(String prompt) {
        log.info("Calling Groq model [{}]", model);

        // OpenAI-compatible request format with JSON mode
        Map<String, Object> message = Map.of("role", "user", "content", prompt);
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(message),
                "temperature", 0.2,
                "response_format", Map.of("type", "json_object")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                GROQ_URL, HttpMethod.POST, entity, String.class
        );

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Groq response", e);
        }
    }

    /**
     * Recovery method — called when all retry attempts are exhausted.
     */
    @Recover
    public String recoverCallGemini(Exception ex, String prompt) {
        log.error("All Groq retry attempts exhausted. Model: [{}]. Cause: {}", model, ex.getMessage());
        throw new RuntimeException(
                "The AI analysis service is temporarily unavailable. Please wait a moment and try again.", ex);
    }
}