package com.atharva.nutricheckai.entity;

import com.atharva.nutricheckai.enums.RegulatoryStatus;
import com.atharva.nutricheckai.enums.SafetyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ingredient_safety")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientSafety {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ingredient_id", nullable = false, unique = true)
    private Ingredient ingredient;

    @Enumerated(EnumType.STRING)
    private RegulatoryStatus indiaStatus;

    @Enumerated(EnumType.STRING)
    private RegulatoryStatus fdaStatus;

    @Enumerated(EnumType.STRING)
    private RegulatoryStatus euStatus;

    @Enumerated(EnumType.STRING)
    private SafetyStatus pregnancySafety;

    @Enumerated(EnumType.STRING)
    private SafetyStatus skinSafety;

    @Enumerated(EnumType.STRING)
    private SafetyStatus foodSafety;

    private Integer riskScore;

    @Column(length = 3000)
    private String scientificNotes;

    private String referenceSource;

    private LocalDateTime lastUpdated;

    @PrePersist
    public void prePersist() {
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}