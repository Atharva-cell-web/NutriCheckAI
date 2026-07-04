package com.atharva.nutricheckai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientResponse {

    private Long id;

    private String name;

    private String description;

    private String category;
}