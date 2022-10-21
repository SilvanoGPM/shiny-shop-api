package com.skyg0d.shop.shiny.payload;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductCalculate {

    private UUID id;

    private String slug;

    private BigDecimal price;

    private long amount;

}
