package com.flash21.caddycom.service;

import com.flash21.caddycom.dto.ReservationSheetDto;
import com.flash21.caddycom.entity.Course;
import com.flash21.caddycom.entity.ReservationDate;
import com.flash21.caddycom.entity.ReservationSheet;
import com.flash21.caddycom.global.exception.cException.CInvalidCourseException;
import com.flash21.caddycom.global.exception.cException.CInvalidDateOrderException;
import com.flash21.caddycom.global.exception.cException.CInvalidPartInfoException;
import com.flash21.caddycom.global.exception.cException.CInvalidTimeOrderException;
import com.flash21.caddycom.repository.CourseRepository;
import com.flash21.caddycom.repository.MetaDataReport;
import com.flash21.caddycom.repository.ReservationDateRepository;
import com.flash21.caddycom.repository.ReservationSheetRepository;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationSheetService {
    final ReservationSheetRepository rsRepository;
    final ReservationDateRepository rdRepository;
    final CourseRepository courseRepository;

    // 데이터 검증 - 날짜시간(입력, 순서)
    public List<Long> createReservationSheet(ReservationSheetDto.CreateRequestDto dto) {

        List<Course> courseList = courseRepository.findAllById(dto.getCourseList());
        if (courseList.isEmpty())
            throw new CInvalidCourseException();
        List<ReservationSheet> sheets = ReservationSheetDto.CreateRequestDto.toEntities(dto, courseList);
        List<Long> returnSheetIdList = new ArrayList<>();

        for (ReservationSheet sheet: sheets) {
            ReservationSheet returnSheet = rsRepository.save(sheet);
            returnSheetIdList.add(returnSheet.getId());
            createReservationDate(returnSheet, dto.getStartDate(), dto.getEndDate());
        }

        return returnSheetIdList;
    }

    private void createReservationDate(ReservationSheet sheet, LocalDate startDate, LocalDate endDate) {
        List<ReservationDate> reservationDates = new ArrayList<>();
        List<LocalDate> localDates = startDate.datesUntil(endDate.plusDays(1)).toList();

        for (LocalDate oneDay: localDates) {
            reservationDates.add(ReservationDate.builder().
                    reservationSheet(sheet).
                    reservationAt(oneDay).
                    status(false).
                    totalCnt(0).
                    blockedCnt(0).build());
        }
        rdRepository.saveAll(reservationDates);
    }

    public List<ReservationSheetDto.MetaDataResponseDto> retrieveMetaData(LocalDate targetDate, List<Long> reservationSheetIdList) {
        List<ReservationSheetDto.MetaDataResponseDto> responseDtoList = new ArrayList<>();
        List<MetaDataReport> reports = rdRepository.countAllMetaDataByDate(targetDate, targetDate.plusMonths(1), reservationSheetIdList);

        for (MetaDataReport report: reports) {
            int totalCntResult = report.getTotalCntSum();
            int blockedCntResult = report.getBlockedCntSum();
            int availableCntResult = totalCntResult - blockedCntResult;

            responseDtoList.add(ReservationSheetDto.MetaDataResponseDto.builder().
                    targetDate(report.getReservationAt()).
                    totalCntSum(totalCntResult).
                    blockedCntSum(blockedCntResult).
                    availableCntSum(availableCntResult).build());
        }

        return responseDtoList;
    }

}
