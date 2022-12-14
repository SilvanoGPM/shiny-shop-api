package com.skyg0d.shop.shiny.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skyg0d.shop.shiny.model.Address;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupRequest {

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @JsonProperty("full_name")
    private String fullName;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private Address address;

}
