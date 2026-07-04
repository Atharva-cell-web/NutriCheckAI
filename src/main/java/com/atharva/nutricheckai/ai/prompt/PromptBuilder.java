package com.atharva.nutricheckai.ai.prompt;

import com.atharva.nutricheckai.entity.UserProfile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptBuilder {

    public String buildPrompt(UserProfile profile, List<String> ingredients) {

        return """
You are an expert cosmetic and food ingredient analyzer.

Analyze the product using:

USER PROFILE:
Age: %d
Gender: %s
Skin Type: %s
Diet Preference: %s
Allergies: %s
Medical Conditions: %s
Pregnant: %s

INGREDIENTS:
%s

Tasks:

1. Estimate a risk score (0-100).

2. Classify as:
SAFE
MODERATE
AVOID

3. Explain why.

4. Mention ingredients contributing to the risk.

5. Mention important regulatory observations if any
(EU / FDA / India).

6. Suggest safer alternatives.

Return ONLY valid JSON
Return ONLY valid JSON.

Do NOT use markdown.
Do NOT use ```json.
Do NOT write any explanation outside the JSON.

Use EXACTLY this schema:

{
  "riskScore": 0,
  "overallRisk": "SAFE | MODERATE | AVOID",
  "flaggedIngredients": [
    {
      "ingredient": "",
      "reason": ""
    }
  ],
  "summary": "",
  "recommendation": ""
}.
"""
                .formatted(
                        profile.getAge(),
                        profile.getGender(),
                        profile.getSkinType(),
                        profile.getDietPreference(),
                        profile.getAllergies(),
                        profile.getMedicalConditions(),
                        profile.getPregnant(),
                        String.join(", ", ingredients)
                );
    }
}