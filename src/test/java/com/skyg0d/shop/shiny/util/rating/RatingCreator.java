package com.skyg0d.shop.shiny.util.rating;

import com.skyg0d.shop.shiny.mapper.RatingMapper;
import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.model.Rating;
import com.skyg0d.shop.shiny.model.User;
import com.skyg0d.shop.shiny.payload.request.CreateRatingRequest;
import com.skyg0d.shop.shiny.payload.response.RatingResponse;
import com.skyg0d.shop.shiny.util.product.ProductCreator;
import com.skyg0d.shop.shiny.util.user.UserCreator;

public class RatingCreator {
    
    public static final String COMMENT = "Test comment";
    public static final int STARS = 5;

    public static Rating createRatingToBeSave(Product product, User user) {
        return Rating
        .builder()
        .comment(COMMENT)
        .stars(STARS)
        .product(product)
        .user(user)
        .build();
    }

    public static Rating createRating(Product product, User user) {
        return Rating
                .builder()
                .comment(COMMENT)
                .stars(STARS)
                .product(product)
                .user(user)
                .build();
    }

    public static Rating createRating() {
        return createRating(ProductCreator.createProduct(), UserCreator.createUser());
    }

    public static RatingResponse createRatingResponse(Product product, User user) {
        return RatingMapper.INSTANCE.toRatingResponse(createRating(product, user));
    }

    public static RatingResponse createRatingResponse() {
        return RatingMapper.INSTANCE.toRatingResponse(createRating());
    }

    public static CreateRatingRequest createCreateRatingRequest() {
        return CreateRatingRequest
                .builder()
                .comment(COMMENT)
                .productSlug(ProductCreator.SLUG)
                .stars(STARS)
                .build();
    }

}
