package com.skyg0d.shop.shiny.payload.request;

import com.skyg0d.shop.shiny.model.Category;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProductRequest {

    @NotBlank
    private String slug;

    @NotBlank
    private String name;

    private String description;

    private String thumbnail;

    private String brand;

    @Positive
    private BigDecimal price;

    @PositiveOrZero
    private long amount;

    private Set<Category> categories;

    private Set<String> images;

    private Set<String> sizes;

    private Set<String> features;

}
