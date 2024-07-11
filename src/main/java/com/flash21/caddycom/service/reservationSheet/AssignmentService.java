package com.flash21.caddycom.service.reservationSheet;

import com.flash21.caddycom.dto.reservationSheet.AssignmentDto;
import com.flash21.caddycom.entity.reservationSheet.Assignment;
import com.flash21.caddycom.entity.reservationSheet.ReservationDate;
import com.flash21.caddycom.entity.reservationSheet.ReservationSheet;
import com.flash21.caddycom.global.exception.cException.CReservationDateNotFoundException;
import com.flash21.caddycom.global.exception.cException.CReservationSheetNotFoundException;
import com.flash21.caddycom.repository.reservationSheet.AssignmentJdbcRepository;
import com.flash21.caddycom.repository.reservationSheet.AssignmentRepository;
import com.flash21.caddycom.repository.reservationSheet.ReservationDateRepository;
import com.flash21.caddycom.repository.reservationSheet.ReservationSheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    final ReservationSheetRepository rsRepository;
    final ReservationDateRepository rdRepository;
    final AssignmentRepository assignmentRepository;
    final AssignmentJdbcRepository assignmentJdbcRepository;

    /**
     * 배정정보 조회: 배정정보가 존재하는 경우에는 반환하고, 존재하지 않는 경우에는 생성하여 반환합니다.
     *
     * @param reservationSheetId 대상 예약시트 Id
     * @param targetDate 대상 날짜
     * @return 배정정보 조회 dto 리스트
     *
     * @throws CReservationDateNotFoundException ReservationDate 객체가 존재하지 않을 경우
     * @throws CReservationSheetNotFoundException ReservationSheet 객체가 존재하지 않을 경우
     */
    @Transactional
    public List<AssignmentDto.AssignmentsResponse> getAssignments(Long reservationSheetId, LocalDate targetDate) {
        ReservationDate reservationDate = rdRepository.findByReservationSheetIdAndReservationAt(reservationSheetId, targetDate)
                .orElseThrow(CReservationDateNotFoundException::new);

        if (!reservationDate.getIsAssigned()) {
            ReservationSheet reservationSheet = rsRepository.findById(reservationSheetId)
                    .orElseThrow(CReservationSheetNotFoundException::new);
            createAndGetAssignments(reservationSheet, reservationDate);
        }

        return findAndGetAssignments(reservationDate);
    }

    /**
     * 배정정보 조회 내부함수1: 배정정보를 조회하여 반환합니다.
     *
     * @param reservationDate 예약시트 Id와 조회한 날짜에 해당하는 reservationDate 객체
     * @return 배정정보 조회 dto 리스트
     */
    private List<AssignmentDto.AssignmentsResponse> findAndGetAssignments(ReservationDate reservationDate) {
        List<AssignmentDto.AssignmentsResponse> responseDtoList = new ArrayList<>();
        List<Assignment> assignmentList = assignmentRepository.findAllByReservationDateId(reservationDate.getId());

        for (Assignment assignment : assignmentList)
            responseDtoList.add(toDto(assignment));

        return responseDtoList;
    }

    /**
     * 배정정보 조회 내부함수2: 배정정보를 생성합니다.
     *
     * @param reservationSheet 대상 reservationSheet 객체
     * @param reservationDate 대상 reservationDate 객체
     */
    private void createAndGetAssignments(ReservationSheet reservationSheet, ReservationDate reservationDate) {
        LocalTime startAtLocalTime = reservationSheet.getStartAt().toLocalTime();
        LocalTime endAtLocalTIme = reservationSheet.getEndAt().toLocalTime();

        List<Integer> teeOffList = Arrays.stream(reservationSheet.getTeeOff().split("~")).
                map(Integer::new).toList();
        int teeOffListSize = teeOffList.size();
        int currentTeeOffIndex = 0;

        List<LocalTime> startTimeList = new ArrayList<>();
        while (startAtLocalTime.isBefore(endAtLocalTIme)) {
            startTimeList.add(startAtLocalTime);

            startAtLocalTime = startAtLocalTime.plusMinutes(teeOffList.get(currentTeeOffIndex++));
            if (currentTeeOffIndex >= teeOffListSize)
                currentTeeOffIndex = 0;
        }
        assignmentJdbcRepository.saveAll(reservationDate.getId(), startTimeList);

        reservationDate.setIsAssigned();
        reservationDate.setTotalCnt(startTimeList.size());
        reservationDate.setBlockedCnt(startTimeList.size());
        rdRepository.save(reservationDate);
    }

    /**
     * Assignment 객체를 배정정보 조회 dto로 변환합니다.
     *
     * @param assignment Assignment 객체
     * @return 배정정보 조회 dto
     */
    private AssignmentDto.AssignmentsResponse toDto(Assignment assignment) {
        return AssignmentDto.AssignmentsResponse.builder().
                id(assignment.getId()).
                startTime(assignment.getStartTime()).
                status(assignment.getStatus()).
                // caddyId(assignment.getCaddyId()).
                caddyName(assignment.getCaddyName()).
                reason(assignment.getReason()).build();
    }
}
