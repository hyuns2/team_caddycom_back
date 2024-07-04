package com.flash21.caddycom.service;

import com.flash21.caddycom.dto.ReservationSheetDto;
import com.flash21.caddycom.entity.ReservationDate;
import com.flash21.caddycom.entity.ReservationSheet;
import com.flash21.caddycom.repository.ReservationDateRepository;
import com.flash21.caddycom.repository.ReservationSheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationSheetService {
    final ReservationSheetRepository rsRepository;
    final ReservationDateRepository rdRepository;

    public void createReservationSheet(ReservationSheetDto.CreateRequestDto dto) {
        // Course 연결
        // 시간리스트 티오프리스트 사이즈 같은지 검증
        // 유효한 날짜와 시간인지 검증
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
                    reservationDate(oneDay).
                    status(false).
                    totalCnt(0).
                    blockedCnt(0).build());
        }
    }

}
