package com.skyg0d.shop.shiny.exception.details;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class StripeExceptionDetails extends ExceptionDetails {

    private String stripeCode;
    private String stripeRequestId;
    private Integer stripeStatusCode;

}
