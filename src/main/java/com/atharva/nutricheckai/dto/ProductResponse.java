package com.atharva.nutricheckai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;

    private String name;

    private String brand;

    private String barcode;

    private String description;

    private String imageUrl;

    private String categoryName;
}