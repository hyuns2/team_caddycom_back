package com.flash21.caddycom.service.schedule;

import com.flash21.caddycom.dto.assignment.AssignmentResponse;
import com.flash21.caddycom.dto.schedule.ReservationSheetResponse;
import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.dto.schedule.ReservationSheetRequest;
import com.flash21.caddycom.entity.schedule.*;
import com.flash21.caddycom.global.exception.cException.*;
import com.flash21.caddycom.repository.assignment.AssignmentRepository;
import com.flash21.caddycom.repository.caddy.CaddyRepository;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
import com.flash21.caddycom.repository.schedule.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationSheetService {
    private final GolfFieldRepository golfFieldRepository;
    private final ReservationSheetRepository reservationSheetRepository;
    private final ScheduleRepository scheduleRepository;
    private final CourseRepository courseRepository;
    private final ScheduleJdbcRepository scheduleJdbcRepository;
    private final AssignmentRepository assignmentRepository;
    private final CaddyRepository caddyRepository;

    /**
     * 예약시트 생성: 예약시트를 생성합니다.
     *
     * @param requestDto 예약시트 생성요청 dto
     * @throws CCourseNotFoundException Course 객체가 존재하지 않을 경우
     */
    @Transactional
    public void createReservationSheet(ReservationSheetRequest.CreateOrUpdate requestDto) {

        GolfField golfField = golfFieldRepository.findById(requestDto.getGolfFieldId())
                .orElseThrow(CGolfFieldNotFoundException::new);

        List<Course> courseList = courseRepository.findAllById(requestDto.getCourseList());
        if (courseList.isEmpty()) {
            throw new CCourseNotFoundException();
        }

        validToCreateSchedules(requestDto);

        ReservationSheet reservationSheet = reservationSheetRepository.save(requestDto.toEntity(golfField));
        List<Schedule> scheduleList = createSchedules(golfField, courseList, reservationSheet, requestDto);

        scheduleJdbcRepository.saveAll(scheduleList);
    }

    /**
     * 스케쥴 생성 검증함수: 예약시트 생성요청 dto & 같은 날짜의 같은 코스 예약이 있는지 검증합니다.
     *
     * @param dto 예약시트 생성요청 dto
     */
    private void validToCreateSchedules(ReservationSheetRequest.CreateOrUpdate dto) {
        if (dto.getStartDate().isBefore(LocalDate.now()) || dto.getStartDate().isAfter(dto.getEndDate()))
            throw new CInvalidDateOrderException();

        if (dto.getTeeOffList().size() != dto.getStartTimeList().size() ||
                dto.getStartTimeList().size() != dto.getEndTimeList().size())
            throw new CInvalidPartInfoException();

        for (Long courseId : dto.getCourseList())
            if (scheduleRepository.findFirstByGolfFieldIdAndCourseIdAndReservationAtBetween(dto.getGolfFieldId(), courseId, dto.getStartDate(), dto.getEndDate())
                    .isPresent())
                throw new CBadReservationRequestException();
    }

    private List<Schedule> createSchedules(GolfField golfField, List<Course> courseList, ReservationSheet reservationSheet, ReservationSheetRequest.CreateOrUpdate dto) {

        List<Schedule> scheduleList = new ArrayList<>();
        List<LocalDate> localDateList = getLocalDateList(dto);

        for (int i = 0; i < dto.getTeeOffList().size(); i++) {
            LocalTime startTime = LocalTime.parse(dto.getStartTimeList().get(i));
            LocalTime endTime = LocalTime.parse(dto.getEndTimeList().get(i));
            String teeOff = dto.getTeeOffList().get(i);
            int part = i + 1;

            addSchedules(scheduleList, golfField, courseList, reservationSheet, localDateList, startTime, endTime, teeOff, part);
        }

        return scheduleList;
    }

    private void addSchedules(List<Schedule> scheduleList, GolfField golfField, List<Course> courseList, ReservationSheet reservationSheet, List<LocalDate> localDateList, LocalTime startTime, LocalTime endTime, String teeOff, int part) {

        int totalCnt = getStartTimeList(startTime, endTime, teeOff).size();

        for (Course course : courseList) {
            for (LocalDate oneDay : localDateList) {

                Schedule newSchedule = Schedule.builder()
                        .golfField(golfField)
                        .reservationSheet(reservationSheet)
                        .course(course)
                        .reservationAt(oneDay)
                        .startTime(startTime)
                        .endTime(endTime)
                        .teeOff(teeOff)
                        .part(part)
                        .dateStatus(DateStatus.NOTHING)
                        .totalCnt(totalCnt)
                        .notAssignedCnt(0)
                        .blockedCnt(0)
                        .build();

                scheduleList.add(newSchedule);
            }
        }
    }

    private List<LocalDate> getLocalDateList(ReservationSheetRequest.CreateOrUpdate dto) {
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        return startDate.datesUntil(endDate.plusDays(1)).toList();
    }

    /**
     * 주어진 조건 사이의 시간을 모두 찾아, 리스트로 반환합니다.
     *
     * @param startTime 시작시간
     * @param endTime   종료시간
     * @param teeOff    티오프 간격
     * @return 주어진 조건 사이의 시간 리스트
     */
    public List<LocalTime> getStartTimeList(LocalTime startTime, LocalTime endTime, String teeOff) {
        LocalTime currentStartTime = startTime;

        List<Integer> teeOffList = Arrays.stream(teeOff.split("~")).map(Integer::valueOf).toList();
        int teeOffListSize = teeOffList.size();
        int currentTeeOffIndex = 0;

        List<LocalTime> startTimeList = new ArrayList<>();
        while (currentStartTime.isBefore(endTime)) {
            startTimeList.add(currentStartTime);

            currentStartTime = currentStartTime.plusMinutes(teeOffList.get(currentTeeOffIndex++));
            if (currentTeeOffIndex >= teeOffListSize)
                currentTeeOffIndex = 0;
        }

        return startTimeList;
    }

    /**
     * 예약시트 전체조회: 해당하는 골프장의 모든 예약시트를 반환합니다.
     *
     * @param golfFieldId 골프장 Id
     * @return 예약시트별 dto 리스트
     */
    public List<ReservationSheetResponse.Get> getReservationSheet(Long golfFieldId) {
        List<ReservationSheetResponse.Get> dtoList = new ArrayList<>();

        List<ReservationSheet> reservationSheetList = reservationSheetRepository.findAllByGolfFieldId(golfFieldId);
        Map<Long, String> courseMap = courseRepository.findAllByGolfFieldId(golfFieldId)
                .stream().collect(Collectors.toMap(Course::getId, Course::getName));
        for (ReservationSheet rs : reservationSheetList) {
            int part = 1;
            List<ReservationSheetResponse.InfoByPart> detailDtoList = new ArrayList<>();
            while (true) {
                Optional<Schedule> schedule = scheduleRepository.findFirstByReservationSheetIdAndPart(rs.getId(), part++);
                if (schedule.isEmpty())
                    break;
                detailDtoList.add(ReservationSheetResponse.InfoByPart.from(schedule.get()));
            }
            dtoList.add(ReservationSheetResponse.Get.builder()
                    .id(rs.getId())
                    .courseList(rs.getCourseIdList().stream().map(current ->
                            ReservationSheetResponse.CourseInfo.builder()
                                    .id(current)
                                    .name(courseMap.get(current)).build()).toList())
                    .startDate(rs.getStartDate())
                    .endDate(rs.getEndDate())
                    .timeSlot(detailDtoList).build());
        }
        return dtoList.stream().sorted(new GetResponseComparator()).toList();
    }

    /**
     * 예약시트 수정: 해당하는 예약시트를 요청한 정보로 수정합니다.
     * 1. 요청된 예약시트의 모든 배정정보 상태가 NOTHING 또는 CANCELED인지 확인
     * 2. 기존 예약시트를 삭제한 후, 요청된 정보로 생성
     *
     * @param reservationSheetId 예약시트 Id
     * @param dto                요청한 정보
     */
    @Transactional
    public void updateReservationSheet(Long reservationSheetId, ReservationSheetRequest.CreateOrUpdate dto) {
        List<Schedule> scheduleList = scheduleRepository.findAllByReservationSheetId(reservationSheetId);
        if (assignmentRepository.findFirstByStatusIsInAndScheduleIsIn(
                Arrays.asList(AssignmentStatus.BLOCKED, AssignmentStatus.CANCEL_REQUESTED, AssignmentStatus.ASSIGNED), scheduleList
        ).isPresent())
            throw new CInvalidModifyingRequestException();

        deleteReservationSheet(reservationSheetId);
        createReservationSheet(dto);
    }

    /**
     * 예약시트 삭제: 해당하는 예약시트를 삭제합니다.
     * 1. 과거의 데이터는 보존
     * 2. 미래의 데이터는 캐디가 배정된 배정정보 제외 전체 삭제
     * 3. 오늘은 현재시간 기준으로 반영
     *
     * @param reservationSheetId 예약시트 Id
     */
    @Transactional
    public void deleteReservationSheet(Long reservationSheetId) {
        LocalDate today = LocalDate.now();
        LocalTime current = LocalTime.now();
        List<Schedule> scheduleList = scheduleRepository.findAllByReservationSheetIdAndReservationAtIsAfter(reservationSheetId, today);
        scheduleList.addAll(scheduleRepository.findAllByReservationSheetIdAndReservationAtAndStartTimeIsAfter(reservationSheetId, today, current));

        List<Long> targetAssignmentIdList = new ArrayList<>();
        List<Long> targetScheduleIdList = new ArrayList<>();
        for (Schedule schedule : scheduleList) {
            for (Assignment assignment : schedule.getAssignments()) {
                if (assignment.getCaddy() != null)
                    assignment.updateByDeletedSchedule();
                else
                    targetAssignmentIdList.add(assignment.getId());
            }
            targetScheduleIdList.add(schedule.getId());
        }
        assignmentRepository.deleteAllByIdList(targetAssignmentIdList);
        scheduleRepository.deleteAllByIdList(targetScheduleIdList);
        reservationSheetRepository.deleteById(reservationSheetId);
    }

    /**
     * 메타데이터 조회: 캘린더에 표기되는 메타데이터를 반환합니다.
     *
     * @param golfFieldId 조회할 골프장 Id
     * @param year        대상 연도
     * @param month       대상 월
     * @return 메타데이터 반환 dto 리스트
     */
    public List<ReservationSheetResponse.MetaData> getMetaData(Long golfFieldId, int year, int month) {
        LocalDate targetDate = LocalDate.of(year, month, 1);
        List<MetaDataReport> reports = scheduleRepository.countAllMetaDataByDate(targetDate, targetDate.plusMonths(1).minusDays(1), golfFieldId);

//        List<ReservationSheetResponse.MetaData> responseDtoList = new ArrayList<>();
//        for (MetaDataReport report : reports) {
//            responseDtoList.add(ReservationSheetResponse.MetaData.from(report));
//        }
//        return responseDtoList.stream().sorted(new MetaDataResponseComparator()).toList();


        List<ReservationSheetResponse.MetaData> responseDtoList = reports.stream()
                .map(ReservationSheetResponse.MetaData::from)
                .sorted(Comparator.comparing(ReservationSheetResponse.MetaData::getTargetDate))
                .toList();

        return responseDtoList;
    }

    public Map<LocalDate, List<AssignmentResponse.CaddyAssignmentInfo>> getAssignmentResultSheet(Long caddyId, int year, int month) {

        caddyRepository.findById(caddyId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 캐디 정보입니다."));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Assignment> findAssignments = assignmentRepository.findByCaddyIdAndMonth(caddyId, startDate, endDate);

        return findAssignments.stream()
                .map(AssignmentResponse.CaddyAssignmentInfo::new)
                .sorted(Comparator.comparing(AssignmentResponse.CaddyAssignmentInfo::getStartTime))
                .collect(
                        Collectors.groupingBy(
                                AssignmentResponse.CaddyAssignmentInfo::getDate,
                                LinkedHashMap::new,
                                Collectors.toList()
                        )
                );
    }

    /**
     * MetaDataResponse dto를 날짜 순으로 정렬하는 Comparator
     */
    private static class MetaDataResponseComparator implements Comparator<ReservationSheetResponse.MetaData> {
        @Override
        public int compare(ReservationSheetResponse.MetaData dto1, ReservationSheetResponse.MetaData dto2) {
            if (dto1.getTargetDate().isAfter(dto2.getTargetDate()))
                return 1;
            else if (dto1.getTargetDate().isBefore(dto2.getTargetDate()))
                return -1;
            else
                return 0;
        }
    }

    /**
     * GetResponse dto를 날짜 순으로 정렬하는 Comparator
     */
    private static class GetResponseComparator implements Comparator<ReservationSheetResponse.Get> {
        @Override
        public int compare(ReservationSheetResponse.Get dto1, ReservationSheetResponse.Get dto2) {
            if (dto1.getStartDate().isAfter(dto2.getStartDate()))
                return 1;
            else if (dto1.getStartDate().isBefore(dto2.getStartDate()))
                return -1;
            else
                return 0;
        }
    }
}