package com.skyg0d.shop.shiny.util.rating;

import com.skyg0d.shop.shiny.model.Product;
import com.skyg0d.shop.shiny.model.Rating;
import com.skyg0d.shop.shiny.model.User;

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

}
