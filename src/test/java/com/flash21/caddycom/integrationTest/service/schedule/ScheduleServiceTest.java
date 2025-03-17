package com.flash21.caddycom.integrationTest.service.schedule;

import com.flash21.caddycom.dto.schedule.ReservationSheetRequest;
import com.flash21.caddycom.dto.schedule.ScheduleResponse;
import com.flash21.caddycom.service.schedule.ReservationSheetService;
import com.flash21.caddycom.service.schedule.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
@Transactional
public class ScheduleServiceTest {
    @Autowired
    ScheduleService scheduleService;
    @Autowired
    ReservationSheetService reservationSheetService;

    @BeforeEach
    void beforeEach() {
        reservationSheetService.createReservationSheet(ReservationSheetRequest.CreateOrUpdate.of(
                1L, List.of(1L, 2L),
                LocalDate.now(), LocalDate.now().plusDays(6),
                List.of(LocalTime.parse("08:00"), LocalTime.parse("13:00"), LocalTime.parse("18:00")),
                List.of(LocalTime.parse("12:00"), LocalTime.parse("17:00"), LocalTime.parse("22:00")),
                List.of("5", "5~6", "6")
        ));
        reservationSheetService.createReservationSheet(ReservationSheetRequest.CreateOrUpdate.of(
                1L, List.of(3L),
                LocalDate.now(), LocalDate.now().plusDays(6),
                List.of(LocalTime.parse("09:00"), LocalTime.parse("16:00")),
                List.of(LocalTime.parse("15:00"), LocalTime.parse("22:00")),
                List.of("5", "6")
        ));
    }

    @Test
    @DisplayName("캘린더의 메타정보를 성공적으로 조회합니다.")
    void getMetaData_success() {
        // given
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        // when
        log.info("start");
        List<ScheduleResponse.MetaData> dtos = scheduleService.getMetaData(1L, year, month);
        log.info("end");

        // then
        Assertions.assertThat(dtos.size()).isEqualTo(7);
        Assertions.assertThat(dtos.stream().map(ScheduleResponse.MetaData::getTotalCntSum).toList()).containsOnly(2 * (48 + 44 + 40) + (72 + 60));
    }

    @Test
    @DisplayName("배정정보를 성공적으로 조회 및 생성합니다.")
    void getAssignments_success() {
        // given

        // when
        log.info("start");
        Map<String, List<Object>> dtos = scheduleService.getAssignments(1L, LocalDate.now());
        log.info("end");

        // then
        Assertions.assertThat(dtos.size()).isEqualTo(5);

        List<LocalTime> times = scheduleService.getStartTimes(LocalTime.parse("08:00"), LocalTime.parse("12:00"), "5");
        times.addAll(scheduleService.getStartTimes(LocalTime.parse("13:00"), LocalTime.parse("17:00"), "5~6"));
        times.addAll(scheduleService.getStartTimes(LocalTime.parse("18:00"), LocalTime.parse("22:00"), "6"));
        times.addAll(scheduleService.getStartTimes(LocalTime.parse("09:00"), LocalTime.parse("15:00"), "5"));
        times.addAll(scheduleService.getStartTimes(LocalTime.parse("16:00"), LocalTime.parse("22:00"), "6"));
        times = times.stream().distinct().toList();

        Assertions.assertThat(dtos.get("courses").size()).isEqualTo(3);
        Assertions.assertThat(dtos.get("times").size()).isEqualTo(times.size());
        Assertions.assertThat(dtos.get("A").size()).isEqualTo(times.size());
        Assertions.assertThat(dtos.get("B").size()).isEqualTo(times.size());
        Assertions.assertThat(dtos.get("C").size()).isEqualTo(times.size());
    }
}
