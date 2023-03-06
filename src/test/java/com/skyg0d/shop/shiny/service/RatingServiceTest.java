package com.skyg0d.shop.shiny.service;

import com.skyg0d.shop.shiny.exception.PermissionInsufficient;
import com.skyg0d.shop.shiny.exception.RatingAlreadyExistsException;
import com.skyg0d.shop.shiny.exception.ResourceNotFoundException;
import com.skyg0d.shop.shiny.mapper.RatingMapper;
import com.skyg0d.shop.shiny.mapper.RatingMapperImpl;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.model.Rating;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.request.CreateRatingRequest;
import com.skyg0d.shop.shiny.payload.response.RatingResponse;
import com.skyg0d.shop.shiny.payload.response.RatingStarsAverageResponse;
import com.skyg0d.shop.shiny.repository.RatingRepository;
import com.skyg0d.shop.shiny.util.AuthUtils;
import com.skyg0d.shop.shiny.util.product.ProductCreator;
import com.skyg0d.shop.shiny.util.rating.RatingCreator;
import com.skyg0d.shop.shiny.util.user.UserCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for RatingService")
public class RatingServiceTest {

    @InjectMocks
    RatingService ratingService;

    @Mock
    RatingRepository ratingRepository;

    @Mock
    UserService userService;

    @Mock
    ProductService productService;

    @Mock
    AuthUtils authUtils;

    @Spy
    RatingMapper ratingMapper = new RatingMapperImpl();

    @BeforeEach
    void setUp() {
        Page<Rating> ratingsPage = new PageImpl<>(List.of(RatingCreator.createRating()));

        BDDMockito
                .when(userService.findByEmail(ArgumentMatchers.anyString()))
                .thenReturn(UserCreator.createUser());

        BDDMockito
                .when(ratingRepository.findAllByUser(ArgumentMatchers.any(User.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(ratingsPage);

        BDDMockito
                .when(productService.findBySlug((ArgumentMatchers.anyString())))
                .thenReturn(ProductCreator.createProduct());

        BDDMockito
                .when(ratingRepository.findAllByProduct(ArgumentMatchers.any(Product.class), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(ratingsPage);

        BDDMockito
                .when(ratingRepository.productStarsAverage(ArgumentMatchers.anyString()))
                .thenReturn(5D);

        BDDMockito
                .when(ratingRepository.existsByProductAndUser(ArgumentMatchers.any(Product.class), ArgumentMatchers.any(User.class)))
                .thenReturn(false);

        BDDMockito
                .when(ratingRepository.save(ArgumentMatchers.any(Rating.class)))
                .thenReturn(RatingCreator.createRating());

        ReflectionTestUtils.setField(
                ratingMapper,
                "userService",
                userService
        );

        ReflectionTestUtils.setField(
                ratingMapper,
                "productService",
                productService
        );

        BDDMockito
                .when(ratingRepository.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(RatingCreator.createRating()));

        BDDMockito
                .when(authUtils.isOwnerOrStaff(ArgumentMatchers.anyString()))
                .thenReturn(true);

        BDDMockito
                .doNothing()
                .when(ratingRepository)
                .delete(ArgumentMatchers.any(Rating.class));
    }

    @Test
    @DisplayName("findAllByUser Returns List Of Ratings Inside Page Object When Successful")
    void findAllByUser_ReturnsListOfRatingsInsidePageObject_WhenSuccessful() {
        RatingResponse expectedRating = RatingCreator.createRatingResponse();

        Page<RatingResponse> ratingPage = ratingService.findAllByUser(expectedRating.getUser().getEmail(), PageRequest.of(0, 1));

        assertThat(ratingPage).isNotEmpty();

        assertThat(ratingPage.getContent()).isNotEmpty();

        assertThat(ratingPage.getContent().get(0)).isNotNull();

        assertThat(ratingPage.getContent().get(0).getUser()).isEqualTo(expectedRating.getUser());
    }

    @Test
    @DisplayName("findAllByProduct Returns List Of Ratings Inside Page Object When Successful")
    void findAllByProduct_ReturnsListOfRatingsInsidePageObject_WhenSuccessful() {
        RatingResponse expectedRating = RatingCreator.createRatingResponse();

        Page<RatingResponse> ratingPage = ratingService.findAllByProduct(expectedRating.getProduct().getSlug(), PageRequest.of(0, 1));

        assertThat(ratingPage).isNotEmpty();

        assertThat(ratingPage.getContent()).isNotEmpty();

        assertThat(ratingPage.getContent().get(0)).isNotNull();

        assertThat(ratingPage.getContent().get(0).getProduct()).isEqualTo(expectedRating.getProduct());
    }

    @Test
    @DisplayName("productStarsAverage Returns Stars Average Of Product When Successful")
    void productStarsAverage_ReturnsStarsAverageOfProduct_WhenSuccessful() {
        RatingResponse expectedRating = RatingCreator.createRatingResponse();

        RatingStarsAverageResponse productStarsAverage = ratingService.productStarsAverage(expectedRating.getProduct().getSlug());

        assertThat(productStarsAverage).isNotNull();

        assertThat(productStarsAverage.getStars()).isEqualTo(5);
    }

    @Test
    @DisplayName("create Persists Rating When Successful")
    void create_PersistsRating_WhenSuccessful() {
        CreateRatingRequest request = RatingCreator.createCreateRatingRequest();

        RatingResponse ratingCreated = ratingService.create(request, UserCreator.EMAIL);

        assertThat(ratingCreated).isNotNull();

        assertThat(ratingCreated.getUser().getEmail()).isEqualTo(UserCreator.EMAIL);

        assertThat(ratingCreated.getProduct().getSlug()).isEqualTo(ProductCreator.SLUG);
    }

    @Test
    @DisplayName("create Throws RatingAlreadyExistsException When Already Exists A Rating")
    void create_ThrowsRatingAlreadyExistsException_WhenAlreadyExistsARating() {
        BDDMockito
                .when(ratingRepository.existsByProductAndUser(ArgumentMatchers.any(Product.class), ArgumentMatchers.any(User.class)))
                .thenReturn(true);

        CreateRatingRequest request = RatingCreator.createCreateRatingRequest();

        assertThatExceptionOfType(RatingAlreadyExistsException.class)
                .isThrownBy(() -> ratingService.create(request, UserCreator.EMAIL));

    }

    @Test
    @DisplayName("delete Removes Rating When Successful")
    void delete_RemovesRating_WhenSuccessful() {
        assertThatCode(() -> ratingService.delete(UUID.randomUUID().toString()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("delete Throws ResourceNotFoundException When Rating Don't Exists")
    void delete_ThrowsResourceNotFoundException_WhenRatingDoNotExists() {
        BDDMockito
                .when(ratingRepository.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> ratingService.delete(UUID.randomUUID().toString()));
    }

    @Test
    @DisplayName("delete Throws PermissionInsufficient When User Doesn't Have Permission")
    void delete_PermissionInsufficient_WhenUserDoesNotHavePermission() {
        BDDMockito
                .when(authUtils.isOwnerOrStaff(ArgumentMatchers.anyString()))
                .thenReturn(false);

        assertThatExceptionOfType(PermissionInsufficient.class)
                .isThrownBy(() -> ratingService.delete(UUID.randomUUID().toString()));
    }

}
