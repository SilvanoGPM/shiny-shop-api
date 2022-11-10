package com.skyg0d.shop.shiny.repository;

import com.skyg0d.shop.shiny.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID>, JpaSpecificationExecutor<Category> {

    Optional<Category> findBySlug(String slug);

    Boolean existsBySlug(String slug);

}
