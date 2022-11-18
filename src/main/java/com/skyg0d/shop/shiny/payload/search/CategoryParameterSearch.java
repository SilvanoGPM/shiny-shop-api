package com.skyg0d.shop.shiny.payload.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class CategoryParameterSearch extends AbstractParameterSearch {

    private String name;
    private String description;
    private String thumbnail;

}
