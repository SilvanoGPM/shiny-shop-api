package com.skyg0d.shop.shiny.repository;

import com.skyg0d.shop.shiny.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static com.skyg0d.shop.shiny.util.category.CategoryCreator.SLUG;
import static com.skyg0d.shop.shiny.util.category.CategoryCreator.createCategoryToBeSave;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests for CategoryRepository")
public class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("findBySlug returns category when successful")
    void findBySlug_ReturnsCategory_WhenSuccessful() {
        categoryRepository.save(createCategoryToBeSave());

        Optional<Category> categoryFound = categoryRepository.findBySlug(SLUG);

        assertThat(categoryFound).isNotEmpty();

        assertThat(categoryFound.get()).isNotNull();

        assertThat(categoryFound.get().getSlug()).isEqualTo(SLUG);
    }

    @Test
    @DisplayName("existsBySlug returns true when successful")
    void existsBySlug_ReturnsTrue_WhenSuccessful() {
        categoryRepository.save(createCategoryToBeSave());

        Boolean categoryExists = categoryRepository.existsBySlug(SLUG);

        assertThat(categoryExists).isTrue();
    }

    @Test
    @DisplayName("existsBySlug returns false when category do not exists")
    void existsBySlug_ReturnsFalse_WhenDoNotExists() {
        Boolean categoryExists = categoryRepository.existsBySlug(SLUG);

        assertThat(categoryExists).isFalse();
    }

}
