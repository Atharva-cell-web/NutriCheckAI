package com.atharva.nutricheckai.service;

import com.atharva.nutricheckai.dto.AnalysisHistoryResponse;
import com.atharva.nutricheckai.entity.AnalysisHistory;
import com.atharva.nutricheckai.entity.User;
import com.atharva.nutricheckai.exception.ResourceNotFoundException;
import com.atharva.nutricheckai.repository.AnalysisHistoryRepository;
import com.atharva.nutricheckai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisHistoryService {

    private final AnalysisHistoryRepository analysisHistoryRepository;

    private final UserRepository userRepository;
    public List<AnalysisHistoryResponse> getMyHistory(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return analysisHistoryRepository.findByUserOrderByAnalyzedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    public AnalysisHistoryResponse getHistoryById(Long id) {

        AnalysisHistory history = analysisHistoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Analysis not found"));

        return mapToResponse(history);
    }
    public void deleteHistory(Long id) {

        AnalysisHistory history = analysisHistoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Analysis not found"));

        analysisHistoryRepository.delete(history);
    }
    private AnalysisHistoryResponse mapToResponse(
            AnalysisHistory history) {

        AnalysisHistoryResponse response = new AnalysisHistoryResponse();

        response.setId(history.getId());
        response.setProductName(history.getProductName());
        response.setIngredientList(history.getIngredientList());
        response.setRiskScore(history.getRiskScore());
        response.setOverallRisk(history.getOverallRisk());
        response.setAiResponse(history.getAiResponse());
        response.setAnalyzedAt(history.getAnalyzedAt());

        return response;
    }

}
