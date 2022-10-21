package com.skyg0d.shop.shiny.payload.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull
    private List<CreateOrderProduct> products = new ArrayList<>();

    @NotEmpty
    private String userEmail;

}
