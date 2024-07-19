package com.flash21.caddycom.service.reservationSheet;

import com.flash21.caddycom.dto.reservationSheet.AssignmentDto;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.reservationSheet.Assignment;
import com.flash21.caddycom.entity.reservationSheet.ReservationDate;
import com.flash21.caddycom.entity.reservationSheet.ReservationSheet;
import com.flash21.caddycom.global.exception.cException.CCourseNotFoundException;
import com.flash21.caddycom.global.exception.cException.CReservationDateNotFoundException;
import com.flash21.caddycom.global.exception.cException.CReservationSheetNotFoundException;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
import com.flash21.caddycom.repository.reservationSheet.AssignmentJdbcRepository;
import com.flash21.caddycom.repository.reservationSheet.AssignmentRepository;
import com.flash21.caddycom.repository.reservationSheet.ReservationDateRepository;
import com.flash21.caddycom.repository.reservationSheet.ReservationSheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    final ReservationSheetRepository rsRepository;
    final ReservationDateRepository rdRepository;
    final AssignmentRepository assignmentRepository;
    final AssignmentJdbcRepository assignmentJdbcRepository;

    final CourseRepository courseRepository;

    /**
     * 배정정보 조회: 배정정보가 존재하는 경우에는 반환하고, 존재하지 않는 경우에는 생성하여 반환합니다.
     *
     * @param reservationSheetInfoId 대상 예약시트 Id
     * @param targetDate 대상 날짜
     * @param part 부 (0일 경우, 전체 조회)
     * @param page 페이지 번호 (데이터 10개)
     * @return 코스리스트, 시간리스트, 부별 id-status 형태의 map 반환
     *
     * @throws CReservationDateNotFoundException ReservationDate 객체가 존재하지 않을 경우
     * @throws CReservationSheetNotFoundException ReservationSheet 객체가 존재하지 않을 경우
     */
    @Transactional
    public Map<String, List<String>> getAssignments(Long reservationSheetInfoId, LocalDate targetDate, int part, int page) {
        Set<Course> courseList = new HashSet<>();

        List<ReservationSheet> reservationSheetList = rsRepository.findAllByReservationSheetInfoId(reservationSheetInfoId);
        if (reservationSheetList.isEmpty())
            throw new CReservationSheetNotFoundException();
        for (ReservationSheet reservationSheet : reservationSheetList) {
            Course course = reservationSheet.getCourse();
            ReservationDate reservationDate = rdRepository.findByReservationSheetIdAndReservationAt(reservationSheet.getId(), targetDate)
                    .orElseThrow(CReservationDateNotFoundException::new);

            if (!reservationDate.getIsAssigned())
                createAssignments(reservationSheet, reservationDate);
            courseList.add(course);
        }

        return findAndGetAssignmentsByCourse(courseList, targetDate, part, page);
    }

    /**
     * 배정정보 조회 내부함수1: 코스와 부, 날짜에 따른 배정정보를 조회하여 반환합니다.
     *
     * @param courseList 대상 코스리스트
     * @param date 대상 날짜
     * @param part 대상 부 (0일 경우, 전체 조회)
     * @param page page 페이지 번호 (데이터 10개)
     * @return 코스리스트, 시간리스트, 부별 id-status 형태의 map 반환
     */
    private Map<String, List<String>> findAndGetAssignmentsByCourse(Set<Course> courseList, LocalDate date, int part, int page) {
        Map<String, List<String>> result = new WeakHashMap<>();
        Set<LocalTime> timeList = new HashSet<>();

        for (Course course: courseList) {
            List<String> resultListByCourse = new ArrayList<>();
            int pageSize = 10;
            Pageable pageable = PageRequest.of(page, pageSize);

            Page<Assignment> assignmentPageList = part==0
              ?  assignmentRepository.findAllByCourseAndReservationDate(course, date, pageable)
              :  assignmentRepository.findAllByCourseAndPartAndReservationDate(course, part, date, pageable);
            List<Assignment> assignmentList = assignmentPageList.getContent();

            for (Assignment assignment : assignmentList) {
                timeList.add(assignment.getStartTime());
                resultListByCourse.add(assignment.getId() + "-" + assignment.getStatus());
            }

            result.put(course.getName(), resultListByCourse);
        }

        result.put("courseList", courseList.stream().map(Course::getName).sorted().toList());
        result.put("timeList", timeList.stream().sorted().map(LocalTime::toString).toList());
        return result;
    }

    /**
     * 배정정보 조회 내부함수2: 배정정보를 생성합니다.
     *
     * @param reservationSheet 대상 reservationSheet 객체
     * @param reservationDate 대상 reservationDate 객체
     */
    private void createAssignments(ReservationSheet reservationSheet, ReservationDate reservationDate) {
        LocalTime startAtLocalTime = reservationSheet.getStartTime();
        LocalTime endAtLocalTIme = reservationSheet.getEndTime();

        List<Integer> teeOffList = Arrays.stream(reservationSheet.getTeeOff().split("~")).
                map(Integer::new).toList();
        int teeOffListSize = teeOffList.size();
        int currentTeeOffIndex = 0;

        List<LocalTime> startTimeList = new ArrayList<>();
        while (startAtLocalTime.isBefore(endAtLocalTIme)) {
            startTimeList.add(startAtLocalTime);

            startAtLocalTime = startAtLocalTime.plusMinutes(teeOffList.get(currentTeeOffIndex++));
            if (currentTeeOffIndex >= teeOffListSize)
                currentTeeOffIndex = 0;
        }
        assignmentJdbcRepository.saveAll(reservationDate.getId(), startTimeList);

        reservationDate.setIsAssigned();
        reservationDate.setTotalCnt(startTimeList.size());
        reservationDate.setBlockedCnt(startTimeList.size());
    }

    /**
     * Assignment 객체를 배정정보 조회 dto로 변환합니다.
     *
     * @param assignment Assignment 객체
     * @return 배정정보 조회 dto
     */
    private AssignmentDto.AssignmentsResponse toDto(Assignment assignment) {
        return AssignmentDto.AssignmentsResponse.builder().
                id(assignment.getId()).
                startTime(assignment.getStartTime()).
                status(assignment.getStatus()).
                // caddyId(assignment.getCaddyId()).
                caddyName(assignment.getCaddyName()).
                reason(assignment.getReason()).build();
    }
}
