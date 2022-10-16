package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.exception.SlugAlreadyExistsException;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.payload.request.ApplyDiscountRequest;
import com.skyg0d.shop.shiny.payload.request.ChangeAmountRequest;
import com.skyg0d.shop.shiny.payload.request.CreateProductRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceProductRequest;
import com.skyg0d.shop.shiny.payload.response.AdminProductResponse;
import com.skyg0d.shop.shiny.payload.response.MessageResponse;
import com.skyg0d.shop.shiny.payload.response.UserProductResponse;
import com.skyg0d.shop.shiny.service.ProductService;
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
                .when(productService.create(ArgumentMatchers.any(CreateProductRequest.class)))
                .thenReturn(createUserProductResponse());

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
                .applyDiscount(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());

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
    @DisplayName("existsBySlug Says Product Don't Exists When Successful")
    void existsBySlug_SaysProductDoNotExists_WhenSuccessful() {
        BDDMockito
                .doNothing()
                .when(productService)
                .verifySlugExists(ArgumentMatchers.anyString());

        String expectedMessage = "Product don't exists";

        ResponseEntity<MessageResponse> entity = productController.existsBySlug("test-slug");

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("existsBySlug Says Product Exists When Product Slug Already Exists")
    void existsBySlug_SaysProductExists_WhenProductSlugAlreadyExists() {
        BDDMockito
                .doThrow(SlugAlreadyExistsException.class)
                .when(productService)
                .verifySlugExists(ArgumentMatchers.anyString());

        String expectedMessage = "Product exists";

        ResponseEntity<MessageResponse> entity = productController.existsBySlug("test-slug");

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("create Persists Product When Successful")
    void create_PersistsProduct_WhenSuccessful() {
        UserProductResponse expectedProduct = createUserProductResponse();

        ResponseEntity<UserProductResponse> entity = productController.create(createCreateProductRequest());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getSlug()).isEqualTo(expectedProduct.getSlug());
    }

    @Test
    @DisplayName("replace Updates Product When Successful")
    void replace_UpdatesProduct_WhenSuccessful() {
        String expectedMessage = "Product replaced!";

        ResponseEntity<MessageResponse> entity = productController.replace(createReplaceProductRequest());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("toggleActive Alternates Active When Successful")
    void toggleActive_AlternatesActive_WhenSuccessful() {
        String expectedMessage = "Product visibility toggle!";

        ResponseEntity<MessageResponse> entity = productController.toggleActive("test-slug");

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("applyDiscount Applies Discount To Product When Successful")
    void applyDiscount_AppliesDiscountToProduct_WhenSuccessful() {
        String expectedMessage = "Product discount applied!";

        ResponseEntity<MessageResponse> entity = productController.applyDiscount("test-slug", new ApplyDiscountRequest(10));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("changeAmount Updates Product Amount When Successful")
    void changeAmount_UpdatesProductAmount_WhenSuccessful() {
        String expectedMessage = "Product amount changed!";

        ResponseEntity<MessageResponse> entity = productController.changeAmount("test-slug", new ChangeAmountRequest(10));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("addCategory Append Category To Product When Successful")
    void addCategory_AppendCategoryToProduct_WhenSuccessful() {
        String expectedMessage = "Add category to product!";

        ResponseEntity<MessageResponse> entity = productController.addCategory("test-product-slug", "test-category-slug");

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("removeCategory Removes Category To Product When Successful")
    void removeCategory_RemovesCategoryToProduct_WhenSuccessful() {
        String expectedMessage = "Remove product category!";

        ResponseEntity<MessageResponse> entity = productController.removeCategory("test-product-slug", "test-category-slug");

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("delete Removes Product When Successful")
    void delete_RemovesProduct_WhenSuccessful() {
        String expectedMessage = "Product removed!";

        ResponseEntity<MessageResponse> entity = productController.delete("test-slug");

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);
    }

}
