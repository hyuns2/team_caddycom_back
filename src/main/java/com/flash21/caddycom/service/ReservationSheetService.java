package com.flash21.caddycom.service;

import com.flash21.caddycom.dto.ReservationSheetDto;
import com.flash21.caddycom.entity.ReservationDate;
import com.flash21.caddycom.entity.ReservationSheet;
import com.flash21.caddycom.repository.MetaDataReport;
import com.flash21.caddycom.repository.ReservationDateRepository;
import com.flash21.caddycom.repository.ReservationSheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationSheetService {
    final ReservationSheetRepository rsRepository;
    final ReservationDateRepository rdRepository;

    public void createReservationSheet(ReservationSheetDto.CreateRequestDto dto) {
        // Course 연결
        // 시간리스트 티오프리스트 사이즈 같은지 검증
        // 유효한 날짜와 시간인지 검증
        // 시트 아이디 전부 반환
        List<ReservationSheet> sheets = ReservationSheetDto.CreateRequestDto.toEntities(dto);
        for (ReservationSheet sheet: sheets) {
            ReservationSheet returnSheet = rsRepository.save(sheet);
            createReservationDate(returnSheet, dto.getStartDate(), dto.getEndDate());
        }
    }

    private void createReservationDate(ReservationSheet sheet, LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = startDate.datesUntil(endDate.plusDays(1)).toList();
        for (LocalDate oneDay: dates) {
            rdRepository.save(ReservationDate.builder().
                    reservationSheet(sheet).
                    reservationAt(oneDay).
                    status(false).
                    totalCnt(0).
                    blockedCnt(0).build());
        }
    }

    public List<ReservationSheetDto.MetaDataResponseDto> retrieveMetaData(LocalDate targetDate, List<Long> reservationSheetIdList) {
        List<ReservationSheetDto.MetaDataResponseDto> responseDtos = new ArrayList<>();
        List<MetaDataReport> reports = rdRepository.countAllMetaDataByDate(targetDate, targetDate.plusMonths(1), reservationSheetIdList);

        for (MetaDataReport report: reports) {
            int totalCntResult = report.getTotalCntSum();
            int blockedCntResult = report.getBlockedCntSum();
            int availableCntResult = totalCntResult - blockedCntResult;

            responseDtos.add(ReservationSheetDto.MetaDataResponseDto.builder().
                    targetDate(report.getReservationAt()).
                    totalCntSum(totalCntResult).
                    blockedCntSum(blockedCntResult).
                    availableCntSum(availableCntResult).build());
        }

        return responseDtos;
    }

}
