package com.skyg0d.shop.shiny.payload.response;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private String slug;

    private String name;

    private String description;

    private String thumbnail;

}
