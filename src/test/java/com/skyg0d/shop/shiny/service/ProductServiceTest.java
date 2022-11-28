package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.ProductCategoryNotFoundException;
import com.skyg0d.shop.shiny.exception.ResourceNotFoundException;
import com.skyg0d.shop.shiny.exception.SlugAlreadyExistsException;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.payload.request.CreateProductRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceProductRequest;
import com.skyg0d.shop.shiny.payload.response.AdminProductResponse;
import com.skyg0d.shop.shiny.payload.response.UserProductResponse;
import com.skyg0d.shop.shiny.repository.ProductRepository;
import com.skyg0d.shop.shiny.util.category.CategoryCreator;
import com.stripe.model.Price;
import com.stripe.param.ProductUpdateParams;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.skyg0d.shop.shiny.util.product.ProductCreator.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for ProductService")
public class ProductServiceTest {

    @InjectMocks
    ProductService productService;

    @Mock
    ProductRepository productRepository;

    @Mock
    CategoryService categoryService;

    @Mock
    StripeService stripeService;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        PageImpl<Product> productsPage = new PageImpl<>(List.of(createProduct()));

        com.stripe.model.Product stripeProductMock = new com.stripe.model.Product();

        BDDMockito
                .when(productRepository.findAll(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(productsPage);

        BDDMockito
                .when(productRepository.findAllByActiveTrue(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(productsPage);

        BDDMockito
                .when(productRepository.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(createProduct()));

        BDDMockito
                .when(productRepository.findAll(ArgumentMatchers.<Specification<Product>>any(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(productsPage);

        BDDMockito
                .when(productRepository.existsBySlug(ArgumentMatchers.anyString()))
                .thenReturn(false);

        BDDMockito
                .when(stripeService.createProduct(ArgumentMatchers.any(CreateProductRequest.class)))
                .thenReturn(stripeProductMock);

        BDDMockito
                .when(stripeService.createPrice(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(new Price());

        BDDMockito
                .doNothing()
                .when(stripeService)
                .desactivePrice(ArgumentMatchers.anyString());

        BDDMockito
                .doNothing()
                .when(stripeService)
                .setProductActive(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());

        BDDMockito
                .doNothing()
                .when(stripeService)
                .updateProduct(ArgumentMatchers.anyString(), ArgumentMatchers.any(ProductUpdateParams.class));

        BDDMockito
                .doNothing()
                .when(stripeService)
                .deleteProduct(ArgumentMatchers.anyString());

        BDDMockito
                .when(stripeService.retrieveProduct(ArgumentMatchers.anyString()))
                .thenReturn(stripeProductMock);

        BDDMockito
                .when(productRepository.save(ArgumentMatchers.any(Product.class)))
                .thenReturn(createProduct());

        BDDMockito
                .doNothing()
                .when(productRepository)
                .delete(ArgumentMatchers.any(Product.class));

        BDDMockito
                .when(categoryService.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(CategoryCreator.createCategory());
    }

    @Test
    @DisplayName("listAll Returns List Of Products Inside Page Object When Successful")
    void listAll_ReturnsListOfProductsInsidePageObject_WhenSuccessful() {
        AdminProductResponse expectedProduct = createAdminProductResponse();

        Page<AdminProductResponse> productsPage = productService.listAll(PageRequest.of(0, 1));

        assertThat(productsPage).isNotEmpty();

        assertThat(productsPage.getContent()).isNotEmpty();

        assertThat(productsPage.getContent().get(0)).isNotNull();

        assertThat(productsPage.getContent().get(0).getSlug()).isEqualTo(expectedProduct.getSlug());
    }

    @Test
    @DisplayName("listAllActive Returns List Of Products Inside Page Object When Successful")
    void listAllActive_ReturnsListOfProductsInsidePageObject_WhenSuccessful() {
        UserProductResponse expectedProduct = createUserProductResponse();

        Page<UserProductResponse> productsPage = productService.listAllActive(PageRequest.of(0, 1));

        assertThat(productsPage).isNotEmpty();

        assertThat(productsPage.getContent()).isNotEmpty();

        assertThat(productsPage.getContent().get(0)).isNotNull();

        assertThat(productsPage.getContent().get(0).getSlug()).isEqualTo(expectedProduct.getSlug());
    }

    @Test
    @DisplayName("findBySlug Returns Product When Successful")
    void findBySlug_ReturnsProduct_WhenSuccessful() {
        Product expectedProduct = createProduct();

        Product productFound = productService.findBySlug(expectedProduct.getSlug());

        assertThat(productFound).isNotNull();

        assertThat(productFound.getSlug()).isEqualTo(expectedProduct.getSlug());
    }

    @Test
    @DisplayName("findBySlug Throws ResourceNotFoundException When Product Don't Exists")
    void findBySlug_ThrowsResourceNotFoundException_WhenProductDoNotExists() {
        BDDMockito
                .when(productRepository.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> productService.findBySlug("test-slug"));
    }

    @Test
    @DisplayName("findBySlugMapped Returns Product When Successful")
    void findBySlugMapped_ReturnsProduct_WhenSuccessful() {
        Product expectedProduct = createProduct();

        UserProductResponse productFound = productService.findBySlugMapped(expectedProduct.getSlug());

        assertThat(productFound).isNotNull();

        assertThat(productFound.getSlug()).isEqualTo(expectedProduct.getSlug());
    }

    @Test
    @DisplayName("findBySlugMapped Throws ResourceNotFoundException When Product Don't Exists")
    void findBySlugMapped_ThrowsResourceNotFoundException_WhenProductDoNotExists() {
        BDDMockito
                .when(productRepository.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> productService.findBySlugMapped("test-slug"));
    }

    @Test
    @DisplayName("search Returns List Of Products Inside Page Object When Successful")
    void search_ReturnsListOfProductsInsidePageObject_WhenSuccessful() {
        UserProductResponse expectedProduct = createUserProductResponse();

        Page<UserProductResponse> productsPage = productService.search(createProductParametersSearch(), PageRequest.of(0, 1));

        assertThat(productsPage).isNotEmpty();

        assertThat(productsPage.getContent()).isNotEmpty();

        assertThat(productsPage.getContent().get(0)).isNotNull();

        assertThat(productsPage.getContent().get(0).getSlug()).isEqualTo(expectedProduct.getSlug());
    }

    @Test
    @DisplayName("verifySlugExists Verify If Slug Exists When Successful")
    void verifySlugExists_VerifyIfSlugExists_WhenSuccessful() {
        assertThatCode(() -> productService.verifySlugExists("test-slug"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("verifySlugExists Throws SlugAlreadyExistsException When Slug Already Exists")
    void verifySlugExists_ThrowsSlugAlreadyExistsException_WhenSlugAlreadyExists() {
        BDDMockito
                .when(productRepository.existsBySlug(ArgumentMatchers.anyString()))
                .thenReturn(true);

        assertThatExceptionOfType(SlugAlreadyExistsException.class)
                .isThrownBy(() -> productService.verifySlugExists("test-slug"));
    }

    @Test
    @DisplayName("create Persists Product When Successful")
    @SneakyThrows
    void create_PersistsProduct_WhenSuccessful() {
        UserProductResponse expectedProduct = createUserProductResponse();

        AdminProductResponse productFound = productService.create(createCreateProductRequest());

        assertThat(productFound).isNotNull();

        assertThat(productFound.getSlug()).isEqualTo(expectedProduct.getSlug());
    }

    @Test
    @DisplayName("replace Updates Product When Successful")
    void replace_UpdatesProduct_WhenSuccessful() {
        assertThatCode(() -> productService.replace(createReplaceProductRequest()))
                .doesNotThrowAnyException();

        ReplaceProductRequest secondRequest = createReplaceProductRequest();
        secondRequest.setPrice(new BigDecimal(400));

        assertThatCode(() -> productService.replace(secondRequest))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("toggleActive Alternates Active When Successful")
    void toggleActive_AlternatesActive_WhenSuccessful() {
        assertThatCode(() -> productService.toggleActive("test-slug"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("applyDiscount Applies Discount To Product When Successful")
    void applyDiscount_AppliesDiscountToProduct_WhenSuccessful() {
        assertThatCode(() -> productService.applyDiscount("test-slug", 10))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("changeAmount Updates Product Amount When Successful")
    void changeAmount_UpdatesProductAmount_WhenSuccessful() {
        assertThatCode(() -> productService.changeAmount("test-slug", 10))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("addCategory Append Category To Product When Successful")
    void addCategory_AppendCategoryToProduct_WhenSuccessful() {
        assertThatCode(() -> productService.addCategory("test-product", "test-category"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("removeCategory Removes Category To Product When Successful")
    void removeCategory_RemovesCategoryToProduct_WhenSuccessful() {
        Product productToReturn = createProduct();

        productToReturn.setCategories(new HashSet<>(Set.of(CategoryCreator.createCategory())));

        BDDMockito
                .when(productRepository.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(productToReturn));

        assertThatCode(() -> productService.removeCategory("test-product", "test-category"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("removeCategory Throws ProductCategoryNotFoundException When Category Not Found On Product")
    void removeCategory_ThrowsProductCategoryNotFoundException_WhenCategoryNotFoundOnProduct() {
        assertThatExceptionOfType(ProductCategoryNotFoundException.class)
                .isThrownBy(() -> productService.removeCategory("test-product", "test-category"));
    }

    @Test
    @DisplayName("delete Removes Product When Successful")
    @SneakyThrows
    void delete_RemovesProduct_WhenSuccessful() {
        assertThatCode(() -> productService.delete("test-slug"))
                .doesNotThrowAnyException();

        BDDMockito
                .willThrow(new RuntimeException())
                .given(stripeService)
                .desactivePrice(ArgumentMatchers.anyString());

        assertThatCode(() -> productService.delete("test-slug"))
                .doesNotThrowAnyException();
    }

}
