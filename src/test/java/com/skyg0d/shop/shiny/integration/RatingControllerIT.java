package com.skyg0d.shop.shiny.integration;

import com.skyg0d.shop.shiny.exception.details.ExceptionDetails;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.model.Rating;
import com.skyg0d.shop.shiny.model.Role;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.request.CreateRatingRequest;
import com.skyg0d.shop.shiny.payload.response.RatingResponse;
import com.skyg0d.shop.shiny.payload.response.RatingStarsAverageResponse;
import com.skyg0d.shop.shiny.repository.ProductRepository;
import com.skyg0d.shop.shiny.repository.RatingRepository;
import com.skyg0d.shop.shiny.repository.RoleRepository;
import com.skyg0d.shop.shiny.repository.UserRepository;
import com.skyg0d.shop.shiny.util.JWTCreator;
import com.skyg0d.shop.shiny.util.product.ProductCreator;
import com.skyg0d.shop.shiny.util.rating.RatingCreator;
import com.skyg0d.shop.shiny.util.user.UserCreator;
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

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Integration tests for RatingController")
public class RatingControllerIT {

    @Autowired
    TestRestTemplate httpClient;

    @Autowired
    JWTCreator jwtCreator;

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ProductRepository productRepository;

    @Test
    @DisplayName("listAllByUser Returns List Of Ratings Inside Page Object When Successful")
    void listAllByUser_ReturnsListOfRatingsInsidePageObject_WhenSuccessful() {
        Rating expectedRating = persistRating();

        ResponseEntity<PageableResponse<RatingResponse>> entity = httpClient.exchange(
                "/ratings/user/{email}/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                expectedRating.getUser().getEmail()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getUser().getEmail()).isEqualTo(expectedRating.getUser().getEmail());
    }

    @Test
    @DisplayName("listAllByProduct Returns List Of Ratings Inside Page Object When Successful")
    void listAllByProduct_ReturnsListOfRatingsInsidePageObject_WhenSuccessful() {
        Rating expectedRating = persistRating();

        ResponseEntity<PageableResponse<RatingResponse>> entity = httpClient.exchange(
                "/ratings/product/{slug}/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                expectedRating.getProduct().getSlug()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getProduct().getSlug()).isEqualTo(expectedRating.getProduct().getSlug());
    }

    @Test
    @DisplayName("productStarsAverage Returns Stars Average Of Product When Successful")
    void productStarsAverage_ReturnsStarsAverageOfProduct_WhenSuccessful() {
        Rating expectedRating = persistRating();

        ResponseEntity<RatingStarsAverageResponse> entity = httpClient.exchange(
                "/ratings/product/{slug}/average",
                HttpMethod.GET,
                null,
                RatingStarsAverageResponse.class,
                expectedRating.getProduct().getSlug()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getStars()).isEqualTo(expectedRating.getStars());
    }

    @Test
    @DisplayName("create Persists Rating When Successful")
    void create_PersistsRating_WhenSuccessful() {
        Product product = persistProduct();

        CreateRatingRequest request = RatingCreator.createCreateRatingRequest();

        request.setProductSlug(product.getSlug());

        ResponseEntity<RatingResponse> entity = httpClient.exchange(
                "/ratings",
                HttpMethod.POST,
                jwtCreator.createUserAuthEntity(request),
                RatingResponse.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getUser().getEmail()).isEqualTo(jwtCreator.createUser().getEmail());

        assertThat(entity.getBody().getProduct().getSlug()).isEqualTo(request.getProductSlug());
    }

    @Test
    @DisplayName("create Returns ExceptionDetails When Rating Already Exists")
    void create_ReturnsExceptionDetails_WhenRatingAlreadyExists() {
        String expectedTitle = "Rating Already Exists";

        Rating rating = persistRating(findUserByEmail(jwtCreator.createUser().getEmail()));

        CreateRatingRequest request = RatingCreator.createCreateRatingRequest();

        request.setProductSlug(rating.getProduct().getSlug());

        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/ratings",
                HttpMethod.POST,
                jwtCreator.createUserAuthEntity(request),
                ExceptionDetails.class
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getTitle()).isEqualTo(expectedTitle);
    }

    @Test
    @DisplayName("delete Removes Rating When Successful")
    void delete_RemovesRating_WhenSuccessful() {
        Rating rating = persistRating();

        ResponseEntity<Void> entity = httpClient.exchange(
                "/ratings/{id}",
                HttpMethod.DELETE,
                jwtCreator.createAdminAuthEntity(null),
                Void.class,
                rating.getId().toString()
        );

        long totalRatings = ratingRepository.count();

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(totalRatings).isEqualTo(0);
    }

    @Test
    @DisplayName("delete Returns ExceptionDetails When Rating Don't Exists")
    void delete_ReturnsExceptionDetails_WhenRatingDoNotExists() {
        String expectedTitle = "Resource Not Found";

        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/ratings/{id}",
                HttpMethod.DELETE,
                jwtCreator.createAdminAuthEntity(null),
                ExceptionDetails.class,
                UUID.randomUUID().toString()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getTitle()).isEqualTo(expectedTitle);
    }

    @Test
    @DisplayName("delete Returns ExceptionDetails When User Doesn't Have Permission")
    void delete_ReturnsExceptionDetails_WhenUserDoesNotHavePermission() {
        String expectedTitle = "Permission Insufficient";

        Rating rating = persistRating();

        ResponseEntity<ExceptionDetails> entity = httpClient.exchange(
                "/ratings/{id}",
                HttpMethod.DELETE,
                jwtCreator.createOtherUserAuthEntity(null),
                ExceptionDetails.class,
                rating.getId().toString()
        );

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getTitle()).isEqualTo(expectedTitle);
    }

    private Rating persistRating(User user) {
        Product productSaved = persistProduct();

        Rating ratingToSave = RatingCreator.createRating(productSaved, user);

        return ratingRepository.save(ratingToSave);
    }

    private Rating persistRating() {
        User userSaved = persistUser();

        return persistRating(userSaved);
    }

    private User persistUser() {
        User userToSave = UserCreator.createUser();

        List<Role> rolesSaved = roleRepository.saveAll(userToSave.getRoles());

        userToSave.setRoles(new HashSet<>(rolesSaved));

        return userRepository.save(userToSave);
    }

    private Product persistProduct() {
        Product productToSave = ProductCreator.createProduct();

        productToSave.setCategories(new HashSet<>());

        return productRepository.save(productToSave);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

}
