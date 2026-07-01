package com.atharva.nutricheckai.service;

import com.atharva.nutricheckai.dto.ProductRequest;
import com.atharva.nutricheckai.dto.ProductResponse;
import com.atharva.nutricheckai.entity.Category;
import com.atharva.nutricheckai.entity.Product;
import com.atharva.nutricheckai.exception.DuplicateResourceException;
import com.atharva.nutricheckai.exception.ResourceNotFoundException;
import com.atharva.nutricheckai.repository.CategoryRepository;
import com.atharva.nutricheckai.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductResponse createProduct(ProductRequest request) {

        if (productRepository.existsByBarcode(request.getBarcode())) {
            throw new RuntimeException("Product already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        Product product = new Product();

        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setBarcode(request.getBarcode());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }
    private ProductResponse mapToResponse(Product product) {

        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setBrand(product.getBrand());
        response.setBarcode(product.getBarcode());
        response.setDescription(product.getDescription());
        response.setImageUrl(product.getImageUrl());
        response.setCategoryName(product.getCategory().getName());

        return response;
    }
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
        new ResourceNotFoundException("Product not found"));

        return mapToResponse(product);
    }
    public ProductResponse updateProduct(Long id,
                                         ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        if (!product.getBarcode().equals(request.getBarcode())
                && productRepository.existsByBarcode(request.getBarcode())) {

            throw new DuplicateResourceException("Barcode already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
        new ResourceNotFoundException("Category not found"));

        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setBarcode(request.getBarcode());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        return mapToResponse(updatedProduct);
    }
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        productRepository.delete(product);
    }

}