package com.skyg0d.shop.shiny.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skyg0d.shop.shiny.model.Address;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private String username;

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    private Address address;

    private Set<String> roles = new HashSet<>();

}
