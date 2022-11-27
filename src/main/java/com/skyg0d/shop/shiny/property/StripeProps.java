package com.skyg0d.shop.shiny.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.stripe")
@Getter
@Setter
@ToString
public class StripeProps {

    private String publicKey;
    private String secretKey;
    private String currency = "BRL";

}
