package com.flash21.caddycom.service;

import com.flash21.caddycom.dto.ReservationSheetDto;
import com.flash21.caddycom.entity.ReservationDate;
import com.flash21.caddycom.entity.ReservationSheet;
import com.flash21.caddycom.repository.MetaDataReport;
import com.flash21.caddycom.repository.ReservationDateRepository;
import com.flash21.caddycom.repository.ReservationSheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationSheetService {
    final ReservationSheetRepository rsRepository;
    final ReservationDateRepository rdRepository;

    public List<Long> createReservationSheet(ReservationSheetDto.CreateRequestDto dto) {
        // Course 연결
        // 시간리스트 티오프리스트 사이즈 같은지 검증
        // 유효한 날짜와 시간인지 검증
        List<ReservationSheet> sheets = ReservationSheetDto.CreateRequestDto.toEntities(dto);
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
