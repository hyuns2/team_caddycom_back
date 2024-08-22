package com.flash21.caddycom.entity.caddy.converter;

import com.flash21.caddycom.entity.caddy.Days;
import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DayListConverter implements AttributeConverter<List<Days>, String> {
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<Days> days) {
        if (days == null || days.isEmpty())
            return null;

        return days.stream()
                .map(Days::getNumber)
                .collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<Days> convertToEntityAttribute(String s) {
        if (s == null || s.isEmpty())
            return null;

        return Arrays.stream(s.split(SPLIT_CHAR))
                .map(Days::fromNumberString)
                .toList();
    }
}
