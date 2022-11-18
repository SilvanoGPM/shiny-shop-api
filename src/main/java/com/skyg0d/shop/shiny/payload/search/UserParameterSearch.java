package com.skyg0d.shop.shiny.payload.search;

import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class UserParameterSearch extends AbstractParameterSearch {

    private String username;
    private String fullName;
    private String email;
    private String photoURL;

    private String role;

    private String country;
    private String state;
    private String city;
    private String neighborhood;
    private String street;
    private String number;
    private String referencePoint;

}
