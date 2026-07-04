package com.atharva.nutricheckai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisHistoryResponse {

    private Long id;

    private String productName;

    private String ingredientList;

    private Integer riskScore;

    private String overallRisk;

    private String aiResponse;

    private LocalDateTime analyzedAt;
}