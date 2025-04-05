package com.flash21.caddycom.integrationTest.service.schedule;

import com.flash21.caddycom.dto.schedule.ReservationSheetRequest;
import com.flash21.caddycom.dto.schedule.ReservationSheetResponse;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.global.exception.CustomException;
import com.flash21.caddycom.global.exception.ErrorCode;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
import com.flash21.caddycom.repository.schedule.ReservationSheetRepository;
import com.flash21.caddycom.repository.schedule.ScheduleRepository;
import com.flash21.caddycom.service.schedule.ReservationSheetService;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@SpringBootTest
@Transactional
public class ReservationSheetServiceTest {
    @Autowired
    ReservationSheetService reservationSheetService;
    @Autowired
    ReservationSheetRepository reservationSheetRepository;
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("예약시트를 성공적으로 생성합니다.")
    void createReservationSheet_success() {
        // given
        ReservationSheetRequest.CreateOrUpdate dto = ReservationSheetRequest.CreateOrUpdate.of(1L, List.of(1L, 2L),
                LocalDate.now(), LocalDate.now().plusDays(6),
                List.of(LocalTime.parse("08:00"), LocalTime.parse("13:00"), LocalTime.parse("18:00")),
                List.of(LocalTime.parse("12:00"), LocalTime.parse("17:00"), LocalTime.parse("22:00")),
                List.of("5", "5~6", "6"));

        // when
        log.info("start");
        Long reservationSheetId = reservationSheetService.createReservationSheet(dto);
        log.info("end");

        // then
        Assertions.assertThat(reservationSheetRepository.findById(reservationSheetId)).isPresent();
        Assertions.assertThat(scheduleRepository.findAllByReservationSheetId(reservationSheetId).size()).isEqualTo(2 * 7 * 3);
    }

    @Test
    @DisplayName("요청 시작 날짜가 오늘보다 이전이라면, 예약시트 생성에 실패합니다.")
    void createReservationSheet_fail_1() {
        // given
        ReservationSheetRequest.CreateOrUpdate dto = ReservationSheetRequest.CreateOrUpdate.of(1L, List.of(1L, 2L),
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(6),
                List.of(LocalTime.parse("08:00"), LocalTime.parse("13:00"), LocalTime.parse("18:00")),
                List.of(LocalTime.parse("12:00"), LocalTime.parse("17:00"), LocalTime.parse("22:00")),
                List.of("5", "5~6", "6"));

        // when, then
        log.info("start");
        Assertions.assertThatThrownBy(() -> reservationSheetService.createReservationSheet(dto)).isInstanceOf(CustomException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.INVALID_DATE_ORDER);
        log.info("end");
    }

    @Test
    @DisplayName("시작시간 리스트, 종료시간 리스트, 티오프간격 리스트의 길이가 모두 같지 않을 경우, 예약시트 생성에 실패합니다.")
    void createReservationSheet_fail_2() {
        // given
        ReservationSheetRequest.CreateOrUpdate dto = ReservationSheetRequest.CreateOrUpdate.of(1L, List.of(1L, 2L),
                LocalDate.now(), LocalDate.now().plusDays(6),
                List.of(LocalTime.parse("08:00"), LocalTime.parse("13:00"), LocalTime.parse("18:00")),
                List.of(LocalTime.parse("12:00"), LocalTime.parse("17:00"), LocalTime.parse("22:00")),
                List.of("5~6", "6"));

        // when, then
        log.info("start");
        Assertions.assertThatThrownBy(() -> reservationSheetService.createReservationSheet(dto)).isInstanceOf(CustomException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.INVALID_PART_INFO);
        log.info("end");
    }

    @Test
    @DisplayName("요청 코스와 날짜가 속하는 예약 스케쥴이 이미 존재하는 경우, 예약시트 생성에 실패합니다.")
    void createReservationSheet_fail_3() {
        // given
        reservationSheetService.createReservationSheet(ReservationSheetRequest.CreateOrUpdate.of(1L, List.of(2L),
                LocalDate.now(), LocalDate.now(),
                List.of(LocalTime.parse("08:00"), LocalTime.parse("13:00"), LocalTime.parse("18:00")),
                List.of(LocalTime.parse("12:00"), LocalTime.parse("17:00"), LocalTime.parse("22:00")),
                List.of("5", "5~6", "6")));

        ReservationSheetRequest.CreateOrUpdate dto = ReservationSheetRequest.CreateOrUpdate.of(1L, List.of(1L, 2L),
                LocalDate.now(), LocalDate.now().plusDays(6),
                List.of(LocalTime.parse("08:00"), LocalTime.parse("13:00"), LocalTime.parse("18:00")),
                List.of(LocalTime.parse("12:00"), LocalTime.parse("17:00"), LocalTime.parse("22:00")),
                List.of("5", "5~6", "6"));

        // when, then
        log.info("start");
        Assertions.assertThatThrownBy(() -> reservationSheetService.createReservationSheet(dto)).isInstanceOf(CustomException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.BAD_RESERVATION_REQUEST);
        log.info("end");
    }

    @Test
    @DisplayName("예약시트 전체를 성공적으로 조회합니다.")
    void getReservationSheets_success() {
        // given
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

        // when
        log.info("start");
        List<ReservationSheetResponse.Get> dtos = reservationSheetService.getReservationSheet(1L);
        log.info("end");

        // then
        Assertions.assertThat(dtos.size()).isEqualTo(2);

        List<String> courseNames = courseRepository.findAllById(List.of(1L, 2L, 3L)).stream()
                .map(Course::getName)
                .sorted().toList();
        Assertions.assertThat(dtos.stream()
                .map(dto -> dto.getCourses().values().stream().toList())
                .flatMap(List::stream)
                .sorted().toList()).isEqualTo(courseNames);
    }

    @Test
    @DisplayName("예약시트를 성공적으로 수정합니다.")
    void updateReservationSheet_success() {
        // given
        Long reservationSheetId = reservationSheetService.createReservationSheet(ReservationSheetRequest.CreateOrUpdate.of(
                1L, List.of(1L, 2L),
                LocalDate.now(), LocalDate.now().plusDays(6),
                List.of(LocalTime.parse("08:00"), LocalTime.parse("13:00"), LocalTime.parse("18:00")),
                List.of(LocalTime.parse("12:00"), LocalTime.parse("17:00"), LocalTime.parse("22:00")),
                List.of("5", "5~6", "6")
        ));
        entityManager.flush();
        entityManager.clear();

        // when
        log.info("start");
        reservationSheetService.updateReservationSheet(reservationSheetId, ReservationSheetRequest.CreateOrUpdate.of(
                1L, List.of(1L, 2L, 3L),
                LocalDate.now(), LocalDate.now().plusDays(6),
                List.of(LocalTime.parse("08:00"), LocalTime.parse("13:00"), LocalTime.parse("18:00")),
                List.of(LocalTime.parse("12:00"), LocalTime.parse("17:00"), LocalTime.parse("22:00")),
                List.of("5", "5~6", "6")
        ));

        entityManager.flush();
        entityManager.clear();
        log.info("end");

        // then
        Assertions.assertThat(reservationSheetRepository.findById(reservationSheetId).get()
                .getCourseIds().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("예약시트를 성공적으로 삭제합니다.")
    void deleteReservationSheets_success() {
        // given
        Long reservationSheetId = reservationSheetService.createReservationSheet(ReservationSheetRequest.CreateOrUpdate.of(
                1L, List.of(1L, 2L),
                LocalDate.now(), LocalDate.now().plusDays(6),
                List.of(LocalTime.parse("08:00"), LocalTime.parse("13:00"), LocalTime.parse("18:00")),
                List.of(LocalTime.parse("12:00"), LocalTime.parse("17:00"), LocalTime.parse("22:00")),
                List.of("5", "5~6", "6")
        ));

        // when
        log.info("start");
        reservationSheetService.deleteReservationSheet(reservationSheetId);
        log.info("end");

        // then
        Assertions.assertThat(reservationSheetRepository.findById(reservationSheetId)).isEmpty();
    }
}
