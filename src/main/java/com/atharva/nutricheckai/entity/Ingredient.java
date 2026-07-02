package com.atharva.nutricheckai.entity;

import com.atharva.nutricheckai.enums.IngredientCategory;
import com.atharva.nutricheckai.enums.SafetyStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ingredients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private SafetyStatus safetyStatus;

    @Enumerated(EnumType.STRING)
    private IngredientCategory category;

    private String source;

    @Column(length = 2000)
    private String notes;
}