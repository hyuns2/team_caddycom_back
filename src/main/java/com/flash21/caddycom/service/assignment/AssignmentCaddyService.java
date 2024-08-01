package com.flash21.caddycom.service.assignment;

import com.flash21.caddycom.dto.PagingResponse;
import com.flash21.caddycom.dto.assignment.AssignmentResponse;
import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import com.flash21.caddycom.entity.schedule.Schedule;
import com.flash21.caddycom.repository.caddy.HouseCaddyRepository;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import com.flash21.caddycom.repository.schedule.AssignmentQueryFactory;
import com.flash21.caddycom.repository.schedule.AssignmentRepository;
import com.flash21.caddycom.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AssignmentCaddyService {

    private final AssignmentRepository assignmentRepository;
    private final HouseCaddyRepository houseCaddyRepository;
    private final AssignmentQueryFactory assignmentQueryFactory;
    private final ScheduleRepository scheduleRepository;
    private final GolfFieldRepository golfFieldRepository;

    /**
     * 골프장 id와 date로 assignment를 모두 조회한다.
     */
    @Transactional(readOnly = true)
    public PagingResponse<AssignmentResponse.Info> getAssignments(Long golfFieldId, LocalDate date, Long courseId, AssignmentStatus status, int page) {
        Pageable pageable = PageRequest.of(page, 30);
        Page<Assignment> assignmentPage =
                assignmentQueryFactory.findAllByDateAndCourseIdAndStatus(pageable, golfFieldId, date, courseId, status);
        return PagingResponse.from(assignmentPage, AssignmentResponse.Info::from);
    }


    @Transactional(readOnly = true)
    public PagingResponse<AssignmentResponse.Info> getSwitchingCaddy(Long golfFieldId, LocalDate date, Long id, Long courseId, Integer part, int page) {
        Pageable pageable = PageRequest.of(page, 30);
        Page<Assignment> assignmentPage =
                assignmentQueryFactory.findAssignedByDateAndCourseIdAndPart(pageable, id, golfFieldId, date, courseId, part);
        return PagingResponse.from(assignmentPage, AssignmentResponse.Info::fromSwitchable);
    }


    // TODO: CANCELED, ASSIGNED 상태일때만 조회 가능하도록 예외처리 추가 필요
    @Transactional(readOnly = true)
    public AssignmentResponse.Detail getAssignmentDetail(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배정 정보가 없습니다."));
        return AssignmentResponse.Detail.from(assignment);
    }


    @Transactional
    public void cancelAssignment(Long assignmentId, String reason) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배정 정보가 없습니다."));
        assignment.cancel(reason);
    }


    @Transactional
    public void switchAssignment(Long from, Long to) {
        List<Assignment> assignments = assignmentRepository.findByIds(List.of(from, to));
        if (assignments.size() != 2)
            throw new IllegalArgumentException("해당 배정 정보가 없습니다.");
        // swap
        swapCaddy(assignments.get(0), assignments.get(1));
    }


    /**
     * sql batch 처리로 인해 one-to-one 관계의 엔티티를 변경 시 duplicate key 에러가 발생
     * -> 두 엔티티의 caddy를 null로 변경 후 다시 업데이트
     */
    private void swapCaddy(Assignment fromAssignment, Assignment toAssignment) {
        HouseCaddy fromCaddy = fromAssignment.getHouseCaddy();
        String fromCaddyName = fromAssignment.getCaddyName();
        HouseCaddy toCaddy = toAssignment.getHouseCaddy();
        String toCaddyName = toAssignment.getCaddyName();

        fromAssignment.vacateCaddy();
        toAssignment.vacateCaddy();

        assignmentRepository.switchAssignment(fromAssignment.getId(), toCaddy, toCaddyName);
        assignmentRepository.switchAssignment(toAssignment.getId(), fromCaddy, fromCaddyName);
    }


    @Transactional
    public void assignSelectedCaddy(Long assignmentId, Long caddyId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 배정 정보입니다."));
        HouseCaddy caddy = houseCaddyRepository.findById(caddyId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 캐디입니다."));

        assignment.assignCaddy(caddy);
    }

    /**
     * 캐디 자동 배정
     */
    @Transactional
    public void assignCaddyToSchedule(Long golfFieldId, LocalDate date) {

        //골프장 검증
        GolfField findGolfField = golfFieldRepository.findById(golfFieldId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 골프장입니다."));

        //스케줄 검증
        List<Schedule> findSchedules = scheduleRepository.findAllByGolfFieldIdAndReservationAtFetchJoin(golfFieldId, date);
        if (findSchedules.isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 스케줄입니다.");
        }

        //오늘의 요일 변환
        Days todaysDayOfWeek = getDays(date.plusDays(5));

        //배정 검증
        List<Assignment> findAssignments = getAllAssignmentsSortByTime(findSchedules);
        if (findAssignments.isEmpty()) {
            throw new NoSuchElementException("배정 정보가 존재하지 않습니다.");
        }

        //캐디 검증
        List<HouseCaddy> findCaddies = houseCaddyRepository.findAllByGolfFieldIdSortById(golfFieldId);
        if (findCaddies.isEmpty()) {
            throw new NoSuchElementException("캐디가 존재하지 않습니다.");
        }

        int caddySize = findCaddies.size();
        int currentCaddyIndex = 0;
        Long recentlyAssignedCaddyId = null;

        for (Assignment assignment : findAssignments) {
            boolean isAssigned = false;

            if (assignment.getStatus() == AssignmentStatus.BLOCKED) continue;

            while (!isAssigned) {

                HouseCaddy currentCaddy = findCaddies.get(currentCaddyIndex);
                if (verifyCaddy(assignment, currentCaddy, todaysDayOfWeek)) {
                    assignment.assignCaddy(currentCaddy);
                    System.out.println("배정된 캐디 이름: " + currentCaddy.getName() + " 캐디 ID: " + currentCaddy.getId() + " 배정 Id: " + assignment.getId());
                    isAssigned = true;
                    recentlyAssignedCaddyId = currentCaddy.getId();
                }

                currentCaddyIndex = (currentCaddyIndex + 1) % caddySize;
            }
        }

        findGolfField.changeCaddyAssignCursor(recentlyAssignedCaddyId);
    }

    private List<Assignment> getAllAssignmentsSortByTime(List<Schedule> findSchedules) {
        return findSchedules.stream()
                .flatMap(schedule -> schedule.getAssignments().stream())
                .sorted(Comparator.comparing(Assignment::getStartTime))
                .toList();
    }

    private boolean verifyCaddy(Assignment assignment, HouseCaddy currentCaddy, Days todaysDayOfWeek) {
        boolean isHoliday = currentCaddy.getHoliday() == null || !currentCaddy.getHoliday().contains(todaysDayOfWeek);
        boolean isOffPart = currentCaddy.getOffPart() == null || !currentCaddy.getOffPart().contains(assignment.getSchedule().getPart());

        return isHoliday && isOffPart;
    }


    private Days getDays(LocalDate date) {
        int dayOfWeek = getDayofWeekFromRequestDate(date);
        return Days.fromNumber(String.valueOf(dayOfWeek));
    }

    private int getDayofWeekFromRequestDate(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return switch (dayOfWeek) {
            case MONDAY -> 1;
            case TUESDAY -> 2;
            case WEDNESDAY -> 3;
            case THURSDAY -> 4;
            case FRIDAY -> 5;
            case SATURDAY -> 6;
            case SUNDAY -> 7;
        };
    }
}
