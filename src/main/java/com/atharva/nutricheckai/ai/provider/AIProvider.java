package com.atharva.nutricheckai.ai.provider;

import com.atharva.nutricheckai.dto.ai.AnalysisResponse;

public interface AIProvider {

    AnalysisResponse analyze(
            String ingredients,
            String userProfile
    );

}