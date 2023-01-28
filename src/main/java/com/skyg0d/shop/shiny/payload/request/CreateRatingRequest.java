package com.skyg0d.shop.shiny.payload.request;

import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRatingRequest {

    @NotBlank
    private String comment;

    @Range(min = 1, max = 5)
    private int stars;

    @NotEmpty
    private String productSlug;

}
