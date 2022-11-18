package com.skyg0d.shop.shiny.payload.search;

import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class AbstractParameterSearch {
    protected String createdInDateOrAfter;
    protected String createdInDateOrBefore;
}
