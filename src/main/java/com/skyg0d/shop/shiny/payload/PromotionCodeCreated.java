package com.skyg0d.shop.shiny.payload;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PromotionCodeCreated {

    private String couponId;
    private String promotionCodeId;

}
