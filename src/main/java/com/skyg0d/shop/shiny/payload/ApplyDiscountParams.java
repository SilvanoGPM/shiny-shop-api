package com.skyg0d.shop.shiny.payload;

import com.skyg0d.shop.shiny.payload.request.ApplyDiscountRequest;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyDiscountParams {

    private String name;
    private String code;
    private String productSlug;

    @Positive
    @Max(100)
    private int discount;

    public static ApplyDiscountParams fromRequest(ApplyDiscountRequest request, String productSlug) {
        return ApplyDiscountParams
                .builder()
                .name(request.getName())
                .code(request.getCode())
                .discount(request.getDiscount())
                .productSlug(productSlug)
                .build();
    }

}
