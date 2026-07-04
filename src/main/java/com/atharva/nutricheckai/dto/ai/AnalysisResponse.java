package com.atharva.nutricheckai.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResponse {

    private Integer riskScore;

    private String overallRisk;

    private List<FlaggedIngredient> flaggedIngredients;

    private String summary;

    private String recommendation;
}