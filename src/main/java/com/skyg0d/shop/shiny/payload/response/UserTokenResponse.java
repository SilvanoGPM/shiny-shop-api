package com.skyg0d.shop.shiny.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skyg0d.shop.shiny.model.RefreshToken;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UserTokenResponse {

    private String id;
    @JsonProperty("expiry_date")
    private Instant expiryDate;
    @Schema(description = "Token to generate others access tokens")
    private String token;
    @Schema(description = "Browser that requested the token")
    private String browser;

    @Schema(description = "Operating System that requested the token")
    private String operatingSystem;

    @Schema(description = "Ip Address that requested the token")
    private String ipAddress;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public UserTokenResponse(RefreshToken refreshToken) {
        this.id = refreshToken.getId().toString();
        this.expiryDate = refreshToken.getExpiryDate();
        this.token = refreshToken.getToken();
        this.browser = refreshToken.getBrowser();
        this.operatingSystem = refreshToken.getOperatingSystem();
        this.ipAddress = refreshToken.getIpAddress();
        this.createdAt = refreshToken.getCreatedAt();
        this.updatedAt = refreshToken.getUpdatedAt();
    }

}
