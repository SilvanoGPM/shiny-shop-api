package com.skyg0d.shop.shiny.payload.search;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductParametersSearch {

    private String name;
    private String description;
    private String thumbnail;
    private String brand;

    private String createdInDateOrAfter;
    private String createdInDateOrBefore;

    private BigDecimal greaterThanOrEqualToPrice = new BigDecimal(-1);
    private BigDecimal lessThanOrEqualToPrice = new BigDecimal(-1);

    private long greaterThenOrEqualToAmount = -1;
    private long lessThenOrEqualToAmount = -1;

    private int greaterThenOrEqualToDiscount = -1;
    private int lessThenOrEqualToDiscount = -1;

    private int active = -1;

}
