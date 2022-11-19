package com.skyg0d.shop.shiny.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionUtils {

    public static final String DEFAULT_SEPARATOR = ", ";

    public static List<String> parseList(String value) {
        return parseList(value, DEFAULT_SEPARATOR);
    }

    public static List<String> parseList(String value, String separator) {
        if (value == null) {
            value = "";
        }

        return new ArrayList<>(Arrays.asList(value.split(separator)));
    }

}
