package com.flash21.caddycom.service.reservationSheet;

import com.flash21.caddycom.dto.reservationSheet.AssignmentDto;
import com.flash21.caddycom.entity.reservationSheet.Assignment;
import com.flash21.caddycom.entity.reservationSheet.AssignmentStatus;
import com.flash21.caddycom.entity.reservationSheet.ReservationDate;
import com.flash21.caddycom.entity.reservationSheet.ReservationSheet;
import com.flash21.caddycom.global.exception.cException.CReservationDateNotFoundException;
import com.flash21.caddycom.global.exception.cException.CReservationSheetNotFoundException;
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
@Transactional
public class AssignmentService {
    final ReservationSheetRepository rsRepository;
    final ReservationDateRepository rdRepository;
    final AssignmentRepository assignmentRepository;

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
    public List<AssignmentDto.AssignmentsResponse> getAssignments(Long reservationSheetId, LocalDate targetDate) {
        ReservationDate reservationDate = rdRepository.findByReservationSheetIdAndReservationAt(reservationSheetId, targetDate)
                .orElseThrow(CReservationDateNotFoundException::new);

        if (reservationDate.getIsAssigned()) {
            return findAndGetAssignments(reservationDate);
        }

        ReservationSheet reservationSheet = rsRepository.findById(reservationSheetId)
                .orElseThrow(CReservationSheetNotFoundException::new);
        return createAndGetAssignments(reservationSheet, reservationDate);
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
     * 배정정보 조회 내부함수2: 배정정보를 생성하여 반환합니다.
     *
     * @param reservationSheet 대상 reservationSheet 객체
     * @param reservationDate 대상 reservationDate 객체
     * @return 배정정보 조회 dto 리스트
     */
    private List<AssignmentDto.AssignmentsResponse> createAndGetAssignments(ReservationSheet reservationSheet, ReservationDate reservationDate) {
        List<AssignmentDto.AssignmentsResponse> responseDtoList = new ArrayList<>();
        LocalTime startAtLocalTime = reservationSheet.getStartAt().toLocalTime();
        LocalTime endAtLocalTIme = reservationSheet.getEndAt().toLocalTime();

        List<Integer> teeOffList = Arrays.stream(reservationSheet.getTeeOff().split("~")).
                map(Integer::new).toList();
        int teeOffListSize = teeOffList.size();
        int currentTeeOffIndex = 0;

        while (startAtLocalTime.isBefore(endAtLocalTIme)) {
            Assignment assignment = assignmentRepository.save(Assignment.builder().
                    reservationDate(reservationDate).
                    startTime(startAtLocalTime).
                    status(AssignmentStatus.NOTHING).
                    // caddyId().
                    caddyName(null).
                    reason(null).build());
            responseDtoList.add(toDto(assignment));

            startAtLocalTime = startAtLocalTime.plusMinutes(teeOffList.get(currentTeeOffIndex++));
            if (currentTeeOffIndex >= teeOffListSize)
                currentTeeOffIndex = 0;
        }

        reservationDate.setIsAssigned();
        reservationDate.setTotalCnt(responseDtoList.size());
        rdRepository.save(reservationDate);
        return responseDtoList;
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
                reservationDate(assignment.getReservationDate().getReservationAt()).
                startTime(assignment.getStartTime()).
                status(assignment.getStatus()).
                // caddyId(assignment.getCaddyId()).
                caddyName(assignment.getCaddyName()).
                reason(assignment.getReason()).build();
    }
}
