package com.skyg0d.shop.shiny.payload.search;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserParameterSearch {

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

    private String createdInDateOrAfter;
    private String createdInDateOrBefore;

}
