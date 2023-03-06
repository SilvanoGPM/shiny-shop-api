package com.skyg0d.shop.shiny.controller;

import com.skyg0d.shop.shiny.payload.request.CreateRatingRequest;
import com.skyg0d.shop.shiny.payload.response.RatingResponse;
import com.skyg0d.shop.shiny.payload.response.RatingStarsAverageResponse;
import com.skyg0d.shop.shiny.security.service.UserDetailsImpl;
import com.skyg0d.shop.shiny.service.RatingService;
import com.skyg0d.shop.shiny.util.AuthUtils;
import com.skyg0d.shop.shiny.util.product.ProductCreator;
import com.skyg0d.shop.shiny.util.rating.RatingCreator;
import com.skyg0d.shop.shiny.util.user.UserCreator;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for RatingController")
public class RatingControllerTest {

    @InjectMocks
    RatingController ratingController;

    @Mock
    RatingService ratingService;

    @Mock
    AuthUtils authUtils;

    @BeforeEach
    void setUp() {
        Page<RatingResponse> ratingsPage = new PageImpl<>(List.of(RatingCreator.createRatingResponse()));

        BDDMockito
                .when(ratingService.findAllByUser(ArgumentMatchers.anyString(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(ratingsPage);

        BDDMockito
                .when(ratingService.findAllByProduct(ArgumentMatchers.anyString(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(ratingsPage);

        BDDMockito
                .when(ratingService.productStarsAverage(ArgumentMatchers.anyString()))
                .thenReturn(new RatingStarsAverageResponse(5));

        BDDMockito
                .when(ratingService.create(ArgumentMatchers.any(CreateRatingRequest.class), ArgumentMatchers.anyString()))
                .thenReturn(RatingCreator.createRatingResponse());

        BDDMockito
                .when(authUtils.getUserDetails())
                .thenReturn(new UserDetailsImpl(UUID.randomUUID(), UserCreator.USERNAME, UserCreator.EMAIL, UserCreator.PASSWORD, null));
    }

    @Test
    @DisplayName("listAllByUser Returns List Of Ratings Inside Page Object When Successful")
    void listAllByUser_ReturnsListOfRatingsInsidePageObject_WhenSuccessful() {
        RatingResponse expectedRating = RatingCreator.createRatingResponse();

        ResponseEntity<Page<RatingResponse>> entity = ratingController.listAllByUser(expectedRating.getUser().getEmail(), PageRequest.of(0, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getUser()).isEqualTo(expectedRating.getUser());
    }

    @Test
    @DisplayName("listAllByProduct Returns List Of Ratings Inside Page Object When Successful")
    void listAllByProduct_ReturnsListOfRatingsInsidePageObject_WhenSuccessful() {
        RatingResponse expectedRating = RatingCreator.createRatingResponse();

        ResponseEntity<Page<RatingResponse>> entity = ratingController.listAllByProduct(expectedRating.getUser().getEmail(), PageRequest.of(0, 1));

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotEmpty();

        assertThat(entity.getBody().getContent()).isNotEmpty();

        assertThat(entity.getBody().getContent().get(0)).isNotNull();

        assertThat(entity.getBody().getContent().get(0).getProduct()).isEqualTo(expectedRating.getProduct());
    }

    @Test
    @DisplayName("productStarsAverage Returns Stars Average Of Product When Successful")
    void productStarsAverage_ReturnsStarsAverageOfProduct_WhenSuccessful() {
        RatingResponse expectedRating = RatingCreator.createRatingResponse();

        ResponseEntity<RatingStarsAverageResponse> entity = ratingController.productStarsAverage(expectedRating.getProduct().getSlug());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getStars()).isEqualTo(5);
    }

    @Test
    @DisplayName("create Persists Rating When Successful")
    void create_PersistsRating_WhenSuccessful() {
        CreateRatingRequest request = RatingCreator.createCreateRatingRequest();

        ResponseEntity<RatingResponse> entity = ratingController.create(request);

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(entity.getBody()).isNotNull();

        assertThat(entity.getBody().getUser().getEmail()).isEqualTo(UserCreator.EMAIL);

        assertThat(entity.getBody().getProduct().getSlug()).isEqualTo(ProductCreator.SLUG);
    }

    @Test
    @DisplayName("delete Removes Rating When Successful")
    void delete_RemovesRating_WhenSuccessful() {
        ResponseEntity<Void> entity = ratingController.delete(UUID.randomUUID().toString());

        assertThat(entity).isNotNull();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}
