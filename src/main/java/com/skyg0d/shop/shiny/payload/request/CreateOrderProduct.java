package com.skyg0d.shop.shiny.payload.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderProduct {

    @NotBlank
    private String slug;

    @Positive
    private long amount;

}
