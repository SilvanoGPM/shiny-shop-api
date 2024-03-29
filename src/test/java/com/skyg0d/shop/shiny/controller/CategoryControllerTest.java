package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.exception.SlugAlreadyExistsException;
import com.skyg0d.shop.shiny.model.Category;
import com.skyg0d.shop.shiny.payload.request.CreateCategoryRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceCategoryRequest;
import com.skyg0d.shop.shiny.payload.response.CategoryResponse;
import com.skyg0d.shop.shiny.payload.search.CategoryParameterSearch;
import com.skyg0d.shop.shiny.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static com.skyg0d.shop.shiny.util.category.CategoryCreator.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for CategoryController")
public class CategoryControllerTest {

    @InjectMocks
    CategoryController categoryController;

    @Mock
    CategoryService categoryService;

    @BeforeEach
    void setUp() {
        PageImpl<CategoryResponse> categoryPage = new PageImpl<>(List.of(createCategoryResponse()));

        BDDMockito
                .when(categoryService.listAll(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(categoryPage);

        BDDMockito
                .when(categoryService.findBySlugMapped(ArgumentMatchers.anyString()))
                .thenReturn(createCategoryResponse());

        BDDMockito
                .when(categoryService.search(ArgumentMatchers.any(CategoryParameterSearch.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(categoryPage);

        BDDMockito
                .when(categoryService.create(ArgumentMatchers.any(CreateCategoryRequest.class)))
                .thenReturn(createCategoryResponse());

        BDDMockito
                .doNothing()
                .when(categoryService)
                .replace(ArgumentMatchers.any(ReplaceCategoryRequest.class));

        BDDMockito
                .doNothing()
                .when(categoryService)
                .delete(ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("listAll Returns List Of Categories Inside Page Object When Successful")
    @SuppressWarnings("null")
    void listAll_ReturnsListOfCategoriesInsidePageObject_WhenSuccessful() {
        CategoryResponse expectedCategory = createCategoryResponse();

        ResponseEntity<Page<CategoryResponse>> entity = categoryController.listAll(PageRequest.of(0, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getSlug()).isEqualTo(expectedCategory.getSlug());
    }

    @Test
    @DisplayName("findBySlug Returns Category When Successful")
    @SuppressWarnings("null")
    void findBySlug_ReturnsCategory_WhenSuccessful() {
        Category expectedCategory = createCategory();

        ResponseEntity<CategoryResponse> entity = categoryController.findBySlug(expectedCategory.getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getSlug()).isEqualTo(expectedCategory.getSlug());
    }

    @Test
    @DisplayName("search Returns List Of Categories Inside Page Object When Successful")
    @SuppressWarnings("null")
    void search_ReturnsListOfCategoriesInsidePageObject_WhenSuccessful() {
        CategoryResponse expectedCategory = createCategoryResponse();

        ResponseEntity<Page<CategoryResponse>> entity = categoryController.search(createCategortyParameterSearch(), PageRequest.of(0, 1));

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
        BDDMockito
                .doNothing()
                .when(categoryService)
                .verifySlugExists(ArgumentMatchers.anyString());

        ResponseEntity<Void> entity = categoryController.existsBySlug("test-slug");

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("existsBySlug Returns 200 Ok When Slug Already Exists")
    void existsBySlug_Returns200Ok_WhenCategorySlugAlreadyExists() {
        BDDMockito
                .doThrow(SlugAlreadyExistsException.class)
                .when(categoryService)
                .verifySlugExists(ArgumentMatchers.anyString());

        ResponseEntity<Void> entity = categoryController.existsBySlug("test-slug");

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("create Persists Category When Successful")
    @SuppressWarnings("null")
    void create_PersistsCategory_WhenSuccessful() {
        CategoryResponse expectedCategory = createCategoryResponse();

        ResponseEntity<CategoryResponse> entity = categoryController.create(createCreateCategoryRequest());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getSlug()).isEqualTo(expectedCategory.getSlug());
    }

    @Test
    @DisplayName("replace Updates Category When Successful")
    void replace_UpdatesCategory_WhenSuccessful() {
        ResponseEntity<Void> entity = categoryController.replace(createReplaceCategoryRequest());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("delete Removes Category When Successful")
    void delete_RemovesCategory_WhenSuccessful() {

        ResponseEntity<Void> entity = categoryController.delete("test-slug");

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();

    }

}
