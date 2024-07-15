package com.flash21.caddycom.service.reservationSheet;

import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.dto.reservationSheet.ReservationSheetDto;
import com.flash21.caddycom.entity.reservationSheet.ReservationDate;
import com.flash21.caddycom.entity.reservationSheet.ReservationSheet;
import com.flash21.caddycom.entity.reservationSheet.ReservationSheetInfo;
import com.flash21.caddycom.global.exception.cException.CCourseNotFoundException;
import com.flash21.caddycom.global.exception.cException.CInvalidPartInfoException;
import com.flash21.caddycom.repository.golfFieldDetail.course.CourseRepository;
import com.flash21.caddycom.repository.reservationSheet.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationSheetService {
    final ReservationSheetInfoRepository rsInfoRepository;
    final ReservationSheetRepository rsRepository;
    final ReservationDateRepository rdRepository;
    final CourseRepository courseRepository;
    final ReservationDateJdbcRepository rsJdbcRepository;

    /**
     * 예약시트 생성: 예약시트를 생성합니다.
     *
     * @param dto 예약시트 생성요청 dto
     * @return 생성된 예약시트정보 Id
     *
     * @throws CCourseNotFoundException Course 객체가 존재하지 않을 경우
     */
    @Transactional
    public Long createReservationSheet(ReservationSheetDto.CreateRequest dto) {
        validToCreateReservationSheet(dto);

        List<Course> courseList = courseRepository.findAllById(dto.getCourseList());
        if (courseList.isEmpty())
            throw new CCourseNotFoundException();

        ReservationSheetInfo reservationSheetInfo = rsInfoRepository.save(ReservationSheetInfo.builder().
                startAt(dto.getStartDate()).
                endAt(dto.getEndDate()).build());
        List<ReservationSheet> sheets = ReservationSheetDto.CreateRequest.toEntities(dto, reservationSheetInfo, courseList);

        for (ReservationSheet sheet: sheets) {
            ReservationSheet returnSheet = rsRepository.save(sheet);
            createReservationDate(returnSheet, dto.getStartDate(), dto.getEndDate());
        }

        return reservationSheetInfo.getId();
    }

    /**
     * 예약시트 생성 내부함수1: 예약시트 생성요청 dto를 검증합니다.
     *
     * @param dto 예약시트 생성요청 dto
     *
     * @throws CInvalidPartInfoException 부(파트)에 대한 일부 정보가 빠진 경우
     */
    private void validToCreateReservationSheet(ReservationSheetDto.CreateRequest dto) {
        if (dto.getTeeOffList().size() != dto.getStartTimeList().size() ||
                dto.getStartTimeList().size() != dto.getEndTimeList().size())
            throw new CInvalidPartInfoException();
    }

    /**
     * 예약시트 생성 내부함수2: 예약시트를 생성합니다.
     *
     * @param sheet ReservationSheet 객체
     * @param startDate 시작날짜
     * @param endDate 종료날짜
     */
    private void createReservationDate(ReservationSheet sheet, LocalDate startDate, LocalDate endDate) {
        List<ReservationDate> reservationDates = new ArrayList<>();
        List<LocalDate> localDates = startDate.datesUntil(endDate.plusDays(1)).toList();

        for (LocalDate oneDay: localDates) {
            reservationDates.add(ReservationDate.builder().
                    reservationSheet(sheet).
                    reservationAt(oneDay).
                    isAssigned(false).
                    totalCnt(0).
                    blockedCnt(0).build());
        }
        rsJdbcRepository.saveAll(reservationDates);
    }

    /**
     * 메타데이터 조회: 캘린더에 표기되는 메타데이터를 반환합니다.
     *
     * @param reservationSheetInfoId 조회할 예약시트정보 Id
     * @param year 대상 연도
     * @param month 대상 월
     * @return 메타데이터 반환 dto 리스트
     */
    public List<ReservationSheetDto.MetaDataResponse> getMetaData(Long reservationSheetInfoId, int year, int month) {
        LocalDate targetDate = LocalDate.of(year, month, 1);
        List<MetaDataReport> reports = rdRepository.countAllMetaDataByDate(targetDate, targetDate.plusMonths(1), reservationSheetInfoId);

        List<ReservationSheetDto.MetaDataResponse> responseDtoList = new ArrayList<>();
        for (MetaDataReport report: reports) {
            int totalCntResult = report.getTotalCntSum();
            int blockedCntResult = report.getBlockedCntSum();
            int availableCntResult = totalCntResult - blockedCntResult;

            responseDtoList.add(ReservationSheetDto.MetaDataResponse.builder().
                    targetDate(report.getReservationAt()).
                    totalCntSum(totalCntResult).
                    blockedCntSum(blockedCntResult).
                    availableCntSum(availableCntResult).build());
        }

        return responseDtoList;
    }
}