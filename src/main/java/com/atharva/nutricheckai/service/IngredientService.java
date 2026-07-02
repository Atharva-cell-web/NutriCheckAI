package com.atharva.nutricheckai.service;

import com.atharva.nutricheckai.dto.IngredientRequest;
import com.atharva.nutricheckai.dto.IngredientResponse;
import com.atharva.nutricheckai.entity.Ingredient;
import com.atharva.nutricheckai.exception.DuplicateResourceException;
import com.atharva.nutricheckai.exception.ResourceNotFoundException;
import com.atharva.nutricheckai.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientResponse createIngredient(IngredientRequest request) {

        if (ingredientRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Ingredient already exists");
        }

        Ingredient ingredient = new Ingredient();

        ingredient.setName(request.getName());
        ingredient.setDescription(request.getDescription());
        ingredient.setSafetyStatus(request.getSafetyStatus());
        ingredient.setCategory(request.getCategory());
        ingredient.setSource(request.getSource());
        ingredient.setNotes(request.getNotes());

        Ingredient savedIngredient = ingredientRepository.save(ingredient);

        return mapToResponse(savedIngredient);
    }
    public List<IngredientResponse> getAllIngredients() {

        return ingredientRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    public IngredientResponse getIngredientById(Long id) {

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ingredient not found"));

        return mapToResponse(ingredient);
    }
    public IngredientResponse updateIngredient(Long id,
                                               IngredientRequest request) {

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ingredient not found"));

        if (!ingredient.getName().equalsIgnoreCase(request.getName())
                && ingredientRepository.existsByName(request.getName())) {

            throw new DuplicateResourceException("Ingredient already exists");
        }

        ingredient.setName(request.getName());
        ingredient.setDescription(request.getDescription());
        ingredient.setSafetyStatus(request.getSafetyStatus());
        ingredient.setCategory(request.getCategory());
        ingredient.setSource(request.getSource());
        ingredient.setNotes(request.getNotes());

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);

        return mapToResponse(updatedIngredient);
    }
    public void deleteIngredient(Long id) {

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ingredient not found"));

        ingredientRepository.delete(ingredient);
    }
    private IngredientResponse mapToResponse(Ingredient ingredient) {

        IngredientResponse response = new IngredientResponse();

        response.setId(ingredient.getId());
        response.setName(ingredient.getName());
        response.setDescription(ingredient.getDescription());
        response.setSafetyStatus(ingredient.getSafetyStatus());
        response.setCategory(ingredient.getCategory());
        response.setSource(ingredient.getSource());
        response.setNotes(ingredient.getNotes());

        return response;
    }
}