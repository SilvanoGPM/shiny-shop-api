package com.skyg0d.shop.shiny.payload.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class RatingResponse {

    private String id;

    private String comment;

    private int stars;

    private UserResponse user;

    private UserProductResponse product;

}
