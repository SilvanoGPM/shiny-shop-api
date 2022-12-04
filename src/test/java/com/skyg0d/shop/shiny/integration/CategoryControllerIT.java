package com.skyg0d.shop.shiny.integration;

import com.skyg0d.shop.shiny.exception.details.ExceptionDetails;
import com.skyg0d.shop.shiny.model.Category;
import com.skyg0d.shop.shiny.payload.response.CategoryResponse;
import com.skyg0d.shop.shiny.payload.response.MessageResponse;
import com.skyg0d.shop.shiny.repository.CategoryRepository;
import com.skyg0d.shop.shiny.util.JWTCreator;
import com.skyg0d.shop.shiny.wrapper.PageableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static com.skyg0d.shop.shiny.util.category.CategoryCreator.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Integration tests for CategoryController")
public class CategoryControllerIT {

    @Autowired
    TestRestTemplate httpClient;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    JWTCreator jwtCreator;

    @Test
    @DisplayName("listAll Returns List Of Categories Inside Page Object When Successful")
    void listAll_ReturnsListOfCategoriesInsidePageObject_WhenSuccessful() {
        CategoryResponse expectedCategory = createCategoryResponse();

        categoryRepository.save(createCategory());

        ResponseEntity<PageableResponse<CategoryResponse>> entity = httpClient.exchange(
                "/categories",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getSlug()).isEqualTo(expectedCategory.getSlug());
    }

    @Test
    @DisplayName("findBySlug Returns Category When Successful")
    void findBySlug_ReturnsCategory_WhenSuccessful() {
        CategoryResponse expectedCategory = createCategoryResponse();

        categoryRepository.save(createCategory());

        ResponseEntity<CategoryResponse> entity = httpClient.exchange(
                "/categories/{slug}",
                HttpMethod.GET,
                null,
                CategoryResponse.class,
                expectedCategory.getSlug()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getSlug()).isEqualTo(expectedCategory.getSlug());
    }

    @Test
    @DisplayName("findBySlug Returns 400 BadRequest When Category Don't Exists")
    void findBySlug_Returns400BadRequest_WhenCategoryDoNotExists() {
        CategoryResponse expectedCategory = createCategoryResponse();

        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/categories/{slug}",
                HttpMethod.GET,
                null,
                ExceptionDetails.class,
                expectedCategory.getSlug()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("search Returns List Of Categories Inside Page Object When Successful")
    void search_ReturnsListOfCategoriesInsidePageObject_WhenSuccessful() {
        CategoryResponse expectedCategory = createCategoryResponse();

        categoryRepository.save(createCategory());

        ResponseEntity<PageableResponse<CategoryResponse>> entity = httpClient.exchange(
                String.format("/categories/search?name=%s", expectedCategory.getName()),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getSlug()).isEqualTo(expectedCategory.getSlug());
    }

    @Test
    @DisplayName("existsBySlug Returns 404 Not Found When Category Not Exists")
    void existsBySlug_Returns404NotFound_WhenCategoryNotExists() {
        ResponseEntity<Void> entity = httpClient.exchange(
                "/categories/{slug}",
                HttpMethod.HEAD,
                null,
                Void.class,
                "test-slug"
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("existsBySlug Returns 200 Ok When Slug Already Exists")
    void existsBySlug_Returns200Ok_WhenCategorySlugAlreadyExists() {
        Category categorySaved = categoryRepository.save(createCategory());

        ResponseEntity<Void> entity = httpClient.exchange(
                "/categories/{slug}",
                HttpMethod.GET,
                null,
                Void.class,
                categorySaved.getSlug()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("create Persists Category When Successful")
    void create_PersistsCategory_WhenSuccessful() {
        CategoryResponse expectedCategory = createCategoryResponse();

        ResponseEntity<CategoryResponse> entity = httpClient.exchange(
                "/categories",
                HttpMethod.POST,
                jwtCreator.createAdminAuthEntity(createCreateCategoryRequest()),
                CategoryResponse.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getSlug()).isEqualTo(expectedCategory.getSlug());
    }

    @Test
    @DisplayName("replace Updates Category When Successful")
    void replace_UpdatesCategory_WhenSuccessful() {
        categoryRepository.save(createCategory());

        ResponseEntity<Void> entity = httpClient.exchange(
                "/categories",
                HttpMethod.PUT,
                jwtCreator.createAdminAuthEntity(createReplaceCategoryRequest()),
                Void.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("delete Removes Category When Successful")
    void delete_RemovesCategory_WhenSuccessful() {
        Category categorySaved = categoryRepository.save(createCategory());

        ResponseEntity<Void> entity = httpClient.exchange(
                "/categories/{slug}",
                HttpMethod.DELETE,
                jwtCreator.createAdminAuthEntity(null),
                Void.class,
                categorySaved.getSlug()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

}
