package com.skyg0d.shop.shiny.payload.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class NotificationParameterSearch extends AbstractParameterSearch {

    private String content;

    private String category;

    private String userEmail;

    private String readInDateOrAfter;
    private String readInDateOrBefore;

    private String canceledInDateOrAfter;
    private String canceledInDateOrBefore;

}
