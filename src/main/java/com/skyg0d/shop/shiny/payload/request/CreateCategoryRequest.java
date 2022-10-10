package com.skyg0d.shop.shiny.payload.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCategoryRequest {

    @NotBlank
    private String slug;

    @NotBlank
    private String name;

    private String description;

    private String thumbnail;

}
