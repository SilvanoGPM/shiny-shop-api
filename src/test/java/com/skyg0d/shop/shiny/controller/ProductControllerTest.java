package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.exception.SlugAlreadyExistsException;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.payload.ApplyDiscountParams;
import com.skyg0d.shop.shiny.payload.request.ChangeAmountRequest;
import com.skyg0d.shop.shiny.payload.request.CreateProductRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceProductRequest;
import com.skyg0d.shop.shiny.payload.response.AdminProductResponse;
import com.skyg0d.shop.shiny.payload.response.UserProductResponse;
import com.skyg0d.shop.shiny.payload.search.ProductParametersSearch;
import com.skyg0d.shop.shiny.service.ProductService;
import lombok.SneakyThrows;
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

import static com.skyg0d.shop.shiny.util.product.ProductCreator.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for ProductController")
public class ProductControllerTest {

    @InjectMocks
    ProductController productController;

    @Mock
    ProductService productService;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        PageImpl<UserProductResponse> userProductsPage = new PageImpl<>(List.of(createUserProductResponse()));
        PageImpl<AdminProductResponse> adminProductsPage = new PageImpl<>(List.of(createAdminProductResponse()));

        BDDMockito
                .when(productService.listAllActive(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(userProductsPage);

        BDDMockito
                .when(productService.listAll(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(adminProductsPage);

        BDDMockito
                .when(productService.findBySlugMapped(ArgumentMatchers.anyString()))
                .thenReturn(createUserProductResponse());

        BDDMockito
                .when(productService.search(ArgumentMatchers.any(ProductParametersSearch.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(userProductsPage);

        BDDMockito
                .when(productService.create(ArgumentMatchers.any(CreateProductRequest.class)))
                .thenReturn(createAdminProductResponse());

        BDDMockito
                .doNothing()
                .when(productService)
                .replace(ArgumentMatchers.any(ReplaceProductRequest.class));

        BDDMockito
                .doNothing()
                .when(productService)
                .toggleActive(ArgumentMatchers.anyString());

        BDDMockito
                .doNothing()
                .when(productService)
                .applyDiscount(ArgumentMatchers.any(ApplyDiscountParams.class));

        BDDMockito
                .doNothing()
                .when(productService)
                .changeAmount(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());

        BDDMockito
                .doNothing()
                .when(productService)
                .addCategory(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        BDDMockito
                .doNothing()
                .when(productService)
                .removeCategory(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        BDDMockito
                .doNothing()
                .when(productService)
                .delete(ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("listAllActive Returns List Of Products Inside Page Object When Successful")
    void listAllActive_ReturnsListOfProductsInsidePageObject_WhenSuccessful() {
        UserProductResponse expectedProduct = createUserProductResponse();

        ResponseEntity<Page<UserProductResponse>> entity = productController.listAllActive(PageRequest.of(0, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getSlug()).isEqualTo(expectedProduct.getSlug());
    }

    @Test
    @DisplayName("listAll Returns List Of Products Inside Page Object When Successful")
    void listAll_ReturnsListOfProductsInsidePageObject_WhenSuccessful() {
        AdminProductResponse expectedProduct = createAdminProductResponse();

        ResponseEntity<Page<AdminProductResponse>> entity = productController.listAll(PageRequest.of(0, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getSlug()).isEqualTo(expectedProduct.getSlug());
    }

    @Test
    @DisplayName("findBySlug Returns Product When Successful")
    void findBySlug_ReturnsProduct_WhenSuccessful() {
        Product expectedProduct = createProduct();

        ResponseEntity<UserProductResponse> entity = productController.findBySlug(expectedProduct.getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getSlug()).isEqualTo(expectedProduct.getSlug());
    }

    @Test
    @DisplayName("search Returns List Of Products Inside Page Object When Successful")
    void search_ReturnsListOfProductsInsidePageObject_WhenSuccessful() {
        UserProductResponse expectedProduct = createUserProductResponse();

        ResponseEntity<Page<UserProductResponse>> entity = productController.search(createProductParametersSearch(), PageRequest.of(0, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getSlug()).isEqualTo(expectedProduct.getSlug());
    }

    @Test
    @DisplayName("existsBySlug Returns 404 NotFound When Product Don't Exists")
    void existsBySlug_Returns400NotFound_WhenProductDoNotExists() {
        BDDMockito
                .doNothing()
                .when(productService)
                .verifySlugExists(ArgumentMatchers.anyString());

        ResponseEntity<Void> entity = productController.existsBySlug("test-slug");

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("existsBySlug Returns 200 Ok When Product Exists")
    void existsBySlug_Returns200Ok_WhenProductExists() {
        BDDMockito
                .doThrow(SlugAlreadyExistsException.class)
                .when(productService)
                .verifySlugExists(ArgumentMatchers.anyString());

        ResponseEntity<Void> entity = productController.existsBySlug("test-slug");

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("create Persists Product When Successful")
    @SneakyThrows
    void create_PersistsProduct_WhenSuccessful() {
        UserProductResponse expectedProduct = createUserProductResponse();

        ResponseEntity<AdminProductResponse> entity = productController.create(createCreateProductRequest());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getSlug()).isEqualTo(expectedProduct.getSlug());
    }

    @Test
    @DisplayName("replace Updates Product When Successful")
    @SneakyThrows
    void replace_UpdatesProduct_WhenSuccessful() {
        ResponseEntity<Void> entity = productController.replace(createReplaceProductRequest());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("toggleActive Alternates Active When Successful")
    @SneakyThrows
    void toggleActive_AlternatesActive_WhenSuccessful() {
        ResponseEntity<Void> entity = productController.toggleActive("test-slug");

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("applyDiscount Applies Discount To Product When Successful")
    @SneakyThrows
    void applyDiscount_AppliesDiscountToProduct_WhenSuccessful() {
        ResponseEntity<Void> entity = productController.applyDiscount(SLUG, createApplyDiscountRequest());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("removeDiscount Removes Discount To Product When Successful")
    @SneakyThrows
    void removeDiscount_RemovesDiscountToProduct_WhenSuccessful() {
        ResponseEntity<Void> entity = productController.removeDiscount(SLUG);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("changeAmount Updates Product Amount When Successful")
    void changeAmount_UpdatesProductAmount_WhenSuccessful() {
        ResponseEntity<Void> entity = productController.changeAmount("test-slug", new ChangeAmountRequest(10));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("addCategory Append Category To Product When Successful")
    @SneakyThrows
    void addCategory_AppendCategoryToProduct_WhenSuccessful() {
        ResponseEntity<Void> entity = productController.addCategory("test-product-slug", "test-category-slug");

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("removeCategory Removes Category To Product When Successful")
    @SneakyThrows
    void removeCategory_RemovesCategoryToProduct_WhenSuccessful() {
        ResponseEntity<Void> entity = productController.removeCategory("test-product-slug", "test-category-slug");

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("delete Removes Product When Successful")
    @SneakyThrows
    void delete_RemovesProduct_WhenSuccessful() {
        ResponseEntity<Void> entity = productController.delete("test-slug");

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();
    }

}
