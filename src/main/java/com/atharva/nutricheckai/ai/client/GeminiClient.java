package com.atharva.nutricheckai.ai.client;

import com.atharva.nutricheckai.ai.client.dto.Content;
import com.atharva.nutricheckai.ai.client.dto.GeminiRequest;
import com.atharva.nutricheckai.ai.client.dto.GeminiResponse;
import com.atharva.nutricheckai.ai.client.dto.Part;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    public String callGemini(String prompt) {

        Part part = new Part(prompt);

        Content content = new Content(List.of(part));

        GeminiRequest request = new GeminiRequest(List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GeminiRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        URL + apiKey,
                        HttpMethod.POST,
                        entity,
                        String.class
                );

        try {

            GeminiResponse geminiResponse =
                    objectMapper.readValue(
                            response.getBody(),
                            GeminiResponse.class
                    );

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
}