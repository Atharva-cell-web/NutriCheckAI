package com.atharva.nutricheckai.controller;

import com.atharva.nutricheckai.dto.ai.AnalysisRequest;
import com.atharva.nutricheckai.dto.ai.AnalysisResponse;
import com.atharva.nutricheckai.ai.service.AIAnalysisService;
import com.atharva.nutricheckai.ocr.service.OCRService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIAnalysisService aiAnalysisService;
    private final OCRService ocrService;

    @PostMapping("/analyze")
    public AnalysisResponse analyze(
            @RequestBody AnalysisRequest request) {

        return aiAnalysisService.analyze(request);
    }

    @PostMapping(value = "/analyze-image", consumes = "multipart/form-data")
    public AnalysisResponse analyzeImage(
            @RequestParam("image") MultipartFile image) {
            
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image file is missing or empty");
        }

        String contentType = image.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new IllegalArgumentException("Unsupported file type. Only JPEG and PNG images are allowed.");
        }

        // 1. Extract raw text from the image
        String rawText = ocrService.extractText(image);
        
        // 2. Clean and parse into a list of ingredients
        List<String> ingredients = ocrService.extractIngredients(rawText);
        
        if (ingredients.isEmpty()) {
            throw new IllegalArgumentException("No ingredients could be extracted from the image");
        }
        
        // 3. Delegate to existing analysis pipeline
        AnalysisRequest request = new AnalysisRequest(ingredients);
        return aiAnalysisService.analyze(request);
    }
}