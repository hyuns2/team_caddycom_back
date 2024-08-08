package com.flash21.caddycom.entity.schedule.converter;

import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IdListConverter implements AttributeConverter<List<Long>, String> {

    private static final String SPLIT_CHAR = ",";
    @Override
    public String convertToDatabaseColumn(List<Long> idList) {
        if (idList == null || idList.isEmpty())
            return null;

        return idList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<Long> convertToEntityAttribute(String s) {
        if (s == null || s.isEmpty())
            return null;

        return Arrays.stream(s.split(SPLIT_CHAR))
                .map(Long::valueOf)
                .toList();
    }
}
