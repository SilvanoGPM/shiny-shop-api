package com.skyg0d.shop.shiny.payload.request;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyDiscountRequest {

    @PositiveOrZero
    @Max(100)
    private int discount;

}
