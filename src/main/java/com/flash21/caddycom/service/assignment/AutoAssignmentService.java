package com.flash21.caddycom.service.assignment;

import com.flash21.caddycom.entity.caddy.Caddy;
import com.flash21.caddycom.entity.caddy.Days;
import com.flash21.caddycom.entity.caddy.HouseCaddy;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.schedule.Assignment;
import com.flash21.caddycom.entity.schedule.AssignmentStatus;
import com.flash21.caddycom.entity.schedule.DateStatus;
import com.flash21.caddycom.entity.schedule.Schedule;
import com.flash21.caddycom.repository.caddy.HouseCaddyRepository;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import com.flash21.caddycom.repository.schedule.ScheduleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import static com.flash21.caddycom.entity.schedule.AssignmentStatus.*;

/**
 * 특정 날짜에 대한 하우스 캐디의 자동 배정
 */
@Service
@RequiredArgsConstructor
public class AutoAssignmentService {
    private final GolfFieldRepository golfFieldRepository;
    private final ScheduleRepository scheduleRepository;
    private final HouseCaddyRepository houseCaddyRepository;

    /**
     * 캐디 자동 배정: date 에 해당하는 모든 배정에 골프장에 등록된 하우스캐디로 자동 배정한다.
     *
     * 1. 배정할 대상들이 존재하는지 유효성검사를 한다.
     * 2. 배정 리스트에 하우스캐디를 차례로 배정한다.
     *    * 하우스캐디는 배정가능한 상태여야 한다. (쉬는 타임이면 안되고, 이미 배정된 캐디도 안된다.)
     * 3. 몇번 캐디까지 배정이 끝났는지를 golfField의 caddyAssignCursor에 저장한다.
     * 4. 배정이 완료된 스케줄들의 상태를 ASSIGNED 로 변경한다.
     */
    @Transactional
    public void assignCaddyAutomatically(Long golfFieldId, LocalDate date) {
        GolfField golfField = golfFieldRepository.getGolfFieldById(golfFieldId); //골프장 검증
        List<Schedule> schedules = validateSchedules(golfFieldId, date); //스케줄 검증
        List<Assignment> assignments = validateAssignments(schedules); //배정 검증
        List<HouseCaddy> caddies = validateCaddies(golfFieldId); //캐디 검증

        //블락된 캐디 Set
        Set<Long> blockedCaddyIds = getBlockedHouseCaddies(assignments);
        //오늘의 요일 변환(휴무일 제외를 위함)
        Days todaysDayOfWeek = Days.fromNumber(date.getDayOfWeek().getValue());

        int caddySize = caddies.size();
        int currentCaddyIndex = getStartIndex(golfField.getCaddyAssignCursor(), caddySize, caddies);

        for (Assignment assignment : assignments) {
            AssignmentStatus status = assignment.getStatus();
            if (status == BLOCKED || status == ASSIGNED) continue; // 이미 배정되거나 블락된 assignment 는 건너뛴다.
            Integer part = assignment.getSchedule().getPart();

            boolean isAssigned = false;
            while (!isAssigned) {
                HouseCaddy currentCaddy = caddies.get(currentCaddyIndex);

                if (isCaddyAvailable(part, currentCaddy, todaysDayOfWeek, blockedCaddyIds)) {
                    assignment.assignCaddy(currentCaddy);
                    isAssigned = true;
                }
                currentCaddyIndex = (currentCaddyIndex + 1) % caddySize;
            }
        }

        //다음에 맨 처음으로 배정되어야 할 캐디의 ID를 Cursor로 세팅
        golfField.changeCaddyAssignCursor(caddies.get(currentCaddyIndex).getId());
        //배정 완료된 스케줄 상태 ASSIGNED 로 변경
        schedules.forEach(fs -> fs.changeDateStatus(DateStatus.ASSIGNED));
    }

    @NonNull
    private List<HouseCaddy> validateCaddies(Long golfFieldId) {
        List<HouseCaddy> caddies = houseCaddyRepository.findAllByGolfFieldIdSortById(golfFieldId);
        if (caddies.isEmpty()) {
            throw new NoSuchElementException("캐디가 존재하지 않습니다.");
        }
        return caddies;
    }

    /**
     * 배정이 가능한 assignment를 시간순으로 반환한다.
     * 배정이 가능한 assignment 상태: NOTHING, BLOCKED
     */
    @NonNull
    private List<Assignment> validateAssignments(List<Schedule> schedules) {
        List<Assignment> assignments = schedules.stream()
                .flatMap(schedule -> schedule.getAssignments().stream())
                .filter(assignment -> assignment.getStatus() == NOTHING || assignment.getStatus() == BLOCKED)
                .sorted(Comparator.comparing(Assignment::getStartTime))
                .collect(Collectors.toList());
        if (assignments.isEmpty()) {
            throw new NoSuchElementException("배정 정보가 존재하지 않습니다.");
        }
        return assignments;
    }

    /**
     * 배정이 가능한 schedule 상태 : SETTING
     * schedule이 없거나 배정이 불가능한 schedule이 하나라도 존재하면 예외를 발생시킨다.
     */
    @NonNull
    private List<Schedule> validateSchedules(Long golfFieldId, LocalDate date) {
        List<Schedule> schedules = scheduleRepository.findAllByDateFetchJoinToAssignmentAndHouseCaddy(golfFieldId, date);

        boolean invalidScheduleExists = schedules.stream()
                .anyMatch(findSchedule -> findSchedule.getDateStatus() != DateStatus.SETTING);
        if (schedules.isEmpty() || invalidScheduleExists) {
            throw new NoSuchElementException("존재하지 않는 스케줄입니다.");
        }
        return schedules;
    }

    /**
     * Cursor를 기준으로 배정을 시작할 첫번째 캐디의 인덱스를 반환한다.
     */
    private int getStartIndex(Long cursor, int caddySize, List<HouseCaddy> findCaddies) {
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



    /**
     * 배정이 불가능한 캐디의 ID Set 으로 반환
     * 이미 블락에 선택배정된 캐디는 자동배정에서 제외한다.
     */
    private Set<Long> getBlockedHouseCaddies(List<Assignment> findAssignment) {
        return findAssignment.stream()
                .filter(assignment -> assignment.getStatus() == BLOCKED && assignment.getCaddy() != null)
                .map(Assignment::getCaddy)
                .map(Caddy::getId)
                .collect(Collectors.toSet());
    }


    /**
     * 배정 part가 캐디가 일하는 part인지,
     * 배정 요일이 캐디가 일하는 요일인지,
     * 이미 배정된(블락시 선택배정) 캐디가 아닌지를 확인한다.
     */
    private boolean isCaddyAvailable(Integer part, HouseCaddy caddy, Days today, Set<Long> blockedCaddyIds) {
        return caddy.getOffPart() == null || !caddy.getOffPart().contains(part)
                && (caddy.getHoliday() == null || !caddy.getHoliday().contains(today))
                && !blockedCaddyIds.contains(caddy.getId());
    }
}
