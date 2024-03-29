package com.skyg0d.shop.shiny.integration;

import com.skyg0d.shop.shiny.exception.details.ExceptionDetails;
import com.skyg0d.shop.shiny.model.Category;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.payload.request.ApplyDiscountRequest;
import com.skyg0d.shop.shiny.payload.request.ChangeAmountRequest;
import com.skyg0d.shop.shiny.payload.request.CreateProductRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceProductRequest;
import com.skyg0d.shop.shiny.payload.response.AdminProductResponse;
import com.skyg0d.shop.shiny.payload.response.MessageResponse;
import com.skyg0d.shop.shiny.payload.response.UserProductResponse;
import com.skyg0d.shop.shiny.repository.CategoryRepository;
import com.skyg0d.shop.shiny.repository.ProductRepository;
import com.skyg0d.shop.shiny.service.StripeService;
import com.skyg0d.shop.shiny.util.JWTCreator;
import com.skyg0d.shop.shiny.util.product.ProductCreator;
import com.skyg0d.shop.shiny.wrapper.PageableResponse;
import com.stripe.model.Price;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;
import java.util.Set;

import static com.skyg0d.shop.shiny.util.category.CategoryCreator.createCategoryToBeSave;
import static com.skyg0d.shop.shiny.util.product.ProductCreator.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Integration tests for ProductController")
public class ProductControllerIT {

    @MockBean
    StripeService stripeService;

    @Autowired
    TestRestTemplate httpClient;

    @Autowired
    JWTCreator jwtCreator;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        com.stripe.model.Product stripeProduct = new com.stripe.model.Product();
        stripeProduct.setId(STRIPE_PRODUCT_ID);

        Price stripePrice = new Price();
        stripePrice.setId(STRIPE_PRICE_ID);

        BDDMockito
                .when(stripeService.createProduct(ArgumentMatchers.any(CreateProductRequest.class)))
                .thenReturn(stripeProduct);

        BDDMockito
                .when(stripeService.createPrice(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(stripePrice);

        BDDMockito
                .when(stripeService.createPromotionCode(ArgumentMatchers.any(), ArgumentMatchers.anyString()))
                .thenReturn(ProductCreator.createPromotionCodeCreated());

        BDDMockito
                .doNothing()
                .when(stripeService)
                .deletePromotionCode(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("listAllActive Returns List Of Products Inside Page Object When Successful")
    @SuppressWarnings("null")
    void listAllActive_ReturnsListOfProductsInsidePageObject_WhenSuccessful() {
        UserProductResponse expectedProduct = createUserProductResponse();

        productRepository.save(createProductToBeSave());

        ResponseEntity<PageableResponse<UserProductResponse>> entity = httpClient.exchange(
                "/products",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getSlug()).isEqualTo(expectedProduct.getSlug());
    }

    @Test
    @DisplayName("listAll Returns List Of Products Inside Page Object When Successful")
    @SuppressWarnings("null")
    void listAll_ReturnsListOfProductsInsidePageObject_WhenSuccessful() {
        AdminProductResponse expectedProduct = createAdminProductResponse();

        productRepository.save(createProductToBeSave());

        ResponseEntity<PageableResponse<AdminProductResponse>> entity = httpClient.exchange(
                "/products/all",
                HttpMethod.GET,
                jwtCreator.createAdminAuthEntity(null),
                new ParameterizedTypeReference<>() {
                });

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getSlug()).isEqualTo(expectedProduct.getSlug());
    }

    @Test
    @DisplayName("findBySlug Returns Product When Successful")
    @SuppressWarnings("null")
    void findBySlug_ReturnsProduct_WhenSuccessful() {
        Product expectedProduct = createProduct();

        Product productSaved = productRepository.save(createProductToBeSave());

        ResponseEntity<UserProductResponse> entity = httpClient.exchange(
                "/products/{slug}",
                HttpMethod.GET,
                null,
                UserProductResponse.class,
                productSaved.getSlug()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getSlug()).isEqualTo(expectedProduct.getSlug());
    }

    @Test
    @DisplayName("findBySlug Returns ExceptionDetails When Product Don't Exists")
    @SuppressWarnings("null")
    void findBySlug_ReturnsExceptionDetails_WhenProductDoNotExists() {
        String expectedTitle = "Resource Not Found";

        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/products/{slug}",
                HttpMethod.GET,
                null,
                ExceptionDetails.class,
                "product-slug"
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getTitle()).isEqualTo(expectedTitle);
    }

    @Test
    @DisplayName("search Returns List Of Products Inside Page Object When Successful")
    @SuppressWarnings("null")
    void search_ReturnsListOfProductsInsidePageObject_WhenSuccessful() {
        UserProductResponse expectedProduct = createUserProductResponse();

        productRepository.save(createProductToBeSave());

        ResponseEntity<PageableResponse<UserProductResponse>> entity = httpClient.exchange(
                String.format("/products/search?name=%s", expectedProduct.getName()),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

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
        ResponseEntity<Void> entity = httpClient.exchange(
                "/products/{slug}",
                HttpMethod.HEAD,
                null,
                Void.class,
                "some-slug"
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(entity.getBody()).isNull();
    }

    @Test
    @DisplayName("existsBySlug Returns 200 Ok When Product Exists")
    void existsBySlug_Returns200Ok_WhenProductExists() {
        Product productSaved = productRepository.save(createProductToBeSave());

        ResponseEntity<Void> entity = httpClient.exchange(
                "/products/{slug}",
                HttpMethod.HEAD,
                null,
                Void.class,
                productSaved.getSlug()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNull();
    }

    @SneakyThrows
    @Test
    @DisplayName("create Persists Product When Successful")
    @SuppressWarnings("null")
    void create_PersistsProduct_WhenSuccessful() {
        AdminProductResponse expectedProduct = createAdminProductResponse();

        ResponseEntity<AdminProductResponse> entity = httpClient.exchange(
                "/products",
                HttpMethod.POST,
                jwtCreator.createAdminAuthEntity(createCreateProductRequest()),
                AdminProductResponse.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getSlug()).isEqualTo(expectedProduct.getSlug());

        assertThat(entity.getBody().getStripeProductId()).isEqualTo(expectedProduct.getStripeProductId());

        assertThat(entity.getBody().getStripePriceId()).isEqualTo(expectedProduct.getStripePriceId());
    }

    @Test
    @DisplayName("replace Updates Product When Successful")
    void replace_UpdatesProduct_WhenSuccessful() {
        Product productSaved = productRepository.save(createProductToBeSave());

        ReplaceProductRequest replaceProductRequest = createReplaceProductRequest();
        replaceProductRequest.setName("NewName");
        replaceProductRequest.setSlug(productSaved.getSlug());

        ResponseEntity<MessageResponse> entity = httpClient.exchange(
                "/products",
                HttpMethod.PUT,
                jwtCreator.createAdminAuthEntity(replaceProductRequest),
                MessageResponse.class
        );

        Optional<Product> productFound = productRepository.findBySlug(productSaved.getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();

        assertThat(productFound).isNotEmpty();

        assertThat(productFound.get()).isNotNull();

        assertThat(productFound.get().getName()).isEqualTo("NewName");
    }

    @Test
    @DisplayName("toggleActive Alternates Active When Successful")
    void toggleActive_AlternatesActive_WhenSuccessful() {
        Product productSaved = productRepository.save(createProductToBeSave());

        ResponseEntity<Void> entity = httpClient.exchange(
                "/products/{slug}/toggle/active",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                Void.class,
                productSaved.getSlug()
        );

        Optional<Product> productFound = productRepository.findBySlug(productSaved.getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();

        assertThat(productFound).isNotEmpty();

        assertThat(productFound.get()).isNotNull();

        assertThat(productFound.get().isActive()).isFalse();
    }

    @Test
    @DisplayName("applyDiscount Applies Discount To Product When Successful")
    void applyDiscount_AppliesDiscountToProduct_WhenSuccessful() {
        Product productSaved = productRepository.save(createProductToBeSave());

        ApplyDiscountRequest request = createApplyDiscountRequest();

        ResponseEntity<Void> entity = httpClient.exchange(
                "/products/{slug}/apply/discount",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(request),
                Void.class,
                productSaved.getSlug()
        );

        Optional<Product> productFound = productRepository.findBySlug(productSaved.getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();

        assertThat(productFound).isNotEmpty();

        assertThat(productFound.get()).isNotNull();

        assertThat(productFound.get().getDiscount()).isEqualTo(request.getDiscount());

        assertThat(productFound.get().getDiscountCode()).isEqualTo(request.getCode());
    }

    @Test
    @DisplayName("removeDiscount Removes Discount To Product When Successful")
    @SneakyThrows
    void removeDiscount_RemovesDiscountToProduct_WhenSuccessful() {
        Product productSaved = productRepository.save(createProductToBeSave());

        ResponseEntity<Void> entity = httpClient.exchange(
                "/products/{slug}/remove/discount",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                Void.class,
                productSaved.getSlug()
        );

        Optional<Product> productFound = productRepository.findBySlug(productSaved.getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();

        assertThat(productFound).isNotEmpty();

        assertThat(productFound.get()).isNotNull();

        assertThat(productFound.get().getDiscount()).isEqualTo(0);

        assertThat(productFound.get().getDiscountCode()).isEqualTo(null);
    }

    @Test
    @DisplayName("changeAmount Updates Product Amount When Successful")
    void changeAmount_UpdatesProductAmount_WhenSuccessful() {
        Product productSaved = productRepository.save(createProductToBeSave());

        ResponseEntity<Void> entity = httpClient.exchange(
                "/products/{slug}/change/amount",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(new ChangeAmountRequest(15)),
                Void.class,
                productSaved.getSlug()
        );

        Optional<Product> productFound = productRepository.findBySlug(productSaved.getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();

        assertThat(productFound).isNotEmpty();

        assertThat(productFound.get()).isNotNull();

        assertThat(productFound.get().getAmount()).isEqualTo(15);
    }

    @Test
    @DisplayName("addCategory Append Category To Product When Successful")
    void addCategory_AppendCategoryToProduct_WhenSuccessful() {
        Product productSaved = productRepository.save(createProductToBeSave());

        Category categorySaved = categoryRepository.save(createCategoryToBeSave());

        ResponseEntity<Void> entity = httpClient.exchange(
                "/products/{productSlug}/add/{categorySlug}/category",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                Void.class,
                productSaved.getSlug(),
                categorySaved.getSlug()
        );

        Optional<Product> productFound = productRepository.findBySlug(productSaved.getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();

        assertThat(productFound).isNotEmpty();

        assertThat(productFound.get()).isNotNull();

        assertThat(productFound.get().getCategories()).isNotEmpty();

        assertThat(productFound.get().getCategories()).contains(categorySaved);
    }

    @Test
    @DisplayName("removeCategory Removes Category To Product When Successful")
    void removeCategory_RemovesCategoryToProduct_WhenSuccessful() {
        Category categorySaved = categoryRepository.save(createCategoryToBeSave());

        Product productToBeSave = createProductToBeSave();

        productToBeSave.setCategories(Set.of(categorySaved));

        Product productSaved = productRepository.save(productToBeSave);

        ResponseEntity<Void> entity = httpClient.exchange(
                "/products/{productSlug}/remove/{categorySlug}/category",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                Void.class,
                productSaved.getSlug(),
                categorySaved.getSlug()
        );

        Optional<Product> productFound = productRepository.findBySlug(productSaved.getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();

        assertThat(productFound).isNotEmpty();

        assertThat(productFound.get()).isNotNull();

        assertThat(productFound.get().getCategories()).isEmpty();
    }

    @Test
    @DisplayName("removeCategory Returns ExceptionDetails When Category Not Found On Product")
    @SuppressWarnings("null")
    void removeCategory_ReturnsExceptionDetails_WhenCategoryNotFoundOnProduct() {
        String expectedTitle = "Product Category Not Found";

        Product productSaved = productRepository.save(createProductToBeSave());

        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/products/{productSlug}/remove/{categorySlug}/category",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                ExceptionDetails.class,
                productSaved.getSlug(),
                "category-slug"
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getTitle()).isEqualTo(expectedTitle);
    }

    @Test
    @DisplayName("delete Removes Product When Successful")
    void delete_RemovesProduct_WhenSuccessful() {
        Product productSaved = productRepository.save(createProductToBeSave());

        ResponseEntity<Void> entity = httpClient.exchange(
                "/products/{slug}",
                HttpMethod.DELETE,
                jwtCreator.createAdminAuthEntity(null),
                Void.class,
                productSaved.getSlug()
        );

        Optional<Product> productFound = productRepository.findBySlug(productSaved.getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(entity.getBody()).isNull();

        assertThat(productFound).isEmpty();
    }

}
