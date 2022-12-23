package com.skyg0d.shop.shiny.payload.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateNotificationRequest {

    @NotBlank
    private String content;

    @NotBlank
    private String category;

    @NotBlank
    private String userEmail;

}
