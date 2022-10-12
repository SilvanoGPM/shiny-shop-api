package com.skyg0d.shop.shiny.repository;

import com.skyg0d.shop.shiny.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static com.skyg0d.shop.shiny.util.product.ProductCreator.SLUG;
import static com.skyg0d.shop.shiny.util.product.ProductCreator.createProductToBeSave;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests for ProductRepository")
public class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    @DisplayName("findAllByActiveTrue Returns List Of Products Inside Page Object When Successful")
    void findAllByActiveTrue_ReturnsListOfProductsInsidePageObject_WhenSuccessful() {
        productRepository.save(createProductToBeSave());

        Page<Product> productsPage = productRepository.findAllByActiveTrue(PageRequest.of(0, 1));

        assertThat(productsPage).isNotEmpty();

        assertThat(productsPage.getContent()).isNotEmpty();

        assertThat(productsPage.getContent().get(0)).isNotNull();

        assertThat(productsPage.getContent().get(0).getSlug()).isEqualTo(SLUG);
    }

    @Test
    @DisplayName("findBySlug Returns Product When Successful")
    void findBySlug_ReturnsProduct_WhenSuccessful() {
        productRepository.save(createProductToBeSave());

        Optional<Product> categoryFound = productRepository.findBySlug(SLUG);

        assertThat(categoryFound).isNotEmpty();

        assertThat(categoryFound.get()).isNotNull();

        assertThat(categoryFound.get().getSlug()).isEqualTo(SLUG);
    }

    @Test
    @DisplayName("existsBySlug Returns True When Successful")
    void existsBySlug_ReturnsTrue_WhenSuccessful() {
        productRepository.save(createProductToBeSave());

        Boolean categoryExists = productRepository.existsBySlug(SLUG);

        assertThat(categoryExists).isTrue();
    }

    @Test
    @DisplayName("existsBySlug Returns False When Category don't Exists")
    void existsBySlug_ReturnsFalse_WhenDoNotExists() {
        Boolean categoryExists = productRepository.existsBySlug(SLUG);

        assertThat(categoryExists).isFalse();
    }

}
