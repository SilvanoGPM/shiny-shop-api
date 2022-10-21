package com.skyg0d.shop.shiny.converter;

import com.skyg0d.shop.shiny.model.EOrderStatus;
import org.springframework.core.convert.converter.Converter;

public class StringToEnumConverter implements Converter<String, EOrderStatus> {
    @Override
    public EOrderStatus convert(String source) {
        return EOrderStatus.valueOf(source.toUpperCase());
    }
}
