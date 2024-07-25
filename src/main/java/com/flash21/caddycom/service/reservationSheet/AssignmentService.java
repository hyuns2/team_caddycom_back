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
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentService {
    final ReservationSheetService rsService;
    final ReservationSheetRepository rsRepository;
    final ReservationDateRepository rdRepository;
    final AssignmentRepository assignmentRepository;
    final AssignmentJdbcRepository assignmentJdbcRepository;
    final CourseRepository courseRepository;

    /**
     * 배정정보 조회: 배정정보가 존재하는 경우에는 반환하고, 존재하지 않는 경우에는 생성하여 반환합니다.
     *
     * @param golfFieldId 골프장 Id
     * @param targetDate 대상 날짜
     * @param page 페이지 번호 (데이터 10개)
     * @return 코스리스트, 시간리스트, 부별 id-status 형태의 map 반환
     *
     * @throws CReservationDateNotFoundException ReservationDate 객체가 존재하지 않을 경우
     * @throws CReservationSheetNotFoundException ReservationSheet 객체가 존재하지 않을 경우
     */
    @Transactional
    public Map<String, Map<String, AssignmentDto.AssignmentsResponse>> getAssignments(Long golfFieldId, LocalDate targetDate, int page) {
        List<ReservationSheet> reservationSheetList = rsRepository.findAllByGolfFieldId(golfFieldId);
        if (reservationSheetList.isEmpty())
            throw new CReservationSheetNotFoundException();

        List<Course> courseList = new ArrayList<>();
        for (ReservationSheet reservationSheet : reservationSheetList) {
            ReservationDate reservationDate = rdRepository.findByReservationSheetIdAndReservationAt(reservationSheet.getId(), targetDate)
                    .orElseThrow(CReservationDateNotFoundException::new);

            if (!reservationDate.getIsAssigned())
                createAssignments(reservationSheet, reservationDate);

            courseList.add(reservationSheet.getCourse());
        }

        return findAndGetAssignmentsByCourse(courseList, targetDate, page);
    }

    /**
     * 배정정보 조회 내부함수1: 코스와 날짜에 따른 배정정보를 조회하여 반환합니다.
     *
     * @param date 대상 날짜
     * @param page page 페이지 번호 (데이터 10개)
     * @return 시간, 코스별 dto 형태의 map 반환
     */
    private Map<String, Map<String, AssignmentDto.AssignmentsResponse>> findAndGetAssignmentsByCourse(List<Course> courseList, LocalDate date, int page) {
        Map<String, Map<String, AssignmentDto.AssignmentsResponse>> result = new TreeMap<>();
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);

        for (Course course: courseList) {
            Page<Assignment> assignmentPageList = assignmentRepository.findAllByCourseAndReservationDate(course.getId(), date, pageable);
            List<Assignment> assignmentList = assignmentPageList.getContent();

            for (Assignment assignment: assignmentList) {
                String startTime = assignment.getStartTime().toString();
                AssignmentDto.AssignmentsResponse dto = AssignmentDto.AssignmentsResponse.builder()
                        .id(assignment.getId())
                        .status(assignment.getStatus()).build();

                if (result.containsKey(startTime))
                    result.get(startTime).put(course.getName(), dto);
                else {
                    Map<String, AssignmentDto.AssignmentsResponse> dtoMap = new WeakHashMap<>();
                    dtoMap.put(course.getName(), dto);
                    result.put(startTime, dtoMap);
                }
            }
        }

        result.forEach((key, value) -> {
            for (Course course: courseList) {
                if (!value.containsKey(course.getName()))
                    value.put(course.getName(), null);
            }
        });
        return result;
    }

    /**
     * 배정정보 조회 내부함수2: 배정정보를 생성합니다.
     *
     * @param reservationSheet 대상 reservationSheet 객체
     * @param reservationDate 대상 reservationDate 객체
     */
    private void createAssignments(ReservationSheet reservationSheet, ReservationDate reservationDate) {
        assignmentJdbcRepository.saveAll(reservationDate.getId(), rsService.getStartTimeList(
                reservationSheet.getStartDateTime().toLocalTime(), reservationSheet.getEndDateTime().toLocalTime(), reservationSheet.getTeeOff()));

        reservationDate.setIsAssigned();
    }
}
