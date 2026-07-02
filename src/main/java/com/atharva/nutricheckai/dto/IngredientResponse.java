package com.atharva.nutricheckai.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientResponse {

    private Long id;

    private String name;

    private String description;

    private String safetyStatus;

    private String category;

    private String source;

    private String notes;
}