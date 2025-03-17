package com.flash21.caddycom.service.schedule;

import com.flash21.caddycom.dto.schedule.ScheduleResponse;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.schedule.*;
import com.flash21.caddycom.repository.assignment.AssignmentRepository;
import com.flash21.caddycom.repository.caddy.CaddyRepository;
import com.flash21.caddycom.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final AssignmentRepository assignmentRepository;
    private final CaddyRepository caddyRepository;

    /**
     * 예약 시트에 맞게 schedules를 생성합니다.
     */
    @Transactional
    public void createSchedules(ReservationSheet reservationSheet, List<Course> courses,
                                List<LocalTime> startTimes, List<LocalTime> endTimes, List<String> teeOffs) {
        List<Schedule> schedules = new ArrayList<>();
        List<LocalDate> localDates = reservationSheet.getStartDate()
                .datesUntil(reservationSheet.getEndDate().plusDays(1)).toList();

        for (int i = 0; i < teeOffs.size(); i++) {
            int part = i + 1;
            LocalTime startTime = startTimes.get(i);
            LocalTime endTime = endTimes.get(i);
            String teeOff = teeOffs.get(i);
            int totalCnt = getStartTimes(startTime, endTime, teeOff).size();

            courses.forEach(course -> {
                localDates.forEach(localDate -> {
                    schedules.add(Schedule.of(reservationSheet.getGolfField(), reservationSheet, course,
                            localDate, startTime, endTime, teeOff, part, totalCnt));
                });
            });
        }

        scheduleRepository.bulkInsert(schedules);
    }

    //teeOff 간격으로 startTime과 endTime 사이의 모든 시간을 리스트로 반환합니다.
    public List<LocalTime> getStartTimes(LocalTime startTime, LocalTime endTime, String teeOff) {
        List<LocalTime> startTimes = new ArrayList<>();
        LocalTime currentTime = startTime;

        List<Integer> teeOffs = Arrays.stream(teeOff.split("~"))
                .map(Integer::valueOf).toList();
        int teeOffsIndex = 0;

        while (currentTime.isBefore(endTime)) {
            startTimes.add(currentTime);
            currentTime = currentTime.plusMinutes(
                    teeOffs.get(teeOffsIndex++ % teeOffs.size()));
        }

        return startTimes;
    }

    /**
     * 메타데이터 조회: 골프장 관리자의 캘린더에 표기되는 메타데이터를 반환합니다.
     */
    public List<ScheduleResponse.MetaData> getMetaData(Long golfFieldId, int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1);

        return scheduleRepository.findAllMetaDataByMonth(date, date.plusMonths(1).minusDays(1), golfFieldId).stream()
                .map(ScheduleResponse.MetaData::from).toList();
    }

    /**
     * 배정정보 조회 및 생성: 배정정보가 존재하는 경우에는 반환하고, 존재하지 않는 경우에는 생성하여 반환합니다.
     *
     * @return 코스리스트, 시간리스트, 코스별 id-status 형식의 배정정보 반환
     *         ex) { 코스: [A, B],
     *             시간: [~~~],
     *             A: [ {id&상태}, null, ~~ ],
     *             B: [~~~] }
     */
    @Transactional
    public Map<String, List<Object>> getAssignments(Long golfFieldId, LocalDate date) {
        List<Schedule> schedules = scheduleRepository.findAllWithEntitiesByReservationAtAndGolfFieldId(date, golfFieldId);
        if (schedules.get(0).getAssignments().isEmpty()) {
            createAssignments(schedules);
            schedules = scheduleRepository.findAllWithEntitiesBySchedules(schedules);
        }

        Set<String> courseNames = new HashSet<>();
        Set<LocalTime> times = new HashSet<>();
        Map<String, List<Assignment>> courseNameAndAssignments = new HashMap<>();
        makeInfoForResponse(schedules, courseNames, times, courseNameAndAssignments);

        return makeResponse(courseNames, times, courseNameAndAssignments);
    }

    // 배정정보를 생성합니다.
    private void createAssignments(List<Schedule> schedules) {
        List<Assignment> assignments = new ArrayList<>();

        schedules.forEach(schedule -> {
            List<LocalTime> startTimes = getStartTimes(schedule.getStartTime(), schedule.getEndTime(), schedule.getTeeOff());
            startTimes.forEach(startTime -> {
                assignments.add(Assignment.of(schedule, startTime));
            });
        });

        assignmentRepository.bulkInsert(assignments);
        scheduleRepository.updateDateStatusBySchedules(DateStatus.SETTING, schedules);
    }

    // 코스리스트, 시간리스트, 코스별 배정정보로 분류합니다.
    private void makeInfoForResponse(List<Schedule> schedules, Set<String> courseNames, Set<LocalTime> times, Map<String, List<Assignment>> courseNameAndAssignments) {
        for (Schedule schedule: schedules) {
            courseNames.add(schedule.getCourse().getName());
            times.addAll(schedule.getAssignments().stream()
                    .map(Assignment::getStartTime).toList());
        }

        courseNames.forEach(courseName -> {
            courseNameAndAssignments.put(courseName, schedules.stream()
                    .filter(schedule -> schedule.getCourse().getName().equals(courseName))
                    .map(Schedule::getAssignments)
                    .flatMap(List::stream)
                    .sorted(Comparator.comparing(Assignment::getStartTime))
                    .toList());
        });
    }

    // 코스별 배정정보에서 시간리스트에 없는 시간대를 null로 채우고, response를 규격에 맞게 생성합니다.
    private Map<String, List<Object>> makeResponse(Set<String> courseNames, Set<LocalTime> times, Map<String, List<Assignment>> courseNameAndAssignments) {
        Map<String, List<Object>> result = new HashMap<>();
        result.put("courses", Arrays.asList(courseNames.stream().sorted().toArray()));
        result.put("times", Arrays.asList(times.stream().sorted().toArray()));

        courseNameAndAssignments.forEach((key, value) -> {
            Map<LocalTime, Assignment> timeAndAssignments = value.stream()
                    .collect(Collectors.toMap(Assignment::getStartTime, assignment -> assignment));
            result.put(key, Arrays.asList(times.stream()
                    .sorted()
                    .map(time -> ScheduleResponse.AssignmentInfo.from(timeAndAssignments.getOrDefault(time, null))).toArray()));
        });

        return result;
    }

    /**
     * 캐디의 캘린더에 표시되는 정보 조회: 한달 간 해당 캐디에 배정된 정보를 일별로 반환한다.
     */
    public Map<LocalDate, List<ScheduleResponse.CaddyAssignmentInfo>> getCaddyAssignments(Long caddyId, int year, int month) {
        caddyRepository.findById(caddyId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 캐디 정보입니다."));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Assignment> findAssignments = assignmentRepository.findByCaddyIdAndMonth(caddyId, startDate, endDate);

        return findAssignments.stream()
                .map(ScheduleResponse.CaddyAssignmentInfo::new)
                .sorted(Comparator.comparing(ScheduleResponse.CaddyAssignmentInfo::getStartTime))
                .collect(
                        Collectors.groupingBy(
                                ScheduleResponse.CaddyAssignmentInfo::getDate,
                                LinkedHashMap::new,
                                Collectors.toList()
                        )
                );
    }
}
