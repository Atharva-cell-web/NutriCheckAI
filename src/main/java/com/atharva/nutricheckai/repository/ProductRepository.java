package com.atharva.nutricheckai.repository;

import com.atharva.nutricheckai.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByBarcode(String barcode);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByBrandContainingIgnoreCase(String brand);

    boolean existsByBarcode(String barcode);

}