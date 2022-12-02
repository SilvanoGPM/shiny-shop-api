package com.skyg0d.shop.shiny.payload;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StripePriceItem {

    private String priceId;

    private long amount;

}
