package com.skyg0d.shop.shiny.payload.response;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@EqualsAndHashCode
public class OrderProductResponse {

    private String slug;

    private String name;

    private String thumbnail;

    private String brand;

    private BigDecimal price;

    private Long amount;

    private Integer discount;

}
