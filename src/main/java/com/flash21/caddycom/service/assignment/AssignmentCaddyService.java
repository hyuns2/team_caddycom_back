package com.flash21.caddycom.service.assignment;

import com.flash21.caddycom.dto.PagingResponse;
import com.flash21.caddycom.dto.assignment.AssignmentRequest;
import com.flash21.caddycom.dto.assignment.AssignmentResponse;
import com.flash21.caddycom.dto.golfFieldDetail.course.CourseResponse;
import com.flash21.caddycom.entity.caddy.Caddy;
import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import com.flash21.caddycom.entity.schedule.DateStatus;
import com.flash21.caddycom.entity.schedule.Schedule;
import com.flash21.caddycom.repository.caddy.HouseCaddyRepository;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
import com.flash21.caddycom.repository.assignment.AssignmentRepository;
import com.flash21.caddycom.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import static com.flash21.caddycom.entity.schedule.AssignmentStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentCaddyService {

    private final AssignmentRepository assignmentRepository;
    private final HouseCaddyRepository houseCaddyRepository;
    private final GolfFieldRepository golfFieldRepository;
    private final ScheduleRepository scheduleRepository;
    private final CourseRepository courseRepository;

    /**
     * 골프장 id와 date로 assignment를 모두 조회한다.
     */
    public PagingResponse<AssignmentResponse.Info> getAssignments(Long golfFieldId, LocalDate date, Long courseId, AssignmentStatus status, int page) {
        Pageable pageable = PageRequest.of(page, 30);
        Page<Assignment> assignmentPage =
                assignmentRepository.findAllByDateAndCourseIdAndStatus(pageable, golfFieldId, date, courseId, status);
        return PagingResponse.from(assignmentPage, AssignmentResponse.Info::from);
    }

    public PagingResponse<AssignmentResponse.Info> getSwitchingCaddy(Long golfFieldId, LocalDate date, Long id, Long courseId, Integer part, int page) {
        Pageable pageable = PageRequest.of(page, 30);
        Page<Assignment> assignmentPage =
                assignmentRepository.findAssignedByDateAndCourseIdAndPart(pageable, id, golfFieldId, date, courseId, part);
        return PagingResponse.from(assignmentPage, AssignmentResponse.Info::fromSwitchable);
    }


    public AssignmentResponse.Detail getAssignmentDetail(Long assignmentId) {
        Assignment assignment = assignmentRepository.findByIdWithFetchJoin(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배정 정보가 없습니다."));

        if (assignment.getStatus() != CANCELED && assignment.getStatus() != ASSIGNED) {
            throw new IllegalArgumentException("배정되거나 취소된 상태에서만 조회 가능합니다.");
        }
        return AssignmentResponse.Detail.from(assignment);
    }


    @Transactional
    public void cancelAssignment(Long assignmentId, String reason) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배정 정보가 없습니다."));
        assignment.cancel(reason);
    }


    @Transactional
    public void requestCancelAssignment(Long assignmentId, AssignmentRequest.Cancel request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배정 정보가 없습니다."));
        assignment.requestCancel(request.getReason());
        assignment.getSchedule().addNotAssignedCount();
    }


    @Transactional
    public void switchAssignment(Long from, Long to) {
        List<Assignment> assignments = assignmentRepository.findByIds(List.of(from, to));
        if (assignments.size() != 2)
            throw new IllegalArgumentException("해당 배정 정보가 없습니다.");

        if (assignments.stream().anyMatch(assignment -> assignment.getStatus() != ASSIGNED))
            throw new IllegalArgumentException("이미 취소되거나 블락된 배정입니다. 변경이 불가능합니다.");
        // swap
        Caddy fromCaddy = assignments.get(0).getCaddy();
        Caddy toCaddy = assignments.get(1).getCaddy();
        assignments.get(0).assignCaddy(toCaddy);
        assignments.get(1).assignCaddy(fromCaddy);
    }


    @Transactional
    public void assignSelectedCaddy(Long assignmentId, Long caddyId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 배정 정보입니다."));
        HouseCaddy caddy = houseCaddyRepository.findById(caddyId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 캐디입니다."));

        assignment.assignCaddy(caddy);
    }

    public List<AssignmentResponse.CaddyAssignmentInfoDetail> getAssignmentInfo(List<Long> assignmentIds) {
        return assignmentRepository.findByIdsFetchJoinOrderByStartTime(assignmentIds).stream()
                .map(AssignmentResponse.CaddyAssignmentInfoDetail::from)
                .toList();
    }

    /**
     * 캐디 자동 배정
     */
    @Transactional
    public void assignCaddyAutomatically(Long golfFieldId, LocalDate date) {

        //골프장 검증
        GolfField findGolfField = golfFieldRepository.findById(golfFieldId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 골프장입니다."));

        //스케줄 검증
        List<Schedule> findSchedules = scheduleRepository.findAllByDateFetchJoinToAssignmentAndHouseCaddy(golfFieldId, date);

        boolean invalidScheduleExists = findSchedules.stream()
                .anyMatch(findSchedule -> findSchedule.getDateStatus() != DateStatus.SETTING);
        if (findSchedules.isEmpty() || invalidScheduleExists) {
            throw new NoSuchElementException("존재하지 않는 스케줄입니다.");
        }

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

        //블락된 캐디 확인용 Set
        Set<Long> blockedCaddyIds = getBlockedHouseCaddies(findAssignments);
        //오늘의 요일 변환
        Days todaysDayOfWeek = getDays(date);

        int caddySize = findCaddies.size();
        int currentCaddyIndex = getStartIndex(findGolfField, caddySize, findCaddies);

        for (Assignment assignment : findAssignments) {
            boolean isAssigned = false;

            AssignmentStatus status = assignment.getStatus();
            if (status == BLOCKED || status == ASSIGNED) continue;

            while (!isAssigned) {

                HouseCaddy currentCaddy = findCaddies.get(currentCaddyIndex);
                if (isAlreadyAssigned(blockedCaddyIds, currentCaddy) && isAvailable(assignment, currentCaddy, todaysDayOfWeek)) {
                    assignment.assignCaddy(currentCaddy);
                    isAssigned = true;
                }

                currentCaddyIndex = (currentCaddyIndex + 1) % caddySize;
            }
        }

        //다음에 맨 처음으로 배정되어야 할 캐디의 ID를 Cursor로 세팅
        findGolfField.changeCaddyAssignCursor(findCaddies.get(currentCaddyIndex).getId());
        findSchedules.forEach(fs -> fs.changeDateStatus(DateStatus.ASSIGNED));
    }

    /**
     * 캐디 업무 시작 시 시작 설정, 보여줄 코스 상세 정보 조회
     */
    @Transactional
    public CourseResponse.DetailMap startAssignment(Long courseId, Long assignmentId, LocalTime startedTime) {

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 배정 정보입니다."));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 코스입니다."));

        if (assignment.getEndedTime() != null &&
                assignment.getStatus() != ASSIGNED &&
                assignment.getStatus() != BLOCKED) {
            throw new IllegalStateException("업무가 끝난 상태이거나 배정되지 않은 상태입니다.");
        }

        assignment.start(startedTime);

        return CourseResponse.DetailMap.from(course);
    }

    /**
     * 캐디 업무 종료 시 종료 설정
     */
    @Transactional
    public void terminateAssignment(Long assignmentId, LocalTime endedTime) {

        Assignment findAssignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 배정 정보입니다."));

        if (findAssignment.getStartedTime() == null &&
                findAssignment.getStatus() != ASSIGNED &&
                findAssignment.getStatus() != BLOCKED) {
            throw new IllegalStateException("업무가 시작하지 않은 상태이거나 배정되지 않은 상태입니다.");
        }

        findAssignment.finish(endedTime);
    }

    private int getStartIndex(GolfField findGolfField, int caddySize, List<HouseCaddy> findCaddies) {
        Long cursor = findGolfField.getCaddyAssignCursor();
        if (cursor != null) {
            for (int i = 0; i < caddySize; i++) {
                Long caddyId = findCaddies.get(i).getId();
                if (caddyId.equals(cursor) || caddyId > cursor) {
                    return i;
                }
            }
        }
        return 0;
    }

    private boolean isAlreadyAssigned(Set<Long> blockedCaddyIds, HouseCaddy currentCaddy) {
        return !blockedCaddyIds.contains(currentCaddy.getId());
    }

    private List<Assignment> getAllAssignmentsSortByTime(List<Schedule> findSchedules) {
        return findSchedules.stream()
                .flatMap(schedule -> schedule.getAssignments().stream())
                .filter(assignment -> assignment.getStatus() == NOTHING || assignment.getStatus() == BLOCKED)
                .sorted(Comparator.comparing(Assignment::getStartTime))
                .collect(Collectors.toList());
    }

    private Set<Long> getBlockedHouseCaddies(List<Assignment> findAssignment) {
        return findAssignment.stream()
                .filter(assignment -> assignment.getStatus() == BLOCKED && assignment.getCaddy() != null)
                .map(Assignment::getCaddy)
                .map(Caddy::getId)
                .collect(Collectors.toSet());
    }

    private boolean isAvailable(Assignment assignment, HouseCaddy currentCaddy, Days todaysDayOfWeek) {
        boolean isWorkDay = currentCaddy.getHoliday() == null || !currentCaddy.getHoliday().contains(todaysDayOfWeek);
        boolean isOffPart = currentCaddy.getOffPart() == null || !currentCaddy.getOffPart().contains(assignment.getSchedule().getPart());
        return isWorkDay && isOffPart;
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
