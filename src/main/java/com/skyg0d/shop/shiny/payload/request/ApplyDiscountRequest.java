package com.skyg0d.shop.shiny.payload.request;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyDiscountRequest {

    private String name;
    private String code;

    @Positive
    @Max(100)
    private int discount;

}
