package com.flash21.caddycom.service.schedule;

import com.flash21.caddycom.dto.schedule.AssignmentDto;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import com.flash21.caddycom.entity.schedule.DateStatus;
import com.flash21.caddycom.entity.schedule.Schedule;
import com.flash21.caddycom.global.exception.cException.CReservationSheetNotFoundException;
import com.flash21.caddycom.repository.schedule.*;
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
@Transactional(readOnly = true)
public class AssignmentService {
    final ReservationSheetService rsService;
    final ScheduleRepository scheduleRepository;
    final AssignmentRepository assignmentRepository;
    final AssignmentJdbcRepository assignmentJdbcRepository;

    /**
     * 배정정보 조회 및 생성: 배정정보가 존재하는 경우에는 반환하고, 존재하지 않는 경우에는 생성하여 반환합니다.
     *
     * @param golfFieldId 골프장 Id
     * @param targetDate  대상 날짜
     * @param page        페이지 번호 (데이터 10개)
     * @return 코스리스트, 시간리스트, 부별 id-status 형태의 map 반환
     * @throws CReservationSheetNotFoundException ReservationSheet 객체가 존재하지 않을 경우
     */
    @Transactional
    public Map<String, List<Object>> getAssignments(Long golfFieldId, LocalDate targetDate, int page) {
        List<Schedule> scheduleList = scheduleRepository.findAllByGolfFieldIdAndReservationAt(golfFieldId, targetDate);
        Set<Course> courseSet = new HashSet<>();
        for (Schedule schedule : scheduleList) {
            courseSet.add(schedule.getCourse());

            if (schedule.getDateStatus() == DateStatus.NOTHING)
                createAssignments(schedule);
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
            for (String courseName : courseNameList) {
                if (!value.containsKey(courseName))
                    value.put(courseName, null);
            }
        });

        return result;
    }

    /**
     * 배정정보 조회 내부함수2: 한 페이지만큼의 시간을 추출하고, 이 예약시간을 가지는 코스 정보를 조회하여 반환합니다.
     *
     * @param result 예약시간과 예약시간을 가지는 코스 정보 형태의 map
     * @param date   대상 날짜
     * @param page   page 페이지 번호 (데이터 10개)
     */
    private void getResultFromRepo(Map<String, Map<String, AssignmentDto.AssignmentsResponse>> result, LocalDate date, int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<LocalTime> resultTimePage = assignmentRepository.findTimesByReservationAt(date, pageable);
        List<LocalTime> resultTimeList = resultTimePage.getContent();

        List<Assignment> assignmentList = assignmentRepository.findAllByReservationAtAndBetweenTime(date, resultTimeList.get(0), resultTimeList.get(resultTimeList.size() - 1));
        for (Assignment assignment : assignmentList) {
            String startTime = assignment.getStartTime().toString();
            String courseName = assignment.getSchedule().getCourse().getName();
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
     * 배정정보 조회 내부함수3: 배정정보를 생성합니다.
     *
     * @param schedule 스케쥴 객체
     */
    private void createAssignments(Schedule schedule) {
        assignmentJdbcRepository.saveAll(schedule.getId(), rsService.getStartTimeList(
                schedule.getStartTime(), schedule.getEndTime(), schedule.getTeeOff()));

        schedule.changeDateStatus(DateStatus.SETTING);
    }

    /**
     * 배정정보 조회 내부함수4: 요구되는 response 형식대로 생성 및 반환합니다.
     *
     * @param courseNameList 전체 코스이름 리스트
     * @param dtoMap         코스, dto 구조의 map
     * @return 요구되는 api response
     */
    private Map<String, List<Object>> makeResponse(List<String> courseNameList, Map<String, Map<String, AssignmentDto.AssignmentsResponse>> dtoMap) {
        Map<String, List<Object>> response = new WeakHashMap<>();
        List<String> timeList = new ArrayList<>();

        for (String courseName : courseNameList) {
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

    @Transactional
    public void setBlock(Long assignmentsId, AssignmentDto.BlockRequest blockRequest) {
        Assignment findAssignment = assignmentRepository.findById(assignmentsId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 배정 정보입니다."));

        findAssignment.blockAssignment(blockRequest.getReason());

    }

    @Transactional
    public void cancelBlock(Long assignmentsId) {
        Assignment findAssignment = assignmentRepository.findById(assignmentsId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 배정 정보입니다."));

        findAssignment.cancelBlock();

    }

    public AssignmentDto.BlockResponse getBlock(Long assignmentsId) {
        Assignment findAssignment = assignmentRepository.findById(assignmentsId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 배정 정보입니다."));

        if (findAssignment.getStatus() != AssignmentStatus.BLOCKED) {
            throw new IllegalStateException("블락상태가 아닌 배정 정보입니다.");
        }

        return new AssignmentDto.BlockResponse(findAssignment);
    }
}