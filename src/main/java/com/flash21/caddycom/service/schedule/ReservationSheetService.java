package com.flash21.caddycom.service.schedule;

import com.flash21.caddycom.dto.schedule.ReservationSheetResponse;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.dto.schedule.ReservationSheetRequest;
import com.flash21.caddycom.entity.schedule.*;
import com.flash21.caddycom.global.exception.CustomException;
import com.flash21.caddycom.global.exception.ErrorCode;
import com.flash21.caddycom.repository.assignment.AssignmentRepository;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
import com.flash21.caddycom.repository.schedule.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationSheetService {
    private final ScheduleService scheduleService;
    private final GolfFieldRepository golfFieldRepository;
    private final ReservationSheetRepository reservationSheetRepository;
    private final ScheduleRepository scheduleRepository;
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;

    /**
     * 예약시트 생성: 예약시트를 생성합니다.
     * 1. 예약시트부터 생성
     * 2. 예약시트의 기간만큼 일별로 코스마다 스케줄을 생성
     */
    @Transactional
    public Long createReservationSheet(ReservationSheetRequest.CreateOrUpdate dto) {
        GolfField golfField = golfFieldRepository.findById(dto.getGolfFieldId())
                .orElseThrow(() -> new CustomException(ErrorCode.GOLF_FIELD_NOT_FOUND));
        List<Course> courses = courseRepository.findAllById(dto.getCourseIds());

        validToCreateOrUpdateReservationSheet(dto, golfField, courses);

        ReservationSheet reservationSheet = reservationSheetRepository.save(dto.toEntity(golfField));
        scheduleService.createSchedules(reservationSheet, courses,
                dto.getStartTimes(), dto.getEndTimes(), dto.getTeeOffs());

        return reservationSheet.getId();
    }

    // 예약시트 생성 및 수정 요청 dto & 같은 날짜의 같은 코스 예약이 있는지 검증합니다.
    private void validToCreateOrUpdateReservationSheet(ReservationSheetRequest.CreateOrUpdate dto, GolfField golfField, List<Course> courses) {
        if (dto.getCourseIds().size() != courses.size())
            throw new CustomException(ErrorCode.COURSE_NOT_FOUND);

        if (dto.getStartDate().isBefore(LocalDate.now()) || dto.getStartDate().isAfter(dto.getEndDate()))
            throw new CustomException(ErrorCode.INVALID_DATE_ORDER);

        if (dto.getTeeOffs().size() != dto.getStartTimes().size() ||
                dto.getStartTimes().size() != dto.getEndTimes().size())
            throw new CustomException(ErrorCode.INVALID_PART_INFO);

        if (scheduleRepository.findFirstByGolfFieldAndCourseIsInAndReservationAtBetween(golfField, courses, dto.getStartDate(), dto.getEndDate())
                    .isPresent())
            throw new CustomException(ErrorCode.BAD_RESERVATION_REQUEST);
    }

    /**
     * 예약시트 전체조회: 해당하는 골프장의 모든 예약시트를 반환합니다.
     */
    public List<ReservationSheetResponse.Get> getReservationSheet(Long golfFieldId) {
        List<ReservationSheet> reservationSheets = reservationSheetRepository.findAllByGolfFieldId(golfFieldId);
        Map<Long, String> courseMap = courseRepository.findAllByGolfFieldId(golfFieldId)
                .stream().collect(Collectors.toMap(Course::getId, Course::getName));

        List<ReservationSheetResponse.Get> dtos = new ArrayList<>();
        reservationSheets.forEach(reservationSheet ->
                dtos.add(ReservationSheetResponse.Get.from(reservationSheet, courseMap.entrySet().stream()
                        .filter(entry -> reservationSheet.getCourseIds().contains(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))));

        return dtos.stream().sorted(Comparator.comparing(ReservationSheetResponse.Get::getStartDate)).toList();
    }

    /**
     * 예약시트 수정: 해당하는 예약시트를 요청한 정보로 수정합니다.
     * 1. 요청된 예약시트의 모든 배정정보 상태가 NOTHING 또는 CANCELED 인지 확인
     * 2. 관련 스케쥴 및 배정정보를 모두 삭제하고, 예약시트 정보를 업데이트
     */
    @Transactional
    public void updateReservationSheet(Long reservationSheetId, ReservationSheetRequest.CreateOrUpdate dto) {
        ReservationSheet reservationSheet = reservationSheetRepository.findWithEntitiesById(reservationSheetId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_SHEET_NOT_FOUND));
        validateToUpdateReservationSheet(dto, reservationSheet);

        assignmentRepository.deleteAllBySchedules(reservationSheet.getSchedules());
        scheduleRepository.deleteAllByReservationSheet(reservationSheet);
        reservationSheet.getSchedules().clear();

        List<Course> courses = courseRepository.findAllById(dto.getCourseIds());
        validToCreateOrUpdateReservationSheet(dto, reservationSheet.getGolfField(), courses);

        reservationSheet.updateAll(reservationSheet.getGolfField(), dto.getCourseIds(), dto.getStartDate(), dto.getEndDate(),
                dto.getStartTimes(), dto.getEndTimes(), dto.getTeeOffs());
        scheduleService.createSchedules(reservationSheet, courses,
                dto.getStartTimes(), dto.getEndTimes(), dto.getTeeOffs());
    }

    // 수정 요청 dto의 골프장과 요청된 예약시트의 골프장이 일치하는지 & 요청된 예약시트의 모든 배정정보 상태가 NOTHING 또는 CANCELED 인지 확인합니다.
    private void validateToUpdateReservationSheet(ReservationSheetRequest.CreateOrUpdate dto, ReservationSheet reservationSheet) {
        if (!dto.getGolfFieldId().equals(reservationSheet.getGolfField().getId()))
            throw new CustomException(ErrorCode.NOT_MATCHED_GOLF_FIELD);

        if (assignmentRepository.findFirstByAssignmentStatusIsInAndScheduleIsIn(
                Arrays.asList(AssignmentStatus.BLOCKED, AssignmentStatus.CANCEL_REQUESTED, AssignmentStatus.ASSIGNED), reservationSheet.getSchedules())
                .isPresent())
            throw new CustomException(ErrorCode.INVALID_MODIFYING_REQUEST);
    }

    /**
     * 예약시트 삭제: 해당하는 예약시트를 삭제합니다.
     * 1. 캐디가 배정된 배정정보는 보관하기 위해 연관관계 끊기
     * 2. 예약시트와 관련 스케쥴, 배정정보 모두 삭제
     */
    @Transactional
    public void deleteReservationSheet(Long reservationSheetId) {
        ReservationSheet reservationSheet = reservationSheetRepository.findWithSchedulesById(reservationSheetId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_SHEET_NOT_FOUND));

        assignmentRepository.updateScheduleToNullByAssignments(
                assignmentRepository.findAllByScheduleIsInAndAssignmentStatusIsIn(
                        reservationSheet.getSchedules(),
                        Arrays.asList(AssignmentStatus.ASSIGNED, AssignmentStatus.CANCEL_REQUESTED, AssignmentStatus.ASSIGN_REQUESTED))
        );

        reservationSheet = reservationSheetRepository.findWithSchedulesById(reservationSheetId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_SHEET_NOT_FOUND));
        assignmentRepository.deleteAllBySchedules(reservationSheet.getSchedules());
        scheduleRepository.deleteAllByReservationSheet(reservationSheet);
        reservationSheetRepository.deleteReservationSheetById(reservationSheet.getId());
    }
}