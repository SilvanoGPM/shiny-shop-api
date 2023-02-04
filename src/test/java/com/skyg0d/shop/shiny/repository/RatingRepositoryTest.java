package com.skyg0d.shop.shiny.repository;

import java.util.HashSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.model.Rating;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.util.product.ProductCreator;
import com.skyg0d.shop.shiny.util.rating.RatingCreator;
import com.skyg0d.shop.shiny.util.user.UserCreator;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests for RatingRepository")
public class RatingRepositoryTest {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("findAllByUser Returns List Of Ratings Inside Page Object When Successful")
    void findAllByUser_ReturnsListOfRatingsInsidePageObject_WhenSuccessful() {
        Rating expectedRating = persistRating();

        Page<Rating> usersPage = ratingRepository.findAllByUser(expectedRating.getUser(), PageRequest.of(0, 1));

        assertThat(usersPage).isNotEmpty();

        assertThat(usersPage).contains(expectedRating);
    }

    @Test
    @DisplayName("findAllByProduct Returns List Of Ratings Inside Page Object When Successful")
    void findAllByProduct_ReturnsListOfRatingsInsidePageObject_WhenSuccessful() {
        Rating expectedRating = persistRating();

        Page<Rating> usersPage = ratingRepository.findAllByProduct(expectedRating.getProduct(), PageRequest.of(0, 1));

        assertThat(usersPage).isNotEmpty();

        assertThat(usersPage).contains(expectedRating);
    }

    @Test
    @DisplayName("productStarsAverage Returns Product Stars Average When Successful")
    void productStarsAverage_ReturnsProductStarsAverage_WhenSuccessful() {
        Rating expectedRating = persistRating();

        double average = ratingRepository.productStarsAverage(expectedRating.getProduct().getSlug());

        assertThat(average).isEqualTo(RatingCreator.STARS);
    }

    @Test
    @DisplayName("existsByProductAndUser Returns True When User And Product Match To A Rating")
    void existsByProductAndUser_ReturnsTrue_WhenUserAndProductMatchToRating() {
        Rating expectedRating = persistRating();

        boolean ratingExists = ratingRepository
            .existsByProductAndUser(expectedRating.getProduct(), expectedRating.getUser());

        assertThat(ratingExists).isTrue();
    }

    @Test
    @DisplayName("existsByProductAndUser Returns False When User Or Product Not Match To A Rating")
    void existsByProductAndUser_ReturnsTrue_WhenUserOrProductNotMatchToRating() {
        Rating expectedRating = persistRating();

        User userToBeSave =UserCreator.createUser();
        userToBeSave.setEmail("some@mail.com");

        User user = persistUser(userToBeSave);

        boolean ratingExists = ratingRepository
            .existsByProductAndUser(expectedRating.getProduct(), user);

        assertThat(ratingExists).isFalse();
    }

    private Rating persistRating() {
        User userSaved = persistUser(UserCreator.createUser());
        Product productSaved = productRepository.save(ProductCreator.createProduct());

        Rating ratingToSave = RatingCreator.createRatingToBeSave(productSaved, userSaved);
        
        return ratingRepository.save(ratingToSave);
    }

    private User persistUser(User user) {
        user.setRoles(new HashSet<>());

        return userRepository.save(user);
    }

}
