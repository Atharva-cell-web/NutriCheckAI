package com.atharva.nutricheckai.ai.service;



import com.atharva.nutricheckai.ai.client.GeminiClient;
import com.atharva.nutricheckai.ai.parser.GeminiResponseParser;
import com.atharva.nutricheckai.ai.prompt.PromptBuilder;
import com.atharva.nutricheckai.dto.ai.AnalysisRequest;
import com.atharva.nutricheckai.dto.ai.AnalysisResponse;
import com.atharva.nutricheckai.entity.AnalysisHistory;
import com.atharva.nutricheckai.entity.User;
import com.atharva.nutricheckai.entity.UserProfile;
import com.atharva.nutricheckai.exception.ResourceNotFoundException;
import com.atharva.nutricheckai.repository.AnalysisHistoryRepository;
import com.atharva.nutricheckai.repository.UserProfileRepository;
import com.atharva.nutricheckai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIAnalysisService {
    private final UserRepository userRepository;

    private final PromptBuilder promptBuilder;

    private final GeminiClient geminiClient;

    private final GeminiResponseParser parser;

    private final UserProfileRepository userProfileRepository;

    private final AnalysisHistoryRepository analysisHistoryRepository;

    public AnalysisResponse analyze(AnalysisRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User profile not found"));

        String prompt = promptBuilder.buildPrompt(
                profile,
                request.getIngredients()
        );
        String aiResponse = geminiClient.callGemini(prompt);
        log.debug("========== GEMINI RESPONSE ==========\n{}\n=====================================", aiResponse);
        AnalysisResponse response = parser.parse(aiResponse);

        saveAnalysis(user, request, response);
        return  response;
    }
    private void saveAnalysis(
            User user,
            AnalysisRequest request,
            AnalysisResponse response) {

        AnalysisHistory history = new AnalysisHistory();

        history.setUser(user);

        history.setProductName("Manual Ingredient Analysis");

        history.setIngredientList(
                String.join(", ", request.getIngredients())
        );

        history.setRiskScore(response.getRiskScore());

        history.setOverallRisk(response.getOverallRisk());

        history.setAiResponse(response.getSummary());

        analysisHistoryRepository.save(history);
    }
}