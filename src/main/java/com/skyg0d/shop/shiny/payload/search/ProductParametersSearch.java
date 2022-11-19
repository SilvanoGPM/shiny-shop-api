package com.skyg0d.shop.shiny.payload.search;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class ProductParametersSearch extends AbstractParameterSearch {

    private String name;
    private String description;
    private String thumbnail;
    private String brand;

    private BigDecimal greaterThanOrEqualToPrice = new BigDecimal(-1);
    private BigDecimal lessThanOrEqualToPrice = new BigDecimal(-1);

    private long greaterThenOrEqualToAmount = -1;
    private long lessThenOrEqualToAmount = -1;

    private int greaterThenOrEqualToDiscount = -1;
    private int lessThenOrEqualToDiscount = -1;

    private int active = -1;

    private String categoryName;
    private String categoryDescription;

    private String sizes;
    private String features;

}
