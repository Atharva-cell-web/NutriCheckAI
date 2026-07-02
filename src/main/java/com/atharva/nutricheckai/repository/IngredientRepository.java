package com.atharva.nutricheckai.repository;

import com.atharva.nutricheckai.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    Optional<Ingredient> findByName(String name);

    boolean existsByName(String name);

    List<Ingredient> findByNameContainingIgnoreCase(String name);

    List<Ingredient> findByCategory(String category);
}