package com.flash21.caddycom.entity.caddy.converter;

import com.flash21.caddycom.entity.caddy.Days;
import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PartListConverter implements AttributeConverter<List<Integer>, String> {

    private static final String SPLIT_CHAR = ",";
    @Override
    public String convertToDatabaseColumn(List<Integer> parts) {
        if (parts == null || parts.isEmpty())
            return null;

        return parts.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<Integer> convertToEntityAttribute(String s) {
        if (s == null || s.isEmpty())
            return null;

        return Arrays.stream(s.split(SPLIT_CHAR))
                .map(Integer::valueOf)
                .toList();
    }
}
