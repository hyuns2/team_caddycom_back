package com.flash21.caddycom.service.reservationSheet;

<<<<<<< HEAD:src/main/java/com/flash21/caddycom/service/ReservationSheetService.java
import com.flash21.caddycom.dto.ReservationSheetDto;
import com.flash21.caddycom.entity.golfFieldDetail.Course;
import com.flash21.caddycom.entity.ReservationDate;
import com.flash21.caddycom.entity.ReservationSheet;
=======
import com.flash21.caddycom.dto.reservationSheet.ReservationSheetDto;
import com.flash21.caddycom.entity.Course;
import com.flash21.caddycom.entity.reservationSheet.ReservationDate;
import com.flash21.caddycom.entity.reservationSheet.ReservationSheet;
>>>>>>> develop:src/main/java/com/flash21/caddycom/service/reservationSheet/ReservationSheetService.java
import com.flash21.caddycom.global.exception.cException.CCourseNotFoundException;
import com.flash21.caddycom.global.exception.cException.CInvalidPartInfoException;
import com.flash21.caddycom.repository.CourseRepository;
import com.flash21.caddycom.repository.reservationSheet.MetaDataReport;
import com.flash21.caddycom.repository.reservationSheet.ReservationDateRepository;
import com.flash21.caddycom.repository.reservationSheet.ReservationSheetRepository;
import lombok.RequiredArgsConstructor;
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

    public List<Long> createReservationSheet(ReservationSheetDto.CreateRequestDto dto) {
        validToCreateReservationSheet(dto);

        List<Course> courseList = courseRepository.findAllById(dto.getCourseList());
        if (courseList.isEmpty())
            throw new CCourseNotFoundException();
        List<ReservationSheet> sheets = ReservationSheetDto.CreateRequestDto.toEntities(dto, courseList);
        List<Long> returnSheetIdList = new ArrayList<>();

        for (ReservationSheet sheet: sheets) {
            ReservationSheet returnSheet = rsRepository.save(sheet);
            returnSheetIdList.add(returnSheet.getId());
            createReservationDate(returnSheet, dto.getStartDate(), dto.getEndDate());
        }

        return returnSheetIdList;
    }

    private void validToCreateReservationSheet(ReservationSheetDto.CreateRequestDto dto) {
        if (dto.getTeeOffList().size() != dto.getStartTimeList().size() ||
                dto.getStartTimeList().size() != dto.getEndTimeList().size())
            throw new CInvalidPartInfoException();
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

    public List<ReservationSheetDto.MetaDataResponseDto> retrieveMetaData(int year, int month, List<Long> reservationSheetIdList) {
        LocalDate targetDate = LocalDate.of(year, month, 1);
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
