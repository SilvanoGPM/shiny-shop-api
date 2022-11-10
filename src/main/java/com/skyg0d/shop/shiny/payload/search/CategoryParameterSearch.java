package com.skyg0d.shop.shiny.payload.search;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CategoryParameterSearch {

    private String name;
    private String description;
    private String thumbnail;

    private String createdInDateOrAfter;
    private String createdInDateOrBefore;

}
