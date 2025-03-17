package com.flash21.caddycom.entity.schedule.converter;

import jakarta.persistence.AttributeConverter;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LocalTimeListConverter implements AttributeConverter<List<LocalTime>, String> {
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<LocalTime> localTimeList) {
        if (localTimeList == null || localTimeList.isEmpty())
            return null;

        return localTimeList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<LocalTime> convertToEntityAttribute(String s) {
        if (s == null || s.isEmpty())
            return null;

        return Arrays.stream(s.split(SPLIT_CHAR))
                .map(LocalTime::parse)
                .toList();
    }
}