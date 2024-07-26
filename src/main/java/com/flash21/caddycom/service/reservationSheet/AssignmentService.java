package com.flash21.caddycom.service.reservationSheet;

import com.flash21.caddycom.dto.reservationSheet.AssignmentDto;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.reservationSheet.Assignment;
import com.flash21.caddycom.entity.reservationSheet.ReservationDate;
import com.flash21.caddycom.entity.reservationSheet.ReservationSheet;
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
    public Map<String, List<Object>> getAssignments(Long golfFieldId, LocalDate targetDate, int page) {
        List<ReservationSheet> reservationSheetList = rsRepository.findAllByGolfFieldId(golfFieldId);
        if (reservationSheetList.isEmpty())
            throw new CReservationSheetNotFoundException();

        Set<Course> courseSet = new HashSet<>();
        for (ReservationSheet reservationSheet : reservationSheetList) {
            ReservationDate reservationDate = rdRepository.findByReservationSheetIdAndReservationAt(reservationSheet.getId(), targetDate)
                    .orElseThrow(CReservationDateNotFoundException::new);

            if (!reservationDate.getIsAssigned())
                createAssignments(reservationSheet, reservationDate);

            courseSet.add(reservationSheet.getCourse());
        }

        List<String> courseNameList = courseSet.stream().map(Course::getName).sorted().toList();
        return makeResponse(courseNameList, findAndGetAssignmentsByTime(courseNameList, targetDate, page));
    }

    /**
     * 배정정보 조회 내부함수1: 코스와 날짜에 따른 배정정보를 조회하여 반환합니다.
     *
     * @param date 대상 날짜
     * @param page page 페이지 번호 (데이터 10개)
     * @return 시간, 코스별 dto 형태의 map 반환
     */
    private Map<String, Map<String, AssignmentDto.AssignmentsResponse>> findAndGetAssignmentsByTime(List<String> courseNameList, LocalDate date, int page) {
        Map<String, Map<String, AssignmentDto.AssignmentsResponse>> result = new TreeMap<>();

        getResultFromRepo(result, date, page);

        result.forEach((key, value) -> {
            for (String courseName: courseNameList) {
                if (!value.containsKey(courseName))
                    value.put(courseName, null);
            }
        });

        return result;
    }

    /**
     * 배정정보 조회 내부함수1-1: 한 페이지만큼의 시간을 추출하고, 이 예약시간을 가지는 코스 정보를 조회하여 반환합니다.
     *
     * @param result 예약시간과 예약시간을 가지는 코스 정보 형태의 map
     * @param date 대상 날짜
     * @param page page 페이지 번호 (데이터 10개)
     */
    private void getResultFromRepo(Map<String, Map<String, AssignmentDto.AssignmentsResponse>> result, LocalDate date, int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<LocalTime> resultTimePage = assignmentRepository.findTimesByReservationDate(date, pageable);
        List<LocalTime> resultTimeList = resultTimePage.getContent();

        List<Assignment> assignmentList = assignmentRepository.findAllByReservationDateAndBetweenTime(date, resultTimeList.get(0), resultTimeList.get(resultTimeList.size()-1));
        for (Assignment assignment: assignmentList) {
            String startTime = assignment.getStartTime().toString();
            String courseName = assignment.getReservationDate().getReservationSheet().getCourse().getName();
            AssignmentDto.AssignmentsResponse dto = AssignmentDto.AssignmentsResponse.builder()
                    .id(assignment.getId())
                    .status(assignment.getStatus()).build();

            if (result.containsKey(startTime))
                result.get(startTime).put(courseName, dto);
            else {
                Map<String, AssignmentDto.AssignmentsResponse> dtoMap = new WeakHashMap<>();
                dtoMap.put(courseName, dto);
                result.put(startTime, dtoMap);
            }
        }
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

    private Map<String, List<Object>> makeResponse(List<String> courseNameList, Map<String, Map<String, AssignmentDto.AssignmentsResponse>> dtoMap) {
        Map<String, List<Object>> response = new WeakHashMap<>();
        List<String> timeList = new ArrayList<>();

        for (String courseName: courseNameList) {
            response.put(courseName, new ArrayList<>());
        }

        dtoMap.forEach((time, courseInfo) -> {
            timeList.add(time);
            courseInfo.forEach((courseName, dto) -> {
                response.get(courseName).add(dto);
            });
        });

        response.put("courseList", Arrays.asList(courseNameList.toArray()));
        response.put("timeList", Arrays.asList(timeList.toArray()));
        return response;
    }
}
