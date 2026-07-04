package com.atharva.nutricheckai.ai.parser;

import com.atharva.nutricheckai.dto.ai.AnalysisResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeminiResponseParser {

    private final ObjectMapper objectMapper;

    public AnalysisResponse parse(String aiResponse) {

        try {
            return objectMapper.readValue(aiResponse, AnalysisResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response", e);
        }
    }
}