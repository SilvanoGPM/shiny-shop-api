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

    @Builder.Default
    private BigDecimal greaterThanOrEqualToPrice = new BigDecimal(-1);

    @Builder.Default
    private BigDecimal lessThanOrEqualToPrice = new BigDecimal(-1);

    @Builder.Default
    private long greaterThenOrEqualToAmount = -1;

    @Builder.Default
    private long lessThenOrEqualToAmount = -1;

    @Builder.Default
    private int greaterThenOrEqualToDiscount = -1;

    @Builder.Default
    private int lessThenOrEqualToDiscount = -1;

    @Builder.Default
    private int active = -1;

    private String categoryName;
    private String categoryDescription;

    private String sizes;
    private String features;

}
