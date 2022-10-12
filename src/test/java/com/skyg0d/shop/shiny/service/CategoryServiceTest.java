package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.ResourceNotFoundException;
import com.skyg0d.shop.shiny.exception.SlugAlreadyExistsException;
import com.skyg0d.shop.shiny.model.Category;
import com.skyg0d.shop.shiny.payload.response.CategoryResponse;
import com.skyg0d.shop.shiny.repository.CategoryRepository;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static com.skyg0d.shop.shiny.util.category.CategoryCreator.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for CategoryService")
public class CategoryServiceTest {

    @InjectMocks
    CategoryService categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        PageImpl<Category> categoryPage = new PageImpl<>(List.of(createCategory()));

        BDDMockito
                .when(categoryRepository.findAll(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(categoryPage);

        BDDMockito
                .when(categoryRepository.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(createCategory()));

        BDDMockito
                .when(categoryRepository.existsBySlug(ArgumentMatchers.anyString()))
                .thenReturn(false);

        BDDMockito
                .when(categoryRepository.save(ArgumentMatchers.any(Category.class)))
                .thenReturn(createCategory());

        BDDMockito
                .doNothing()
                .when(categoryRepository)
                .delete(ArgumentMatchers.any(Category.class));
    }

    @Test
    @DisplayName("listAll Returns List Of Categories Inside Page Object When Successful")
    void listAll_ReturnsListOfCategoriesInsidePageObject_WhenSuccessful() {
        CategoryResponse expectedCategory = createCategoryResponse();

        Page<CategoryResponse> categoriesPage = categoryService.listAll(PageRequest.of(0, 1));

        assertThat(categoriesPage).isNotEmpty();

        assertThat(categoriesPage.getContent()).isNotEmpty();

        assertThat(categoriesPage.getContent().get(0)).isNotNull();

        assertThat(categoriesPage.getContent().get(0).getSlug()).isEqualTo(expectedCategory.getSlug());
    }

    @Test
    @DisplayName("findBySlug Returns Category When Successful")
    void findBySlug_ReturnsCategory_WhenSuccessful() {
        Category expectedCategory = createCategory();

        Category categoryFound = categoryService.findBySlug(expectedCategory.getSlug());

        assertThat(categoryFound).isNotNull();

        assertThat(categoryFound.getSlug()).isEqualTo(expectedCategory.getSlug());
    }

    @Test
    @DisplayName("findBySlug Throws ResourceNotFoundException When Category Don't Exists")
    void findBySlug_ThrowsResourceNotFoundException_WhenCategoryDoNotExists() {
        BDDMockito
                .when(categoryRepository.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> categoryService.findBySlug("test-slug"));
    }

    @Test
    @DisplayName("findBySlugMapped Returns Category When Successful")
    void findBySlugMapped_ReturnsCategory_WhenSuccessful() {
        CategoryResponse expectedCategory = createCategoryResponse();

        CategoryResponse categoryFound = categoryService.findBySlugMapped(expectedCategory.getSlug());

        assertThat(categoryFound).isNotNull();

        assertThat(categoryFound.getSlug()).isEqualTo(expectedCategory.getSlug());
    }

    @Test
    @DisplayName("findBySlugMapped Throws ResourceNotFoundException When Category Don't Exists")
    void findBySlugMapped_ThrowsResourceNotFoundException_WhenCategoryDoNotExists() {
        BDDMockito
                .when(categoryRepository.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> categoryService.findBySlug("test-slug"));
    }

    @Test
    @DisplayName("verifySlugExists Verify If Slug Exists When Successful")
    void verifySlugExists_VerifyIfSlugExists_WhenSuccessful() {
        assertThatCode(() -> categoryService.verifySlugExists("test-slug"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("verifySlugExists Throws SlugAlreadyExistsException When Slug Already Exists")
    void verifySlugExists_ThrowsSlugAlreadyExistsException_WhenSlugAlreadyExists() {
        BDDMockito
                .when(categoryRepository.existsBySlug(ArgumentMatchers.anyString()))
                .thenReturn(true);

        assertThatExceptionOfType(SlugAlreadyExistsException.class)
                .isThrownBy(() -> categoryService.verifySlugExists("test-slug"));
    }

    @Test
    @DisplayName("create Persists Category When Successful")
    void create_PersistsCategory_WhenSuccessful() {
        CategoryResponse expectedCategory = createCategoryResponse();

        CategoryResponse categoryFound = categoryService.create(createCreateCategoryRequest());

        assertThat(categoryFound).isNotNull();

        assertThat(categoryFound.getSlug()).isEqualTo(expectedCategory.getSlug());
    }

    @Test
    @DisplayName("replace Updates Category When Successful")
    void replace_UpdatesCategory_WhenSuccessful() {
        assertThatCode(() -> categoryService.replace(createReplaceCategoryRequest()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("delete Removes Category When Successful")
    void delete_RemovesCategory_WhenSuccessful() {
        assertThatCode(() -> categoryService.delete("test-slug"))
                .doesNotThrowAnyException();
    }

}
