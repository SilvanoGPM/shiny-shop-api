package com.skyg0d.shop.shiny.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class MyPaymentLink {

    @Schema(description = "Id of payment link")
    private String paymentId;

    @Schema(description = "Url of payment link")
    private String paymentUrl;

}
