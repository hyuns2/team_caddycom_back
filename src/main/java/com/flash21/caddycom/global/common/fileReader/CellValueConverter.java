package com.flash21.caddycom.global.common.fileReader;

import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.Gender;
import com.flash21.caddycom.entity.caddy.TeamRole;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class CellValueConverter {
    private final LocalDate baseDate = LocalDate.of(1900,1,1);

    protected String convertPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("[^0-9]", "");
    }

    protected Gender convertGender(String gender) {
        if (gender.equals("남")) {
            return Gender.MALE;
        }
        else return Gender.FEMALE;
    }

    protected TeamRole convertTeamRole(String teamRole) {
        if (teamRole.equals("조장")) {
            return TeamRole.LEADER;
        }
        else return TeamRole.MEMBER;
    }

    protected List<Days> convertHoliday(String holidayString) {
        if (holidayString == null || holidayString.isEmpty()) {
            return new ArrayList<>();
        }
        List<Days> days = new ArrayList<>();
        for (int i = 0; i < holidayString.length(); i++) {
            String dayKorean = holidayString.substring(i, i + 1);
            Days day = switch (dayKorean) {
                case "월" -> Days.MON;
                case "화" -> Days.TUE;
                case "수" -> Days.WED;
                case "목" -> Days.THU;
                case "금" -> Days.FRI;
                case "토" -> Days.SAT;
                case "일" -> Days.SUN;
                default -> throw new IllegalStateException("요일 값이 잘못되었습니다. " + dayKorean);
            };
            days.add(day);
        }
        return days;
    }

    protected List<Integer> convertPart(String partString) {
        int limitPart = 2; // 골프장의 최대 Part 개수

        if (partString == null || partString.isEmpty()) {
            return null;
        }
        List<String> parts = Arrays.asList(partString.split(","));
        List<Integer> offPart = parts.stream().map(part -> part.replaceAll("\\D",""))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .toList();

        List<Integer> range = IntStream.rangeClosed(1, limitPart)
                .boxed()
                .toList();

        List<Integer> result = range.stream()
                .filter(num -> !offPart.contains(num))
                .toList();

        return result;
    }


    protected LocalDate convertBirth(String birth) {
        int days = Integer.parseInt(birth)-2;
        return baseDate.plusDays(days);
    }
}
