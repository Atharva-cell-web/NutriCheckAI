package com.atharva.nutricheckai.controller;

import com.atharva.nutricheckai.ai.client.GeminiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AITestController {

    private final GeminiClient geminiClient;

    @GetMapping("/api/test-ai")
    public String testAI() {

        return geminiClient.callGemini(
                "Say hello from Gemini in one sentence."
        );
    }
}