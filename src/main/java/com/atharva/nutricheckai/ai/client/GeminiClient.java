package com.atharva.nutricheckai.ai.client;

import com.atharva.nutricheckai.ai.client.dto.Content;
import com.atharva.nutricheckai.ai.client.dto.GeminiRequest;
import com.atharva.nutricheckai.ai.client.dto.GeminiResponse;
import com.atharva.nutricheckai.ai.client.dto.Part;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    /** Configured via gemini.model in application.properties / env var GEMINI_MODEL */
    @Value("${gemini.model:gemini-2.0-flash}")
    private String geminiModel;

    @Value("${gemini.retry.max-attempts:3}")
    private int maxAttempts;

    @Value("${gemini.retry.backoff-ms:2000}")
    private long backoffMs;

    private String buildUrl() {
        return "https://generativelanguage.googleapis.com/v1beta/models/"
                + geminiModel + ":generateContent?key=" + apiKey;
    }

    /**
     * Calls the Gemini API with the given prompt.
     * Automatically retries up to {@code gemini.retry.max-attempts} times on
     * 503 (Service Unavailable) or 429 (Too Many Requests) with exponential backoff.
     */
    @Retryable(
        retryFor = { HttpServerErrorException.ServiceUnavailable.class,
                     HttpServerErrorException.class },
        maxAttemptsExpression = "${gemini.retry.max-attempts:3}",
        backoff = @Backoff(delayExpression = "${gemini.retry.backoff-ms:2000}", multiplier = 2)
    )
    public String callGemini(String prompt) {
        log.info("Calling Gemini model [{}]", geminiModel);

        Part part = new Part(prompt);
        Content content = new Content(List.of(part));
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

        try {
            GeminiResponse geminiResponse =
                    objectMapper.readValue(response.getBody(), GeminiResponse.class);

            return geminiResponse
                    .getCandidates()
                    .get(0)
                    .getContent()
                    .getParts()
                    .get(0)
                    .getText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response", e);
        }
    }

    /**
     * Recovery method — called when all retry attempts are exhausted.
     * Throws a clear RuntimeException so the GlobalExceptionHandler can surface a
     * friendly message to the frontend instead of leaking raw HTTP details.
     */
    @Recover
    public String recoverCallGemini(Exception ex, String prompt) {
        log.error("All Gemini retry attempts exhausted. Model: [{}]. Cause: {}", geminiModel, ex.getMessage());
        throw new RuntimeException(
                "The AI service is temporarily unavailable (model: " + geminiModel +
                "). Please wait a moment and try again.", ex);
    }
}