package com.skyg0d.shop.shiny.repository;

import com.skyg0d.shop.shiny.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Boolean existsBySlug(String slug);

    Page<Product> findAllByActiveTrue(Pageable pageable);

    Optional<Product> findBySlug(String slug);

}
