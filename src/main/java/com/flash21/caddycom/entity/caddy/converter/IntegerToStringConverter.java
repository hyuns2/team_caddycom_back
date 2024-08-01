package com.flash21.caddycom.entity.caddy.converter;

import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IntegerToStringConverter implements AttributeConverter<List<Integer>, String> {
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<Integer> offPartList) {
        if (offPartList == null || offPartList.isEmpty())
            return null;

        return offPartList.stream()
                .map(Integer::toUnsignedString)
                .collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<Integer> convertToEntityAttribute(String s) {
        if (s == null || s.isEmpty())
            return null;

        return Arrays.stream(s.split(SPLIT_CHAR))
                .map(Integer::new)
                .toList();
    }
}
