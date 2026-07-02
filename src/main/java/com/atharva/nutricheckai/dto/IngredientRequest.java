package com.atharva.nutricheckai.dto;

import com.atharva.nutricheckai.enums.IngredientCategory;
import com.atharva.nutricheckai.enums.SafetyStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientRequest {

    @NotBlank(message = "Ingredient name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Safety status is required")
    private SafetyStatus safetyStatus;

    @NotBlank(message = "Category is required")
    private IngredientCategory category;


    

    @NotBlank(message = "Source is required")
    private String source;

    private String notes;
}