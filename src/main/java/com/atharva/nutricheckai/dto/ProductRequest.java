package com.atharva.nutricheckai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Barcode is required")
    private String barcode;

    @NotBlank(message = "Description is required")
    private String description;

    private String imageUrl;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
}