package com.atharva.nutricheckai.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlaggedIngredient {

    private String ingredient;

    private String reason;

}