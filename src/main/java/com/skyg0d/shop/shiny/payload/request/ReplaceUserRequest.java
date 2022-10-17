package com.skyg0d.shop.shiny.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skyg0d.shop.shiny.model.Address;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplaceUserRequest {

    @NotBlank
    private String email;

    @NotBlank
    @Size(max = 100)
    private String username;

    @JsonProperty("full_name")
    private String fullName;

    private Address address;

    private String photoURL;

}
