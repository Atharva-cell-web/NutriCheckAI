package com.atharva.nutricheckai.service;

import com.atharva.nutricheckai.dto.CategoryRequest;
import com.atharva.nutricheckai.dto.CategoryResponse;
import com.atharva.nutricheckai.entity.Category;
import com.atharva.nutricheckai.exception.DuplicateResourceException;
import com.atharva.nutricheckai.exception.ResourceNotFoundException;
import com.atharva.nutricheckai.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    public CategoryResponse createCategory(CategoryRequest request) {

        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category already exists");
        }

        Category category = new Category();

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category savedCategory = categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }
    private CategoryResponse mapToResponse(Category category) {

        CategoryResponse response = new CategoryResponse();

        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());

        return response;
    }
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        return mapToResponse(category);
    }
    public CategoryResponse updateCategory(Long id,
                                           CategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        if (!category.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByName(request.getName())) {

            throw new RuntimeException("Category already exists");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updatedCategory =
                categoryRepository.save(category);

        return mapToResponse(updatedCategory);
    }
    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        categoryRepository.delete(category);
    }

}