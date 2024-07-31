package com.flash21.caddycom.service.reservationSheet;

import com.flash21.caddycom.entity.golfField.GolfField;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.dto.reservationSheet.ReservationSheetDto;
import com.flash21.caddycom.entity.reservationSheet.ReservationDate;
import com.flash21.caddycom.entity.reservationSheet.ReservationSheet;
import com.flash21.caddycom.entity.schedule.DateStatus;
import com.flash21.caddycom.entity.schedule.Schedule;
import com.flash21.caddycom.global.exception.cException.CBadReservationRequestException;
import com.flash21.caddycom.global.exception.cException.CCourseNotFoundException;
import com.flash21.caddycom.global.exception.cException.CGolfFieldNotFoundException;
import com.flash21.caddycom.global.exception.cException.CInvalidPartInfoException;
import com.flash21.caddycom.repository.golfField.GolfFieldRepository;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
import com.flash21.caddycom.repository.reservationSheet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationSheetService {
    final GolfFieldRepository golfFieldRepository;
    final ScheduleRepository scheduleRepository;
    final CourseRepository courseRepository;
    final ScheduleJdbcRepository scheduleJdbcRepository;

    /**
     * 예약시트 생성: 예약시트를 생성합니다.
     *
     * @param dto 예약시트 생성요청 dto
     *
     * @throws CCourseNotFoundException Course 객체가 존재하지 않을 경우
     */
    @Transactional
    public void createReservationSheet(ReservationSheetDto.CreateRequest dto) {
        GolfField golfField = golfFieldRepository.findById(dto.getGolfFieldId())
                .orElseThrow(CGolfFieldNotFoundException::new);
        List<Course> courseList = courseRepository.findAllById(dto.getCourseList());
        if (courseList.isEmpty())
            throw new CCourseNotFoundException();

        validToCreateSchedules(dto, courseList);

        List<Schedule> scheduleList = new ArrayList<>();
        for (int i = 0; i < dto.getTeeOffList().size(); i++) {
            createSchedulesByPart(scheduleList, golfField, courseList, dto.getStartDate(), dto.getEndDate(),
                    LocalTime.parse(dto.getStartTimeList().get(i)), LocalTime.parse(dto.getEndTimeList().get(i)), dto.getTeeOffList().get(i), i+1);
        }
        scheduleJdbcRepository.saveAll(scheduleList);
    }

    /**
     * 스케쥴 생성 검증함수: 예약시트 생성요청 dto & 같은 날짜의 같은 코스 예약이 있는지 검증합니다.
     *
     * @param dto 예약시트 생성요청 dto
     * @param courseList 요청에 속한 코스리스트
     *
     * @throws CInvalidPartInfoException 부(파트)에 대한 일부 정보가 빠진 경우
     */
    private void validToCreateSchedules(ReservationSheetDto.CreateRequest dto, List<Course> courseList) {
        if (dto.getTeeOffList().size() != dto.getStartTimeList().size() ||
                dto.getStartTimeList().size() != dto.getEndTimeList().size())
            throw new CInvalidPartInfoException();

        for (Course course: courseList) {
            if (!scheduleRepository.findAllByGolfFieldIdAndCourseIdBetweenNewDate(dto.getGolfFieldId(), course.getId(), dto.getStartDate(), dto.getEndDate()).isEmpty())
                throw new CBadReservationRequestException();
        }
    }

    /**
     * 스케쥴 생성함수: 일별, 코스별, 부별 스케쥴을 생성합니다.
     *
     * @param scheduleList 저장할 스케쥴 객체리스트
     * @param golfField 골프장 객체
     * @param courseList 코스 리스트
     * @param startDate 시작날짜
     * @param endDate 종료날짜
     * @param startTime 시작시간
     * @param endTime 종료시간
     * @param teeOff 티오프
     * @param part 몇 부인지
     */
    private void createSchedulesByPart(List<Schedule> scheduleList, GolfField golfField, List<Course> courseList, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, String teeOff, int part) {
        List<LocalDate> localDateList = startDate.datesUntil(endDate.plusDays(1)).toList();
        int totalCnt = getStartTimeList(startTime, endTime, teeOff).size();

        for (Course course: courseList) {
            for (LocalDate oneDay: localDateList) {
                scheduleList.add(Schedule.builder()
                        .golfField(golfField)
                        .course(course)
                        .reservationAt(oneDay)
                        .startTime(startTime)
                        .endTime(endTime)
                        .teeOff(teeOff)
                        .part(part)
                        .dateStatus(DateStatus.NOTHING)
                        .totalCnt(totalCnt)
                        .blockedCnt(0).build());
            }
        }
    }

    /**
     * 주어진 조건 사이의 시간을 모두 찾아, 리스트로 반환합니다.
     *
     * @param startTime 시작시간
     * @param endTime 종료시간
     * @param teeOff 티오프 간격
     * @return 주어진 조건 사이의 시간 리스트
     */
    public List<LocalTime> getStartTimeList(LocalTime startTime, LocalTime endTime, String teeOff) {
        LocalTime currentStartTime = startTime;

        List<Integer> teeOffList = Arrays.stream(teeOff.split("~")).map(Integer::new).toList();
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
     * 메타데이터 조회: 캘린더에 표기되는 메타데이터를 반환합니다.
     *
     * @param golfFieldId 조회할 골프장 Id
     * @param year 대상 연도
     * @param month 대상 월
     * @return 메타데이터 반환 dto 리스트
     */
    public List<ReservationSheetDto.MetaDataResponse> getMetaData(Long golfFieldId, int year, int month) {
        LocalDate targetDate = LocalDate.of(year, month, 1);
        List<MetaDataReport> reports = scheduleRepository.countAllMetaDataByDate(targetDate, targetDate.plusMonths(1).minusDays(1), golfFieldId);

        List<ReservationSheetDto.MetaDataResponse> responseDtoList = new ArrayList<>();
        for (MetaDataReport report : reports) {
            int totalCntResult = report.getTotalCntSum();
            int blockedCntResult = report.getBlockedCntSum();
            int availableCntResult = totalCntResult - blockedCntResult;

            responseDtoList.add(ReservationSheetDto.MetaDataResponse.builder().
                    targetDate(report.getReservationAt()).
                    totalCntSum(totalCntResult).
                    blockedCntSum(blockedCntResult).
                    availableCntSum(report.getDateStatus() != DateStatus.NOTHING ? availableCntResult : 0).build());
        }

        return responseDtoList.stream().sorted(new DtoComparator()).toList();
    }

    /**
     * dto를 날짜 순으로 정렬하는 Comparator
     */
    private static class DtoComparator implements Comparator<ReservationSheetDto.MetaDataResponse> {
        @Override
        public int compare(ReservationSheetDto.MetaDataResponse dto1, ReservationSheetDto.MetaDataResponse dto2) {
            if (dto1.getTargetDate().isAfter(dto2.getTargetDate()))
                return 1;
            else if (dto1.getTargetDate().isBefore(dto2.getTargetDate()))
                return -1;
            else
                return 0;
        }
    }
}