package com.skyg0d.shop.shiny.property;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.stripe")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StripeProps {

    private String publicKey;
    private String secretKey;
    private String currency = "BRL";
    private String redirectUrl = "https://dashboard.stripe.com/test/payments";
    private String webhookSecret;

}
