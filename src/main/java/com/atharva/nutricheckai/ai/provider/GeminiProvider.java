package com.atharva.nutricheckai.ai.provider;

import com.atharva.nutricheckai.dto.ai.AnalysisResponse;
import org.springframework.stereotype.Component;

@Component
public class GeminiProvider implements AIProvider {

    @Override
    public AnalysisResponse analyze(String ingredients,
                                    String userProfile) {

        return new AnalysisResponse();
    }
}