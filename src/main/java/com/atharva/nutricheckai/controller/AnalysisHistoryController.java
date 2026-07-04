package com.atharva.nutricheckai.controller;

import com.atharva.nutricheckai.dto.AnalysisHistoryResponse;
import com.atharva.nutricheckai.service.AnalysisHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class AnalysisHistoryController {

    private final AnalysisHistoryService analysisHistoryService;

    @GetMapping
    public List<AnalysisHistoryResponse> getMyHistory(
            Authentication authentication) {

        return analysisHistoryService.getMyHistory(authentication.getName());
    }

    @GetMapping("/{id}")
    public AnalysisHistoryResponse getHistoryById(
            @PathVariable Long id) {

        return analysisHistoryService.getHistoryById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteHistory(
            @PathVariable Long id) {

        analysisHistoryService.deleteHistory(id);

        return "History deleted successfully";
    }
}