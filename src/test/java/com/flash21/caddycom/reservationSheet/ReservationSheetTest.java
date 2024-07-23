package com.flash21.caddycom.reservationSheet;

import com.flash21.caddycom.dto.reservationSheet.ReservationSheetDto;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.reservationSheet.ReservationDate;
import com.flash21.caddycom.entity.reservationSheet.ReservationSheet;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
import com.flash21.caddycom.repository.reservationSheet.ReservationDateRepository;
import com.flash21.caddycom.repository.reservationSheet.ReservationSheetRepository;
import com.flash21.caddycom.service.reservationSheet.ReservationSheetService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
public class ReservationSheetTest {
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    ReservationSheetService reservationSheetService;
    @Autowired
    ReservationSheetRepository reservationSheetRepository;
    @Autowired
    ReservationDateRepository reservationDateRepository;

    /*
    2. 메타데이터 조회 - 정상, 데이터x시

    1.
     */
    Course course1;
    Course course2;
    List<Long> courseList = new ArrayList<>();
    List<String> startTimeList = new ArrayList<>();
    List<String> endTimeList = new ArrayList<>();
    List<String> teeOffList = new ArrayList<>();
    LocalDate startDate;
    LocalDate endDate;

    @BeforeEach
    void before() {
        course1 = new Course("A", 10, null);
        course2 = new Course("B", 15, null);
        courseList.add(courseRepository.save(course1).getId());
        courseList.add(courseRepository.save(course2).getId());

        startDate = LocalDate.of(2024, 7, 23);
        endDate = LocalDate.of(2024, 7, 31);
        startTimeList.add("13:00");
        endTimeList.add("17:00");
        startTimeList.add("18:00");
        endTimeList.add("22:00");
    }

    @AfterEach
    void after() {
        courseRepository.delete(course1);
        courseRepository.delete(course2);

        courseList.clear();
        startTimeList.clear();
        endTimeList.clear();
        teeOffList.clear();
    }

    @Test
    @DisplayName("예약시트 등록 - 성공")
    void createReservationSheetSuccess() {
        // given
        teeOffList.add("7");
        teeOffList.add("8~9");

        ReservationSheetDto.CreateRequest dto = new ReservationSheetDto.CreateRequest(
                courseList,
                startDate, endDate,
                startTimeList, endTimeList, teeOffList
        );

        // when
        Long id = reservationSheetService.createReservationSheet(dto);

        // then
        List<ReservationSheet> reservationSheetList = reservationSheetRepository.findAllByReservationSheetInfoId(id);
        Assertions.assertThat(reservationSheetList.size()).isEqualTo(4);

        List<LocalDate> localDateList = startDate.datesUntil(endDate.plusDays(1)).toList();
        for (ReservationSheet reservationSheet: reservationSheetList) {
            List<ReservationDate> reservationDateList = reservationDateRepository.findAllByReservationSheetIdOrderByReservationAt(reservationSheet.getId());
            Assertions.assertThat(reservationDateList.stream().map(ReservationDate::getReservationAt).toList())
                    .isEqualTo(localDateList);
        }
    }
}
