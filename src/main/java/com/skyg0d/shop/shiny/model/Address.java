package com.skyg0d.shop.shiny.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Address {

    private String country;
    private String state;
    private String city;
    private String neighborhood;
    private String street;
    private String number;

    @JsonProperty("reference_point")
    private String referencePoint;

}