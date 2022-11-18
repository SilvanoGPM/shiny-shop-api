package com.skyg0d.shop.shiny.payload.search;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class OrderParameterSearch extends AbstractParameterSearch {

    private BigDecimal greaterThanOrEqualToPrice = new BigDecimal(-1);
    private BigDecimal lessThanOrEqualToPrice = new BigDecimal(-1);

    private String productName;
    private String productDescription;
    private String productBrand;

    private String userUsername;
    private String userFullName;
    private String userEmail;

    private String status;

}
