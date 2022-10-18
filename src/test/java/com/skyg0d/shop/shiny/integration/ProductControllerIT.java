package com.skyg0d.shop.shiny.integration;

import com.skyg0d.shop.shiny.exception.details.ExceptionDetails;
import com.skyg0d.shop.shiny.model.Category;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.payload.request.ApplyDiscountRequest;
import com.skyg0d.shop.shiny.payload.request.ChangeAmountRequest;
import com.skyg0d.shop.shiny.payload.request.ReplaceProductRequest;
import com.skyg0d.shop.shiny.payload.response.AdminProductResponse;
import com.skyg0d.shop.shiny.payload.response.MessageResponse;
import com.skyg0d.shop.shiny.payload.response.UserProductResponse;
import com.skyg0d.shop.shiny.repository.CategoryRepository;
import com.skyg0d.shop.shiny.repository.ProductRepository;
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

    @Autowired
    TestRestTemplate httpClient;

    @Autowired
    JWTCreator jwtCreator;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("listAllActive Returns List Of Products Inside Page Object When Successful")
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
    @DisplayName("existsBySlug Says Product Don't Exists When Successful")
    void existsBySlug_SaysProductDoNotExists_WhenSuccessful() {
        String expectedMessage = "Product don't exists";

        ResponseEntity<MessageResponse> entity = httpClient.exchange(
                "/products/{slug}/exists",
                HttpMethod.GET,
                null,
                MessageResponse.class,
                "some-slug"
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("existsBySlug Says Product Exists When Product Slug Already Exists")
    void existsBySlug_SaysProductExists_WhenProductSlugAlreadyExists() {
        String expectedMessage = "Product exists";

        Product productSaved = productRepository.save(createProductToBeSave());

        ResponseEntity<MessageResponse> entity = httpClient.exchange(
                "/products/{slug}/exists",
                HttpMethod.GET,
                null,
                MessageResponse.class,
                productSaved.getSlug()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("create Persists Product When Successful")
    void create_PersistsProduct_WhenSuccessful() {
        UserProductResponse expectedProduct = createUserProductResponse();

        ResponseEntity<UserProductResponse> entity = httpClient.exchange(
                "/products",
                HttpMethod.POST,
                jwtCreator.createAdminAuthEntity(createCreateProductRequest()),
                UserProductResponse.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getSlug()).isEqualTo(expectedProduct.getSlug());
    }

    @Test
    @DisplayName("replace Updates Product When Successful")
    void replace_UpdatesProduct_WhenSuccessful() {
        String expectedMessage = "Product replaced!";

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

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);

        assertThat(productFound).isNotEmpty();

        assertThat(productFound.get()).isNotNull();

        assertThat(productFound.get().getName()).isEqualTo("NewName");
    }

    @Test
    @DisplayName("toggleActive Alternates Active When Successful")
    void toggleActive_AlternatesActive_WhenSuccessful() {
        String expectedMessage = "Product visibility toggle!";

        Product productSaved = productRepository.save(createProductToBeSave());

        ResponseEntity<MessageResponse> entity = httpClient.exchange(
                "/products/{slug}/toggle/active",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                MessageResponse.class,
                productSaved.getSlug()
        );

        Optional<Product> productFound = productRepository.findBySlug(productSaved.getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);

        assertThat(productFound).isNotEmpty();

        assertThat(productFound.get()).isNotNull();

        assertThat(productFound.get().isActive()).isFalse();
    }

    @Test
    @DisplayName("applyDiscount Applies Discount To Product When Successful")
    void applyDiscount_AppliesDiscountToProduct_WhenSuccessful() {
        String expectedMessage = "Product discount applied!";

        Product productSaved = productRepository.save(createProductToBeSave());

        ResponseEntity<MessageResponse> entity = httpClient.exchange(
                "/products/{slug}/apply/discount",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(new ApplyDiscountRequest(10)),
                MessageResponse.class,
                productSaved.getSlug()
        );

        Optional<Product> productFound = productRepository.findBySlug(productSaved.getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);

        assertThat(productFound).isNotEmpty();

        assertThat(productFound.get()).isNotNull();

        assertThat(productFound.get().getDiscount()).isEqualTo(10);
    }

    @Test
    @DisplayName("changeAmount Updates Product Amount When Successful")
    void changeAmount_UpdatesProductAmount_WhenSuccessful() {
        String expectedMessage = "Product amount changed!";

        Product productSaved = productRepository.save(createProductToBeSave());

        ResponseEntity<MessageResponse> entity = httpClient.exchange(
                "/products/{slug}/change/amount",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(new ChangeAmountRequest(15)),
                MessageResponse.class,
                productSaved.getSlug()
        );

        Optional<Product> productFound = productRepository.findBySlug(productSaved.getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);

        assertThat(productFound).isNotEmpty();

        assertThat(productFound.get()).isNotNull();

        assertThat(productFound.get().getAmount()).isEqualTo(15);
    }

    @Test
    @DisplayName("addCategory Append Category To Product When Successful")
    void addCategory_AppendCategoryToProduct_WhenSuccessful() {
        String expectedMessage = "Add category to product!";

        Product productSaved = productRepository.save(createProductToBeSave());

        Category categorySaved = categoryRepository.save(createCategoryToBeSave());

        ResponseEntity<MessageResponse> entity = httpClient.exchange(
                "/products/{productSlug}/add/{categorySlug}/category",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                MessageResponse.class,
                productSaved.getSlug(),
                categorySaved.getSlug()
        );

        Optional<Product> productFound = productRepository.findBySlug(productSaved.getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);

        assertThat(productFound).isNotEmpty();

        assertThat(productFound.get()).isNotNull();

        assertThat(productFound.get().getCategories()).isNotEmpty();

        assertThat(productFound.get().getCategories()).contains(categorySaved);
    }

    @Test
    @DisplayName("removeCategory Removes Category To Product When Successful")
    void removeCategory_RemovesCategoryToProduct_WhenSuccessful() {
        String expectedMessage = "Remove product category!";

        Category categorySaved = categoryRepository.save(createCategoryToBeSave());

        Product productToBeSave = createProductToBeSave();

        productToBeSave.setCategories(Set.of(categorySaved));

        Product productSaved = productRepository.save(productToBeSave);

        ResponseEntity<MessageResponse> entity = httpClient.exchange(
                "/products/{productSlug}/remove/{categorySlug}/category",
                HttpMethod.PATCH,
                jwtCreator.createAdminAuthEntity(null),
                MessageResponse.class,
                productSaved.getSlug(),
                categorySaved.getSlug()
        );

        Optional<Product> productFound = productRepository.findBySlug(productSaved.getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);

        assertThat(productFound).isNotEmpty();

        assertThat(productFound.get()).isNotNull();

        assertThat(productFound.get().getCategories()).isEmpty();
    }

    @Test
    @DisplayName("removeCategory Returns ExceptionDetails When Category Not Found On Product")
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
        String expectedMessage = "Product removed!";

        Product productSaved = productRepository.save(createProductToBeSave());

        ResponseEntity<MessageResponse> entity = httpClient.exchange(
                "/products/{slug}",
                HttpMethod.DELETE,
                jwtCreator.createAdminAuthEntity(null),
                MessageResponse.class,
                productSaved.getSlug()
        );

        Optional<Product> productFound = productRepository.findBySlug(productSaved.getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getMessage()).isEqualTo(expectedMessage);

        assertThat(productFound).isEmpty();
    }

}
